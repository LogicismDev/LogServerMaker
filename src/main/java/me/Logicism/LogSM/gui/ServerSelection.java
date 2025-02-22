package me.Logicism.LogSM.gui;

import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.core.ServerType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ServerSelection extends JFrame {

    private JButton createButton;
    private JButton editButton;
    private JList list1;
    private JButton openButton;
    private JButton deleteButton;
    private JPanel panel1;
    private JButton settingsButton;
    private JScrollPane scrollPane1;

    public ServerSelection() {
        super("LogServerMaker v" + LogSM.VERSION);

        scrollPane1.putClientProperty("JScrollPane.smoothScrolling", true);

        try {
            setIconImage(ImageIO.read(LogSM.class.getClassLoader().getResourceAsStream("icon.png")));
        } catch (IOException ignored) {

        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int i = 0;
                for (Frame frame : Frame.getFrames()) {
                    if (frame.isVisible()) {
                        i++;
                    }
                }

                if (i == 1) {
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(null, "Close all remaining server pages before you close LogSM!", "LogSM", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setContentPane(panel1);

        setPreferredSize(new Dimension(500, 700));
        setResizable(false);

        final DefaultListModel<Server> dlm = new DefaultListModel<>();
        list1.setModel(dlm);
        list1.setCellRenderer(new ServerListRenderer());

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Server server = new Server();

                ServerCreateDialog createDialog = new ServerCreateDialog(server);
                createDialog.pack();
                createDialog.setLocationRelativeTo(null);
                createDialog.setVisible(true);

                updateList();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!list1.isSelectionEmpty()) {
                    Server server = (Server) list1.getSelectedValue();

                    File locked = new File("servers/" + server.getDirName() + "/logsm.lock");

                    if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")) {
                        if (locked.exists()) {
                            locked.delete();
                        }
                    }

                    if (locked.exists()) {
                        JOptionPane.showMessageDialog(null, "This server is in use by another LogSM instance!", "LogSM", JOptionPane.ERROR_MESSAGE);
                    } else {
                        ServerEditDialog renameDialog = new ServerEditDialog(server);
                        renameDialog.pack();
                        renameDialog.setLocationRelativeTo(null);
                        renameDialog.setVisible(true);

                        updateList();
                    }
                }
            }
        });

        KeyStroke deleteStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        ActionMap am = list1.getActionMap();
        list1.getInputMap().put(deleteStroke, deleteStroke.toString());
        am.put(deleteStroke.toString(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!list1.isSelectionEmpty()) {
                    Server server = (Server) list1.getSelectedValue();

                    File locked = new File("servers/" + server.getDirName() + "/logsm.lock");

                    if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")) {
                        if (locked.exists()) {
                            locked.delete();
                        }
                    }

                    if (locked.exists()) {
                        JOptionPane.showMessageDialog(null, "This server is in use by another LogSM instance!", "LogSM", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this server?\n(All data cannot be recovered)", "LogSM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                            dlm.removeElement(server);

                            File serverDir = new File("servers/" + server.getDirName());
                            LogSM.deleteDirectory(serverDir);

                            LogSM.getServerList().remove(server);
                        }
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!list1.isSelectionEmpty()) {
                    Server server = (Server) list1.getSelectedValue();

                    File locked = new File("servers/" + server.getDirName() + "/logsm.lock");

                    if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")) {
                        if (locked.exists()) {
                            locked.delete();
                        }
                    }

                    if (locked.exists()) {
                        JOptionPane.showMessageDialog(null, "This server is in use by another LogSM instance!", "LogSM", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this server?\n(All data cannot be recovered)", "LogSM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                            dlm.removeElement(server);

                            File serverDir = new File("servers/" + server.getDirName());
                            LogSM.deleteDirectory(serverDir);

                            LogSM.getServerList().remove(server);
                        }
                    }
                }
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!list1.isSelectionEmpty()) {
                    Server server = (Server) list1.getSelectedValue();

                    openServer(server);
                }
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogSMSettings logSMSettings = new LogSMSettings();
                logSMSettings.pack();
                logSMSettings.setLocationRelativeTo(null);
                logSMSettings.setVisible(true);
            }
        });

        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    Server server = (Server) list1.getSelectedValue();

                    openServer(server);
                }
            }
        });

        updateList();
    }

    private void openServer(Server server) {
        if (server.getServerType() != null) {
            File locked = new File("servers/" + server.getDirName() + "/logsm.lock");

            if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")) {
                if (locked.exists()) {
                    locked.delete();
                }
            }

            if (locked.exists()) {
                JOptionPane.showMessageDialog(null, "This server is already in use by another LogSM instance!", "LogSM", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    locked.createNewFile();

                    if (server.getServerType().equals(ServerType.Bedrock)) {
                        BedrockServerPage page = new BedrockServerPage(server);
                        page.pack();
                        page.setLocationRelativeTo(null);
                        page.setVisible(true);
                    } else if (server.getServerType().equals(ServerType.Geyser) || server.getServerType().equals(ServerType.BungeeCord) || server.getServerType().equals(ServerType.NanoLimbo) || server.getServerType().equals(ServerType.Velocity) || server.getServerType().equals(ServerType.WaterdogPE)) {
                        ConfigServerPage page = new ConfigServerPage(server);
                        page.pack();
                        page.setLocationRelativeTo(null);
                        page.setVisible(true);
                    } else {
                        ServerPage page = new ServerPage(server);
                        page.pack();
                        page.setLocationRelativeTo(null);
                        page.setVisible(true);
                    }
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "Can't open server! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "This Server is no longer usable in LogSM! Please backup your files!", "LogSM", JOptionPane.ERROR_MESSAGE);
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(new File("servers/" + server.getDirName()));
                } catch (IOException e) {

                }
            }
        }
    }

    private void updateList() {
        DefaultListModel<Server> dlm = (DefaultListModel<Server>) list1.getModel();
        dlm.clear();

        for (Server server : LogSM.getServerList()) {
            dlm.addElement(server);
        }
    }

}

class ServerListRenderer extends JLabel implements ListCellRenderer<Server> {

    public ServerListRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Server> list, Server server, int index, boolean isSelected, boolean cellHasFocus) {
        setText(server.getName() + " - " + (server.getServerType() != null ? server.getServerType().name() + (server.getVersion() != null ? " (" + server.getVersion() + ")" : "") : "NO LONGER USABLE"));
        setFont(getFont().deriveFont(22f));

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
