package me.Logicism.LogSM.gui;

import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.core.ServerThread;
import me.Logicism.LogSM.core.ServerType;
import me.Logicism.LogSM.core.ServerUsageThread;
import me.Logicism.LogSM.network.NetworkClient;
import me.Logicism.LogSM.network.UPnPManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class ConfigServerPage extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JList list1;
    private JTextField textField1;
    private JTextArea consoleTextPane;
    private JLabel ramLabel;
    private JLabel cpuUsageLabel;
    private JButton OFFLINEButton;
    private JTextArea configTextArea;
    private JButton saveConfigurationButton;
    private JButton openServerFilesButton;
    private JButton downloadServerSoftwareButton;
    private JSpinner TCPServerPortSpinner;
    private JButton checkPortForwardButton;
    private JTextField serverArgsTextField;
    private JSpinner allocatedRAMSpinner;
    private JLabel externalIPLabel;
    private JLabel localIPLabel;
    private JButton saveServerPropertiesButton;
    private JCheckBox UPnPPortForwardCheckBox;
    private JSpinner UDPServerPortSpinner;
    private JTextField javaHomeTextField;
    private JCheckBox autoRestartInSecondsCheckBox;
    private JSpinner autoRestartSpinner;
    private GraphView cpuGraph;
    private GraphView ramGraph;
    private GraphView playerGraph;
    private JLabel uptimeLabel;
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;

    private Process p;
    private BufferedWriter dos;
    private DefaultListModel<String> dlm;

    private UPnPManager manager;

    private File locked;
    private FileOutputStream fos;
    private FileLock fileLock;

    public ConfigServerPage(Server server) {
        super(server.getName() + " - " + server.getServerType().name() + " - LogServerMaker v" + LogSM.VERSION);

        scrollPane1.putClientProperty("JScrollPane.smoothScrolling", true);
        scrollPane2.putClientProperty("JScrollPane.smoothScrolling", true);
        tabbedPane1.putClientProperty("JTabbedPane.tabType", "card");
        tabbedPane1.putClientProperty("JTabbedPane.tabWidthMode", "equal");
        tabbedPane1.putClientProperty("JTabbedPane.showTabSeparators", true);
        tabbedPane1.putClientProperty("JTabbedPane.showContentSeparator", true);

        try {
            setIconImage(ImageIO.read(LogSM.class.getClassLoader().getResourceAsStream("icon.png")));
        } catch (IOException ignored) {

        }

        try {
            locked = new File("servers/" + server.getDirName() + "/logsm.lock");
            fos = new FileOutputStream(locked);
            fileLock = fos.getChannel().lock();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't create server! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

            dispose();
        }
        dlm = new DefaultListModel<>();
        list1.setModel(dlm);
        list1.setCellRenderer(new PlayerListRenderer(server, false));
        list1.setPreferredSize(new Dimension(315, 660));
        list1.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (p != null && p.isAlive()) {
                    int i = JOptionPane.showConfirmDialog(null, "You have a server running! Are you sure you want to kill the server process?", "LogSM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (i == JOptionPane.YES_OPTION) {
                        p.destroy();
                        p = null;

                        try {
                            fileLock.release();
                            fos.close();
                            locked.delete();
                        } catch (IOException ioException) {

                        }

                        dispose();
                    }
                } else {
                    try {
                        fileLock.release();
                        fos.close();
                        locked.delete();
                    } catch (IOException ioException) {

                    }

                    dispose();
                }
            }
        });
        setContentPane(panel1);
        setPreferredSize(new Dimension(900, 800));

        externalIPLabel.setFont(externalIPLabel.getFont().deriveFont(16f));
        externalIPLabel.setText(LogSM.getExternalIP());

        localIPLabel.setFont(localIPLabel.getFont().deriveFont(16f));
        try {
            localIPLabel.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            localIPLabel.setText(InetAddress.getLoopbackAddress().getHostAddress());
        }

        manager = new UPnPManager();

        TCPServerPortSpinner.setModel(new SpinnerNumberModel(25565, 1, 65535, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(TCPServerPortSpinner, "#");
        TCPServerPortSpinner.setEditor(editor);
        UDPServerPortSpinner.setModel(new SpinnerNumberModel(19132, 1, 65535, 1));
        editor = new JSpinner.NumberEditor(UDPServerPortSpinner, "#");
        UDPServerPortSpinner.setEditor(editor);
        allocatedRAMSpinner.setModel(new SpinnerNumberModel(1024, 512, 131072, 256));
        editor = new JSpinner.NumberEditor(allocatedRAMSpinner, "#");
        allocatedRAMSpinner.setEditor(editor);
        autoRestartSpinner.setModel(new SpinnerNumberModel(60, 60, 86400, 1));
        editor = new JSpinner.NumberEditor(autoRestartSpinner, "#");
        autoRestartSpinner.setEditor(editor);

        allocatedRAMSpinner.setValue(server.getRAM());
        if (server.getServerArgs() != null) {
            serverArgsTextField.setText(server.getServerArgs());
        }
        javaHomeTextField.setText(server.getJavaHome());
        if (server.getAutoRestart() >= 60) {
            autoRestartInSecondsCheckBox.setSelected(true);
            autoRestartSpinner.setEnabled(true);
            autoRestartSpinner.setValue(server.getAutoRestart());
        }
        if (server.isUpnpEnabled()) {
            UPnPPortForwardCheckBox.setSelected(true);
        }

        if (server.getServerType().equals(ServerType.Geyser) || server.getServerType().equals(ServerType.WaterdogPE)) {
            UDPServerPortSpinner.setEnabled(true);
        } else {
            TCPServerPortSpinner.setEnabled(true);
        }

        loadConfiguration(server);

        openServerFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(new File("servers/" + server.getDirName()));
                    }
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Can't open server files! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        ActionMap am = textField1.getActionMap();
        textField1.getInputMap().put(enterStroke, enterStroke.toString());
        am.put(enterStroke.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField1.getText().isEmpty()) {
                    try {
                        dos.write(textField1.getText() + "\r");
                        dos.flush();
                    } catch (IOException ioException) {

                    }

                    textField1.setText("");
                }
            }
        });

        checkPortForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (p != null) {
                    if (server.getServerType().equals(ServerType.Geyser) || server.getServerType().equals(ServerType.WaterdogPE)) {
                        if (NetworkClient.checkUDPServer(LogSM.getExternalIP(), Long.valueOf(String.valueOf(UDPServerPortSpinner.getValue())).intValue())) {
                            JOptionPane.showMessageDialog(null, "Your server is accessible to the internet!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Your server isn't accessible to the internet! Follow instructions online how to port forward.", "LogSM", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        if (NetworkClient.checkTCPServer(LogSM.getExternalIP(), Long.valueOf(String.valueOf(TCPServerPortSpinner.getValue())).intValue())) {
                            JOptionPane.showMessageDialog(null, "Your server is accessible to the internet!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Your server isn't accessible to the internet! Follow instructions online how to port forward.", "LogSM", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You must start your server before we can check if it is port forwarded!", "LogSM", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        OFFLINEButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem startServer = new JMenuItem("Start Server");
                JMenuItem stopServer = new JMenuItem("Stop Server");
                JMenuItem restartServer = new JMenuItem("Restart Server");
                JMenuItem killServer = new JMenuItem("Kill Server");

                startServer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!consoleTextPane.getText().isEmpty()) {
                            consoleTextPane.setText("");
                        }

                        File serverJar = new File("servers/" + server.getDirName() + "/server.jar");
                        if (!serverJar.exists()) {
                            JOptionPane.showMessageDialog(null, "You must download the server software before you can run the server.", "LogSM", JOptionPane.ERROR_MESSAGE);
                            return;
                        } else {
                            File eula = new File("servers/" + server.getDirName() + "/eula.txt");
                            if (!eula.exists()) {
                                if (JOptionPane.showConfirmDialog(null, "Do you agree to the Minecraft EULA? (https://www.minecraft.net/en-us/eula)", "LogSM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                                    if (!server.getServerType().equals(ServerType.Nukkit)) {
                                        FileWriter fw;
                                        try {
                                            fw = new FileWriter(eula);
                                            fw.write("eula=true");
                                            fw.close();
                                        } catch (IOException ioException) {
                                            JOptionPane.showMessageDialog(null, "Can't save eula.txt! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

                                            return;
                                        }
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "You must agree to the EULA to run the server.", "LogSM", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }

                            startServer(server);
                        }
                    }
                });

                stopServer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        OFFLINEButton.setText("STOPPING");

                        try {
                            if (server.getServerType().equals(ServerType.Geyser)) {
                                dos.write("geyser stop");
                            } else {
                                dos.write("end");
                            }
                            dos.flush();
                            dos.close();
                        } catch (IOException ioException) {

                        }

                        dlm.clear();
                    }
                });

                restartServer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        restartServer(server);
                    }
                });

                killServer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        p.destroy();
                        p = null;

                        consoleTextPane.setText("");
                        OFFLINEButton.setText("OFFLINE");

                        dlm.clear();
                    }
                });

                popupMenu.add(startServer);
                popupMenu.add(stopServer);
                popupMenu.add(restartServer);
                popupMenu.add(killServer);

                if (OFFLINEButton.getText().equals("ONLINE")) {
                    stopServer.setEnabled(true);
                    restartServer.setEnabled(true);
                    killServer.setEnabled(true);
                    startServer.setEnabled(false);
                } else if (OFFLINEButton.getText().equals("STARTING") || OFFLINEButton.getText().equals("STOPPING") || OFFLINEButton.getText().equals("RESTARTING") || OFFLINEButton.getText().equals("AUTO-RESTARTING")) {
                    stopServer.setEnabled(false);
                    restartServer.setEnabled(false);
                    killServer.setEnabled(false);
                    startServer.setEnabled(false);
                } else {
                    stopServer.setEnabled(false);
                    restartServer.setEnabled(false);
                    killServer.setEnabled(false);
                    startServer.setEnabled(true);
                }

                popupMenu.show(OFFLINEButton, e.getX(), e.getY());
            }
        });

        downloadServerSoftwareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdaterDialog updaterDialog = switch (server.getServerType()) {
                    case Geyser -> new UpdaterDialog(server, NetworkClient.getGeyserURL(), null);
                    case BungeeCord -> new UpdaterDialog(server, NetworkClient.getBungeeCordURL(), null);
                    case NanoLimbo -> new UpdaterDialog(server, NetworkClient.getNanoLimboURL(), null);
                    case Velocity -> new UpdaterDialog(server, NetworkClient.getVelocityURL(), null);
                    case WaterdogPE -> new UpdaterDialog(server, NetworkClient.getWaterdogPEURL(), null);
                    default -> null;
                };
                updaterDialog.pack();
                updaterDialog.setLocationRelativeTo(null);
                updaterDialog.setVisible(true);
            }
        });

        autoRestartInSecondsCheckBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoRestartSpinner.setEnabled(autoRestartInSecondsCheckBox.isSelected());
            }
        });

        saveConfigurationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file;
                if (server.getServerType().equals(ServerType.Limbo)) {
                    file = new File("servers/" + server.getDirName() + "/server.properties");
                } else if (server.getServerType().equals(ServerType.Velocity)) {
                    file = new File("servers/" + server.getDirName() + "/velocity.toml");
                } else if (server.getServerType().equals(ServerType.NanoLimbo)) {
                    file = new File("servers/" + server.getDirName() + "/settings.yml");
                } else {
                    file = new File("servers/" + server.getDirName() + "/config.yml");
                }

                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(configTextArea.getText());
                    fw.flush();
                    fw.close();

                    JOptionPane.showMessageDialog(null, "Saved Configuration! If your server is running, you must restart to apply the changes.", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Can't Save Configuration! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveServerPropertiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    server.setRAM((int) allocatedRAMSpinner.getValue());
                    server.setServerArgs(serverArgsTextField.getText());
                    server.setJavaHome(javaHomeTextField.getText());
                    if (autoRestartInSecondsCheckBox.isSelected()) {
                        server.setAutoRestart((Integer) autoRestartSpinner.getValue());
                    } else {
                        server.setAutoRestart(-1);
                    }
                    server.setUpnpEnabled(UPnPPortForwardCheckBox.isSelected());
                    server.save();

                    ramGraph.setMaxScore(server.getRAM());

                    JOptionPane.showMessageDialog(null, "Saved Server Properties! Please restart your server (if running) to apply the changes!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Can't Save Server Properties! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        externalIPLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StringSelection selection = new StringSelection(externalIPLabel.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                JOptionPane.showMessageDialog(null, "Copied the External IP to the Clipboard!", "LogSM",  JOptionPane.INFORMATION_MESSAGE);
            }
        });

        localIPLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StringSelection selection = new StringSelection(localIPLabel.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                JOptionPane.showMessageDialog(null, "Copied the Local IP to the Clipboard!", "LogSM",  JOptionPane.INFORMATION_MESSAGE);
            }
        });

        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            integers.add(0);
        }

        cpuGraph.setXLabel("CPU Usage: 0%");
        cpuGraph.setValues(integers);

        ramGraph.setXLabel("RAM Usage: 0 MB");
        ramGraph.setMaxScore(server.getRAM());
        ramGraph.setLineColor(new Color(60, 230, 44, 180));
        ramGraph.setValues(integers);

        playerGraph.setValues(integers);
        playerGraph.setLineColor(new Color(230, 44, 44, 180));
        playerGraph.setXLabel("Online Players: 0");
        playerGraph.setMaxScore(20);

        setMinimumSize(getPreferredSize());
    }

    public void loadConfiguration(Server server) {
        configTextArea.setText("");

        File file;
        if (server.getServerType().equals(ServerType.Velocity)) {
            file = new File("servers/" + server.getDirName() + "/velocity.toml");
        } else if (server.getServerType().equals(ServerType.NanoLimbo)) {
            file = new File("servers/" + server.getDirName() + "/settings.yml");
        } else {
            file = new File("servers/" + server.getDirName() + "/config.yml");
        }

        if (file.exists()) {
            try {
                for (String line : Files.readAllLines(file.toPath())) {
                    configTextArea.append(line + "\n");
                }

                configTextArea.setCaretPosition(0);
            } catch (IOException ioException) {

            }
        }
    }

    public void startServer(Server server) {
        List<String> commands = new ArrayList<>();
        if (server.getJavaHome() == null || server.getJavaHome().isEmpty()) {
            commands.add("java");
        } else {
            commands.add(server.getJavaHome() + "/java");
        }
        if (!serverArgsTextField.getText().isEmpty()) {
            commands.addAll(Arrays.asList(serverArgsTextField.getText().split(" ")));
        }
        commands.add("-Xmx" + server.getRAM() + "M");
        commands.add("-Xms" + server.getRAM() + "M");
        commands.add("-jar");
        commands.add(new File("servers/" + server.getDirName() + "/server.jar").getAbsolutePath());
        commands.add("nogui");
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        try {
            p = processBuilder.directory(new File("servers/" + server.getDirName())).redirectErrorStream(true).start();
            dos = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            Executors.newSingleThreadExecutor().submit(new ServerThread(this, server, p, OFFLINEButton, consoleTextPane, dlm));
            Executors.newSingleThreadExecutor().submit(new ServerUsageThread(server, p, cpuUsageLabel, cpuGraph, ramLabel, ramGraph, dlm, playerGraph, uptimeLabel, OFFLINEButton));
            ProcessHandle pH = p.toHandle();
            pH.onExit().thenAccept(pH_ -> {
                if (OFFLINEButton.getText().equals("RESTARTING") || OFFLINEButton.getText().equals("AUTO-RESTARTING")) {
                    consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                    list1.removeAll();
                    p = null;
                    try {
                        dos.close();
                    } catch (IOException ioException) {

                    }
                    dos = null;
                    loadConfiguration(server);

                    startServer(server);
                } else if (OFFLINEButton.getText().equals("STOPPING")) {
                    consoleTextPane.append("\n" + "Server Stopped or Crashed");
                    consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                    list1.removeAll();
                    p = null;
                    try {
                        dos.close();
                    } catch (IOException ioException) {

                    }
                    if (UPnPPortForwardCheckBox.isSelected()) {
                        if (TCPServerPortSpinner.isEnabled()) {
                            manager.closeTCPPort(server, localIPLabel.getText(), Integer.parseInt(String.valueOf(TCPServerPortSpinner.getValue())));
                        }
                        if (UDPServerPortSpinner.isEnabled()) {
                            manager.closeUDPPort(server, localIPLabel.getText(), Integer.parseInt(String.valueOf(UDPServerPortSpinner.getValue())));
                        }
                    }
                    dos = null;
                    loadConfiguration(server);
                }
            });
            if (UPnPPortForwardCheckBox.isSelected()) {
                if (TCPServerPortSpinner.isEnabled()) {
                    if (!manager.isTCPOpen()) {
                        manager.openTCPPort(server, localIPLabel.getText(), Integer.parseInt(String.valueOf(TCPServerPortSpinner.getValue())));
                    }
                }
                if (UDPServerPortSpinner.isEnabled()) {
                    if (!manager.isUDPOpen()) {
                        manager.openUDPPort(server, localIPLabel.getText(), Integer.parseInt(String.valueOf(UDPServerPortSpinner.getValue())));
                    }
                }
            }

            if (!OFFLINEButton.getText().equals("RESTARTING")) {
                OFFLINEButton.setText("STARTING");
            }
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "Can't start " + server.getServerType().name() + " Server! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void restartServer(Server server) {
        OFFLINEButton.setText("RESTARTING");

        try {
            if (server.getServerType().equals(ServerType.Geyser)) {
                dos.write("geyser stop");
            } else {
                dos.write("end");
            }
            dos.flush();
            dos.close();
        } catch (IOException ioException) {

        }

        dlm.clear();
    }

}
