package me.Logicism.LogSM.gui;

import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.core.ServerType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class ServerCreateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private Server server;

    public ServerCreateDialog(Server server) {
        this.server = server;

        try {
            setIconImage(ImageIO.read(LogSM.class.getClassLoader().getResourceAsStream("icon.png")));
        } catch (IOException ignored) {

        }

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("LogSM");

        for (String s : LogSM.vanillaVersions) {
            comboBox2.addItem(s);
        }

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBox2.removeAllItems();

                if (comboBox1.getSelectedItem().equals("Vanilla")) {
                    for (String s : LogSM.vanillaVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("CraftBukkit") || comboBox1.getSelectedItem().equals("Spigot")) {
                    for (String s : LogSM.spigotVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("Paper")) {
                    for (String s : LogSM.paperVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("Purpur")) {
                    for (String s : LogSM.purpurVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("Forge")) {
                    for (String s : LogSM.forgeVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("Magma")) {
                    for (String s : LogSM.magmaVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("Mohist")) {
                    for (String s : LogSM.mohistVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("SpongeVanilla")) {
                    for (String s : LogSM.spongeVanillaVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("Fabric")) {
                    for (String s : LogSM.fabricVersions) {
                        comboBox2.addItem(s);
                    }

                    if (!comboBox2.isEnabled()) {
                        comboBox2.setEnabled(true);
                    }
                } else if (comboBox1.getSelectedItem().equals("Nukkit") || comboBox1.getSelectedItem().equals("PocketMine") || comboBox1.getSelectedItem().equals("Bedrock") || comboBox1.getSelectedItem().equals("Geyser") || comboBox1.getSelectedItem().equals("BungeeCord") || comboBox1.getSelectedItem().equals("NanoLimbo") || comboBox1.getSelectedItem().equals("Waterfall") || comboBox1.getSelectedItem().equals("Waterdog") || comboBox1.getSelectedItem().equals("Velocity") || comboBox1.getSelectedItem().equals("WaterdogPE")) {
                    comboBox2.setEnabled(false);
                }

                pack();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (System.getProperty("os.name").startsWith("Mac OS X") && comboBox1.getSelectedItem().equals("Bedrock")) {
            JOptionPane.showMessageDialog(null, "The Mojang Bedrock server is not supported on macOS Systems!", "LogSM", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (textField1.getText().length() > 3) {
            boolean exists = false;

            for (Server server : LogSM.getServerList()) {
                if (server.getName().toLowerCase().equals(textField1.getText().toLowerCase())) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                JOptionPane.showMessageDialog(null, "Server name already exists!", "LogSM", JOptionPane.ERROR_MESSAGE);
            } else {
                server.setName(textField1.getText());
                server.setServerType(ServerType.valueOf(comboBox1.getSelectedItem().toString()));
                if (comboBox2.isEnabled()) {
                    server.setVersion(comboBox2.getSelectedItem().toString());
                }
                server.setRAM(1024);
                server.setAutoRestart(-1);
                try {
                    server.save();

                    LogSM.getServerList().add(server);

                    dispose();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Can't create server! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Name must be longer than 3 characters!", "LogSM", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
