package me.Logicism.LogSM.gui;

import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.core.ServerThread;
import me.Logicism.LogSM.core.ServerType;
import me.Logicism.LogSM.core.ServerUsageThread;
import me.Logicism.LogSM.network.BedrockDownload;
import me.Logicism.LogSM.network.NetworkClient;
import me.Logicism.LogSM.network.UPnPManager;
import oshi.SystemInfo;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BedrockServerPage extends JFrame {

    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JList list1;
    private JTextField textField1;
    private JTextArea consoleTextPane;
    private JLabel ramLabel;
    private JLabel cpuUsageLabel;
    private JButton OFFLINEButton;
    private JTextField serverNameTextField;
    private JComboBox gamemodeComboBox;
    private JComboBox difficultyComboBox;
    private JTextField levelNameTextField;
    private JTextField levelSeedTextField;
    private JCheckBox allowCheatsCheckBox;
    private JSpinner viewDistanceSpinner;
    private JSpinner tickDistanceSpinner;
    private JSpinner ipv4PortSpinner;
    private JSpinner ipv6PortSpinner;
    private JLabel externalIPLabel;
    private JLabel localIPLabel;
    private JSpinner maxPlayersSpinner;
    private JCheckBox onlineModeCheckBox;
    private JCheckBox whitelistEnabledCheckBox;
    private JSpinner idleTimeoutKickSpinner;
    private JComboBox defaultPlayerPermLevelComboBox;
    private JCheckBox requireTexturePackCheckBox;
    private JButton checkPortForwardButton;
    private JSpinner maxThreadsSpinner;
    private JCheckBox contentLogFileEnabledCheckBox;
    private JSpinner compressionThresholdSpinner;
    private JButton openServerFilesButton;
    private JButton saveServerPropertiesButton;
    private JComboBox serverAuthMovementComboBox;
    private JSpinner playerMovementScoreThresholdSpinner;
    private JSpinner playerMovementDistanceThresholdSpinner;
    private JSpinner playerMovementDurationThresholdSpinner;
    private JCheckBox correctPlayerMovementCheckBox;
    private JButton downloadServerSoftwareButton;
    private JCheckBox UPnPPortForwardCheckBox;
    private JCheckBox autoRestartInSecondsCheckBox;
    private JSpinner autoRestartSpinner;
    private GraphView cpuGraph;
    private GraphView ramGraph;
    private GraphView playerGraph;
    private JLabel uptimeLabel;
    private JCheckBox forcedGamemodeCheckBox;
    private JCheckBox serverAuthoritativeBlockBreakingCheckBox;
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;

    private Properties config;
    private Process p;
    private BufferedWriter dos;
    private DefaultListModel<String> dlm;

    private UPnPManager manager;

    private File locked;
    private FileOutputStream fos;
    private FileLock fileLock;

    private FileInputStream fis;

    public BedrockServerPage(Server server) {
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
                            if (fis != null) {
                                fis.close();
                            }
                        } catch (IOException ioException) {

                        }

                        dispose();
                    }
                } else {
                    try {
                        fileLock.release();
                        fos.close();
                        locked.delete();
                        if (fis != null) {
                            fis.close();
                        }
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

        File serverProperties = new File("servers/" + server.getDirName() + "/server.properties");

        tickDistanceSpinner.setModel(new SpinnerNumberModel(4, 4, 12, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(tickDistanceSpinner, "#");
        tickDistanceSpinner.setEditor(editor);
        viewDistanceSpinner.setModel(new SpinnerNumberModel(32, 1, 29999984, 1));
        editor = new JSpinner.NumberEditor(viewDistanceSpinner, "#");
        viewDistanceSpinner.setEditor(editor);
        ipv4PortSpinner.setModel(new SpinnerNumberModel(19132, 1, 65535, 1));
        editor = new JSpinner.NumberEditor(ipv4PortSpinner, "#");
        ipv4PortSpinner.setEditor(editor);
        ipv6PortSpinner.setModel(new SpinnerNumberModel(19133, 1, 65535, 1));
        editor = new JSpinner.NumberEditor(ipv6PortSpinner, "#");
        ipv6PortSpinner.setEditor(editor);
        maxPlayersSpinner.setModel(new SpinnerNumberModel(10, 1, 2147483647, 1));
        editor = new JSpinner.NumberEditor(maxPlayersSpinner, "#");
        maxPlayersSpinner.setEditor(editor);
        idleTimeoutKickSpinner.setModel(new SpinnerNumberModel(0, 0, 29999984, 1));
        editor = new JSpinner.NumberEditor(idleTimeoutKickSpinner, "#");
        idleTimeoutKickSpinner.setEditor(editor);
        maxThreadsSpinner.setModel(new SpinnerNumberModel(8, 0, 29999984, 1));
        editor = new JSpinner.NumberEditor(maxThreadsSpinner, "#");
        maxThreadsSpinner.setEditor(editor);
        compressionThresholdSpinner.setModel(new SpinnerNumberModel(1, 1, 65535, 1));
        editor = new JSpinner.NumberEditor(compressionThresholdSpinner, "#");
        compressionThresholdSpinner.setEditor(editor);
        editor = new JSpinner.NumberEditor(playerMovementScoreThresholdSpinner, "#");
        playerMovementScoreThresholdSpinner.setEditor(editor);
        playerMovementDistanceThresholdSpinner.setModel(new SpinnerNumberModel(0.3, 0, 29999984, 0.1));
        editor = new JSpinner.NumberEditor(playerMovementDistanceThresholdSpinner, "#");
        playerMovementDistanceThresholdSpinner.setEditor(editor);
        editor = new JSpinner.NumberEditor(playerMovementDurationThresholdSpinner, "#");
        playerMovementDurationThresholdSpinner.setEditor(editor);
        autoRestartSpinner.setModel(new SpinnerNumberModel(60, 60, 86400, 1));
        editor = new JSpinner.NumberEditor(autoRestartSpinner, "#");
        autoRestartSpinner.setEditor(editor);

        if (server.getAutoRestart() >= 60) {
            autoRestartInSecondsCheckBox.setSelected(true);
            autoRestartSpinner.setEnabled(true);
            autoRestartSpinner.setValue(server.getAutoRestart());
        }
        if (server.isUpnpEnabled()) {
            UPnPPortForwardCheckBox.setSelected(true);
        }

        try {
            loadConfiguration(serverProperties);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't open server properties! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

            dispose();
        }

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

        saveServerPropertiesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autoRestartInSecondsCheckBox.isSelected()) {
                    server.setAutoRestart((Integer) autoRestartSpinner.getValue());
                } else {
                    server.setAutoRestart(-1);
                }
                server.setUpnpEnabled(UPnPPortForwardCheckBox.isSelected());

                config.setProperty("server-name", serverNameTextField.getText());
                config.setProperty("gamemode", gamemodeComboBox.getSelectedItem().toString().toLowerCase());
                config.setProperty("force-gamemode", LogSM.booleanToString(server.getServerType(), forcedGamemodeCheckBox.isSelected()));
                config.setProperty("level-name", levelNameTextField.getText());
                config.setProperty("level-seed", levelSeedTextField.getText());
                config.setProperty("view-distance", String.valueOf(viewDistanceSpinner.getValue()));
                config.setProperty("difficulty", difficultyComboBox.getSelectedItem().toString().toLowerCase());
                config.setProperty("tick-distance", String.valueOf(tickDistanceSpinner.getValue()));
                config.setProperty("allow-cheats", LogSM.booleanToString(server.getServerType(), allowCheatsCheckBox.isSelected()));
                config.setProperty("texturepack-required", LogSM.booleanToString(server.getServerType(), requireTexturePackCheckBox.isSelected()));

                config.setProperty("server-port", String.valueOf(ipv4PortSpinner.getValue()));
                config.setProperty("server-portv6", String.valueOf(ipv6PortSpinner.getValue()));

                config.setProperty("max-players", String.valueOf(maxPlayersSpinner.getValue()));
                config.setProperty("online-mode", LogSM.booleanToString(server.getServerType(), onlineModeCheckBox.isSelected()));
                config.setProperty("white-list", LogSM.booleanToString(server.getServerType(), whitelistEnabledCheckBox.isSelected()));
                config.setProperty("default-player-permission-level", defaultPlayerPermLevelComboBox.getSelectedItem().toString().toLowerCase());
                config.setProperty("player-idle-timeout", String.valueOf(idleTimeoutKickSpinner.getValue()));

                config.setProperty("max-threads", String.valueOf(maxThreadsSpinner.getValue()));
                config.setProperty("content-log-file-enabled", LogSM.booleanToString(server.getServerType(), contentLogFileEnabledCheckBox.isSelected()));
                config.setProperty("compression-threshold", String.valueOf(compressionThresholdSpinner.getValue()));
                config.setProperty("server-authoritative-movement", String.valueOf(serverAuthMovementComboBox.getSelectedItem()).equals("Server Authoritative") ? "server-auth" : "client-auth");
                if (String.valueOf(serverAuthMovementComboBox.getSelectedItem()).equals("Client Authoritative")) {
                    config.setProperty("player-movement-score-threshold", String.valueOf(playerMovementScoreThresholdSpinner.getValue()));
                    config.setProperty("player-movement-distance-threshold", String.valueOf(playerMovementDistanceThresholdSpinner.getValue()));
                    config.setProperty("player-movement-duration-threshold", String.valueOf(playerMovementDurationThresholdSpinner.getValue()));
                }
                config.setProperty("correct-player-movement", String.valueOf(correctPlayerMovementCheckBox.isSelected()));
                config.setProperty("server-authoritative-block-breaking", String.valueOf(serverAuthoritativeBlockBreakingCheckBox.isSelected()));

                try {
                    FileOutputStream fos = new FileOutputStream(serverProperties);
                    config.store(fos, null);
                    fos.flush();
                    fos.close();

                    server.save();

                    ramGraph.setMaxScore(server.getRAM());
                    playerGraph.setMaxScore((Integer) maxPlayersSpinner.getValue());

                    JOptionPane.showMessageDialog(null, "Saved Server Properties! Please restart your server (if running) to apply the changes!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Can't Save Server Properties! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
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
                        dos.write(textField1.getText());
                        dos.newLine();
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
                    if (NetworkClient.checkUDPServer(LogSM.getExternalIP(), Long.valueOf(String.valueOf(ipv4PortSpinner.getValue())).intValue())) {
                        JOptionPane.showMessageDialog(null, "Your server is accessible to the internet!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Your server isn't accessible to the internet! Follow instructions online how to port forward.", "LogSM", JOptionPane.ERROR_MESSAGE);
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

                        startServer(server, serverProperties);
                    }
                });

                stopServer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        OFFLINEButton.setText("STOPPING");

                        try {
                            dos.write("stop");
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
                        restartServer();
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

        serverAuthMovementComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (String.valueOf(serverAuthMovementComboBox.getSelectedItem()).equals("Server Authoritative") || String.valueOf(serverAuthMovementComboBox.getSelectedItem()).equals("Server Authoritative with Rewind")) {
                    playerMovementScoreThresholdSpinner.setEnabled(true);
                    playerMovementDistanceThresholdSpinner.setEnabled(true);
                    playerMovementDurationThresholdSpinner.setEnabled(true);
                } else {
                    playerMovementScoreThresholdSpinner.setEnabled(false);
                    playerMovementDistanceThresholdSpinner.setEnabled(false);
                    playerMovementDurationThresholdSpinner.setEnabled(false);
                }
            }
        });

        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!list1.isSelectionEmpty() && SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem opPlayer = new JMenuItem("OP Player");
                    JMenuItem deopPlayer = new JMenuItem("De-OP Player");
                    JMenuItem kickPlayer = new JMenuItem("Kick Player");

                    opPlayer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                dos.write("op " + list1.getSelectedValue());
                                dos.newLine();
                                dos.flush();
                            } catch (IOException ioException) {

                            }
                        }
                    });

                    deopPlayer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                dos.write("deop " + list1.getSelectedValue());
                                dos.newLine();
                                dos.flush();
                            } catch (IOException ioException) {

                            }
                        }
                    });

                    kickPlayer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                dos.write("kick " + list1.getSelectedValue());
                                dos.newLine();
                                dos.flush();
                            } catch (IOException ioException) {

                            }
                        }
                    });

                    popupMenu.add(opPlayer);
                    popupMenu.add(deopPlayer);
                    popupMenu.add(kickPlayer);

                    popupMenu.show(list1, e.getX(), e.getY());
                }
            }
        });

        downloadServerSoftwareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdaterDialog updaterDialog = new UpdaterDialog(server, NetworkClient.getBedrockURL(), null);
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

        externalIPLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StringSelection selection = new StringSelection(externalIPLabel.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                JOptionPane.showMessageDialog(null, "Copied the External IP to the Clipboard!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        localIPLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StringSelection selection = new StringSelection(localIPLabel.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);

                JOptionPane.showMessageDialog(null, "Copied the Local IP to the Clipboard!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
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
        playerGraph.setMaxScore((Integer) maxPlayersSpinner.getValue());

        setMinimumSize(getPreferredSize());
    }

    public void loadConfiguration(File serverProperties) throws IOException {
        config = new Properties();

        if (serverProperties.exists()) {
            fis = new FileInputStream(serverProperties);
            config.load(fis);

            serverNameTextField.setText(config.getProperty("server-name"));
            gamemodeComboBox.setSelectedItem(config.getProperty("gamemode").substring(0, 1).toUpperCase() + config.getProperty("gamemode").substring(1));
            forcedGamemodeCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("force-gamemode")));
            difficultyComboBox.setSelectedItem(config.getProperty("difficulty").substring(0, 1).toUpperCase() + config.getProperty("difficulty").substring(1));
            levelNameTextField.setText(config.getProperty("level-name"));
            levelSeedTextField.setText(config.getProperty("level-seed"));
            tickDistanceSpinner.setValue(Integer.parseInt(config.getProperty("tick-distance")));
            viewDistanceSpinner.setValue(Integer.parseInt(config.getProperty("view-distance")));
            allowCheatsCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("allow-cheats")));
            requireTexturePackCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("texturepack-required")));

            ipv4PortSpinner.setValue(Integer.parseInt(config.getProperty("server-port")));
            ipv6PortSpinner.setValue(Integer.parseInt(config.getProperty("server-portv6")));

            maxPlayersSpinner.setValue(Integer.parseInt(config.getProperty("max-players")));
            onlineModeCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("online-mode")));
            whitelistEnabledCheckBox.setSelected(LogSM.parseBoolean(config.containsKey("white-list") ? config.getProperty("white-list") : config.getProperty("allow-list")));
            defaultPlayerPermLevelComboBox.setSelectedItem(config.getProperty("default-player-permission-level").substring(0, 1).toUpperCase() + config.getProperty("default-player-permission-level").substring(1));
            idleTimeoutKickSpinner.setValue(Integer.parseInt(config.getProperty("player-idle-timeout")));

            maxThreadsSpinner.setValue(Integer.parseInt(config.getProperty("max-threads")));
            contentLogFileEnabledCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("content-log-file-enabled")));
            compressionThresholdSpinner.setValue(Integer.parseInt(config.getProperty("compression-threshold")));
            serverAuthMovementComboBox.setSelectedItem(config.getProperty("server-authoritative-movement").equals("server-auth") ? "Server Authoritative" : (config.getProperty("server-authoritative-movement").equals("server-auth-with-rewind") ? "Server Authoritative with Rewind" : "Client Authoritative"));
            if (config.getProperty("server-authoritative-movement").equals("server-auth")) {
                playerMovementScoreThresholdSpinner.setEnabled(false);
                playerMovementDistanceThresholdSpinner.setEnabled(false);
                playerMovementDurationThresholdSpinner.setEnabled(false);
            } else {
                playerMovementScoreThresholdSpinner.setValue(Integer.parseInt(config.getProperty("player-movement-score-threshold")));
                playerMovementDistanceThresholdSpinner.setValue(Double.parseDouble(config.getProperty("player-movement-distance-threshold")));
                playerMovementDurationThresholdSpinner.setValue(Integer.parseInt(config.getProperty("player-movement-duration-threshold-in-ms")));
            }
            correctPlayerMovementCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("correct-player-movement")));
            serverAuthoritativeBlockBreakingCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("server-authoritative-block-breaking")));

            fis.close();
        } else {
            defaultPlayerPermLevelComboBox.setSelectedItem("Member");
        }
    }

    public void startServer(Server server, File serverProperties) {
        if (!consoleTextPane.getText().isEmpty()) {
            consoleTextPane.setText("");
        }

        File serverJar = new File("servers/" + server.getDirName() + (System.getProperty("os.name").startsWith("Windows") ? "/bedrock_server.exe" : "/bedrock_server"));
        if (!serverJar.exists()) {
            JOptionPane.showMessageDialog(null, "You must download the bedrock server files before you can run the server!", "LogSM", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            List<String> commands = new ArrayList<>();
            if (System.getProperty("os.name").startsWith("Windows")) {
                commands.add(new File("servers/" + server.getDirName() + "/bedrock_server.exe").getAbsolutePath());
            } else {
                commands.add("./bedrock_server");
            }
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            try {
                p = processBuilder.directory(new File("servers/" + server.getDirName())).redirectErrorStream(true).start();
                dos = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                Executors.newSingleThreadExecutor().submit(new ServerThread(this, server, p, OFFLINEButton, consoleTextPane, dlm));
                Executors.newSingleThreadExecutor().submit(new ServerUsageThread(server, p, cpuUsageLabel, cpuGraph, ramLabel, ramGraph, dlm, playerGraph, uptimeLabel, OFFLINEButton));
                ProcessHandle pH = p.toHandle();
                pH.onExit().thenAccept(pH_ -> {
                    if (OFFLINEButton.getText().equals("RESTARTING")) {
                        consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                        list1.removeAll();
                        p = null;
                        try {
                            dos.close();
                            loadConfiguration(serverProperties);
                            fis.close();
                        } catch (IOException ioException) {

                        }
                        dos = null;

                        startServer(server, serverProperties);
                    } else if (OFFLINEButton.getText().equals("AUTO-RESTARTING")) {
                        consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                        list1.removeAll();
                        p = null;
                        try {
                            dos.close();
                            loadConfiguration(serverProperties);
                            fis.close();
                        } catch (IOException ioException) {

                        }
                        dos = null;

                        startServer(server, serverProperties);
                    } else if (OFFLINEButton.getText().equals("STOPPING")) {
                        consoleTextPane.append("\n" + "Server Stopped or Crashed");
                        consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                        list1.removeAll();
                        p = null;
                        try {
                            dos.close();
                            loadConfiguration(serverProperties);
                            fis.close();
                        } catch (IOException ioException) {

                        }
                        if (UPnPPortForwardCheckBox.isSelected()) {
                            manager.closeUDPPort(server, localIPLabel.getText(), Integer.parseInt(config.getProperty("server-port")));
                        }
                        dos = null;
                    }
                });
                if (UPnPPortForwardCheckBox.isSelected()) {
                    if (!manager.isUDPOpen()) {
                        manager.openUDPPort(server, localIPLabel.getText(), Integer.parseInt(config.getProperty("server-port")));
                    }
                }
                if (!OFFLINEButton.getText().equals("RESTARTING")) {
                    OFFLINEButton.setText("STARTING");
                }
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null, "Can't start " + server.getServerType().name() + " Server! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void restartServer() {
        OFFLINEButton.setText("RESTARTING");

        try {
            dos.write("stop");
            dos.flush();
            dos.close();
        } catch (IOException ioException) {

        }

        dlm.clear();
    }

}
