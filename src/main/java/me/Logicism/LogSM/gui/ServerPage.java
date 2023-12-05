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
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;

public class ServerPage extends JFrame {

    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTextField worldTextField;
    private JTextField seedTextField;
    private JCheckBox enableNPCSpawningCheckBox;
    private JCheckBox enableAnimalSpawningCheckBox;
    private JCheckBox enableMonsterSpawningCheckBox;
    private JComboBox difficultyComboBox;
    private JComboBox gamemodeComboBox;
    private JCheckBox forcedGamemodeCheckBox;
    private JSpinner maxBuildHeightSpinner;
    private JTextField generatorSettingsTextField;
    private JSpinner maxWorldSizeSpinner;
    private JCheckBox generateStructuresCheckBox;
    private JCheckBox allowNetherCheckBox;
    private JCheckBox hardcoreModeCheckBox;
    private JSpinner maxPlayersSpinner;
    private JTextField MOTDTextField;
    private JCheckBox pvpEnabledCheckBox;
    private JCheckBox onlineModeCheckBox;
    private JSpinner viewDistanceSpinner;
    private JCheckBox allowFlightCheckBox;
    private JComboBox OPPermLevelComboBox;
    private JCheckBox whitelistEnabledCheckBox;
    private JTextField serverIPTextField;
    private JCheckBox enableQueryCheckBox;
    private JCheckBox enableRCONCheckBox;
    private JTextField RCONPasswordTextField;
    private JCheckBox blockProxyConnectionsCheckBox;
    private JSpinner maxTickTimeSpinner;
    private JSpinner serverPortSpinner;
    private JCheckBox enableSnooperCheckBox;
    private JTextField resourcePackURLTextField;
    private JButton saveServerPropertiesButton;
    private JButton OFFLINEButton;
    private JSpinner spawnProtectionSpinner;
    private JList list1;
    private JTextField textField1;
    private JCheckBox broadcastConsoleToOPsCheckBox;
    private JCheckBox announcePlayersAchievementsCheckBox;
    private JButton downloadServerJARButton;
    private JTextField serverArgsTextField;
    private JSpinner networkCompressionThresholdSpinner;
    private JTextField resourcePackSHA1TextField;
    private JCheckBox enableCommandBlocksCheckBox;
    private JSpinner RCONPortSpinner;
    private JSpinner queryPortSpinner;
    private JSpinner allocatedRAMSpinner;
    private JComboBox levelTypeComboBox;
    private JButton openServerFilesButton;
    private JSpinner idleTimeoutKickSpinner;
    private JTextArea consoleTextPane;
    private JLabel cpuUsageLabel;
    private JLabel ramLabel;
    private JButton checkPortForwardButton;
    private JLabel externalIPLabel;
    private JLabel localIPLabel;
    private JTextField subMOTDTextField;
    private JCheckBox UPnPPortForwardCheckBox;
    private JTextField javaHomeTextField;
    private JCheckBox autoRestartInSecondsCheckBox;
    private JSpinner autoRestartSpinner;

    private GraphView cpuGraph;
    private GraphView ramGraph;
    private GraphView playerGraph;
    private JLabel uptimeLabel;
    private JCheckBox enableDebugCheckBox;
    private JCheckBox enableSyncChunkWritingCheckBox;
    private JCheckBox enableJMXMonitoringCheckBox;
    private JCheckBox enableStatusCheckBox;
    private JSpinner rateLimitSpinner;
    private JTextField textFilteringConfigTextField;
    private JTextField resourcePackPromptTextField;
    private JCheckBox forceResourcePackCheckBox;
    private JSpinner entityBroadcastRangeSpinner;
    private JSpinner functionPermissionLevelSpinner;
    private JSpinner simulationDistanceSpinner;
    private JCheckBox hideOnlinePlayersCheckBox;
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;
    private JSpinner maxChainedNeighborUpdatesSpinner;
    private JCheckBox enableChatPreviewCheckBox;
    private JCheckBox enforceSecureProfileCheckBox;

    private Properties config;
    private Process p;
    private BufferedWriter dos;
    private DefaultListModel<String> dlm;

    private UPnPManager manager;

    private File locked;
    private FileOutputStream fos;
    private FileLock fileLock;

    private FileInputStream fis;

    public ServerPage(Server server) {
        super(server.getName() + " - " + server.getServerType().name() + (server.getVersion() != null ? " (" + server.getVersion() + ")" : "") + " - LogServerMaker v" + LogSM.VERSION);

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

        maxWorldSizeSpinner.setModel(new SpinnerNumberModel(29999984, 1, 29999984, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(maxWorldSizeSpinner, "#");
        maxWorldSizeSpinner.setEditor(editor);
        editor = new JSpinner.NumberEditor(maxTickTimeSpinner, "#");
        maxTickTimeSpinner.setEditor(editor);
        maxBuildHeightSpinner.setModel(new SpinnerNumberModel(256, 8, 256, 8));
        editor = new JSpinner.NumberEditor(maxBuildHeightSpinner, "#");
        maxBuildHeightSpinner.setEditor(editor);
        viewDistanceSpinner.setModel(new SpinnerNumberModel(12, 3, 32, 1));
        editor = new JSpinner.NumberEditor(viewDistanceSpinner, "#");
        viewDistanceSpinner.setEditor(editor);
        simulationDistanceSpinner.setModel(new SpinnerNumberModel(12, 3, 32, 1));
        editor = new JSpinner.NumberEditor(simulationDistanceSpinner, "#");
        simulationDistanceSpinner.setEditor(editor);
        maxTickTimeSpinner.setModel(new SpinnerNumberModel(60000, 0, Long.MAX_VALUE, 1));
        editor = new JSpinner.NumberEditor(maxTickTimeSpinner, "#");
        maxTickTimeSpinner.setEditor(editor);
        serverPortSpinner.setModel(new SpinnerNumberModel(25565, 1, 65535, 1));
        editor = new JSpinner.NumberEditor(serverPortSpinner, "#");
        serverPortSpinner.setEditor(editor);
        queryPortSpinner.setModel(new SpinnerNumberModel(25565, 1, 65535, 1));
        editor = new JSpinner.NumberEditor(queryPortSpinner, "#");
        queryPortSpinner.setEditor(editor);
        RCONPortSpinner.setModel(new SpinnerNumberModel(25565, 1, 65535, 1));
        editor = new JSpinner.NumberEditor(RCONPortSpinner, "#");
        RCONPortSpinner.setEditor(editor);
        rateLimitSpinner.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
        editor = new JSpinner.NumberEditor(rateLimitSpinner, "#");
        rateLimitSpinner.setEditor(editor);
        maxPlayersSpinner.setModel(new SpinnerNumberModel(10, 1, 2147483647, 1));
        editor = new JSpinner.NumberEditor(maxPlayersSpinner, "#");
        maxPlayersSpinner.setEditor(editor);
        idleTimeoutKickSpinner.setModel(new SpinnerNumberModel(0, 0, 2147483647, 1));
        editor = new JSpinner.NumberEditor(idleTimeoutKickSpinner, "#");
        idleTimeoutKickSpinner.setEditor(editor);
        entityBroadcastRangeSpinner.setModel(new SpinnerNumberModel(100, 100, 1000, 1));
        editor = new JSpinner.NumberEditor(entityBroadcastRangeSpinner, "#");
        entityBroadcastRangeSpinner.setEditor(editor);
        allocatedRAMSpinner.setModel(new SpinnerNumberModel(1024, 512, 131072, 256));
        editor = new JSpinner.NumberEditor(allocatedRAMSpinner, "#");
        allocatedRAMSpinner.setEditor(editor);
        editor = new JSpinner.NumberEditor(networkCompressionThresholdSpinner, "#");
        networkCompressionThresholdSpinner.setEditor(editor);
        autoRestartSpinner.setModel(new SpinnerNumberModel(60, 60, 86400, 1));
        editor = new JSpinner.NumberEditor(autoRestartSpinner, "#");
        autoRestartSpinner.setEditor(editor);
        functionPermissionLevelSpinner.setModel(new SpinnerNumberModel(2, 1, 4, 1));
        editor = new JSpinner.NumberEditor(functionPermissionLevelSpinner, "#");
        functionPermissionLevelSpinner.setEditor(editor);
        maxChainedNeighborUpdatesSpinner.setModel(new SpinnerNumberModel(1000000, 1, 2147483647, 1));
        editor = new JSpinner.NumberEditor(maxChainedNeighborUpdatesSpinner, "#");
        maxChainedNeighborUpdatesSpinner.setEditor(editor);

        try {
            config = new Properties();

            if (serverProperties.exists()) {
                loadConfiguration(server, serverProperties);
                fis.close();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't open server properties! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

            dispose();
        }

        allocatedRAMSpinner.setValue(server.getRAM());
        if (server.getServerArgs() != null) {
            serverArgsTextField.setText(server.getServerArgs());
        }
        if (server.getServerType().equals(ServerType.PocketMine)) {
            javaHomeTextField.setEnabled(false);
        } else {
            javaHomeTextField.setText(server.getJavaHome());
        }
        if (server.getAutoRestart() >= 60) {
            autoRestartInSecondsCheckBox.setSelected(true);
            autoRestartSpinner.setEnabled(true);
            autoRestartSpinner.setValue(server.getAutoRestart());
        }
        if (server.isUpnpEnabled()) {
            UPnPPortForwardCheckBox.setSelected(true);
        }

        dlm = new DefaultListModel<>();
        list1.setModel(dlm);
        list1.setCellRenderer(new PlayerListRenderer(server, config.getProperty("online-mode") != null ? LogSM.parseBoolean(config.getProperty("online-mode")) : true));
        list1.setPreferredSize(new Dimension(315, 660));
        list1.setLayoutOrientation(JList.HORIZONTAL_WRAP);

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
                server.setRAM((Integer) allocatedRAMSpinner.getValue());
                server.setServerArgs(serverArgsTextField.getText());
                server.setJavaHome(javaHomeTextField.getText());
                if (autoRestartInSecondsCheckBox.isSelected()) {
                    server.setAutoRestart((Integer) autoRestartSpinner.getValue());
                } else {
                    server.setAutoRestart(-1);
                }
                server.setUpnpEnabled(UPnPPortForwardCheckBox.isSelected());

                if (!serverProperties.exists()) {
                    JOptionPane.showMessageDialog(null, "Please start the server to generate the server.properties file before saving!");
                    return;
                }

                if (config.containsKey("level-name")) {
                    config.setProperty("level-name", worldTextField.getText());
                } else if (config.containsKey("world-name")) {
                    config.setProperty("world-name", worldTextField.getText());
                }
                if (config.containsKey("seed")) {
                    config.setProperty("seed", seedTextField.getText());
                } else if (config.containsKey("level-seed")) {
                    config.setProperty("level-seed", seedTextField.getText());
                }
                config.setProperty("generator-settings", generatorSettingsTextField.getText());
                if (maxWorldSizeSpinner.isEnabled()) {
                    config.setProperty("max-world-size", String.valueOf(maxWorldSizeSpinner.getValue()));
                }
                if (levelTypeComboBox.isEnabled()) {
                    config.setProperty("level-type", levelTypeComboBox.getSelectedItem().toString().toLowerCase());
                }
                if (allowNetherCheckBox.isEnabled()) {
                    config.setProperty("allow-nether", LogSM.booleanToString(server.getServerType(), allowNetherCheckBox.isSelected()));
                }
                if (generateStructuresCheckBox.isEnabled()) {
                    config.setProperty("generate-structures", LogSM.booleanToString(server.getServerType(), generateStructuresCheckBox.isSelected()));
                }
                if (allowFlightCheckBox.isEnabled()) {
                    config.setProperty("allow-flight", LogSM.booleanToString(server.getServerType(), allowFlightCheckBox.isSelected()));
                }
                if (enableNPCSpawningCheckBox.isEnabled()) {
                    config.setProperty("spawn-npcs", LogSM.booleanToString(server.getServerType(), enableNPCSpawningCheckBox.isSelected()));
                }
                if (enableAnimalSpawningCheckBox.isEnabled()) {
                    config.setProperty("spawn-animals", LogSM.booleanToString(server.getServerType(), enableAnimalSpawningCheckBox.isSelected()));
                }
                if (config.containsKey("spawn-monsters")) {
                    config.setProperty("spawn-monsters", LogSM.booleanToString(server.getServerType(), enableMonsterSpawningCheckBox.isSelected()));
                } else if (config.containsKey("spawn-mobs")) {
                    config.setProperty("spawn-mobs", LogSM.booleanToString(server.getServerType(), enableMonsterSpawningCheckBox.isSelected()));
                }
                try {
                    int i = Integer.parseInt(config.getProperty("gamemode"));
                    int i1 = Integer.parseInt(config.getProperty("difficulty"));

                    config.setProperty("gamemode", String.valueOf(gamemodeComboBox.getSelectedIndex()));
                    config.setProperty("difficulty", String.valueOf(difficultyComboBox.getSelectedIndex()));
                } catch (NumberFormatException e1) {
                    config.setProperty("gamemode", String.valueOf(gamemodeComboBox.getSelectedItem()));
                    config.setProperty("difficulty", String.valueOf(difficultyComboBox.getSelectedItem()));
                }
                config.setProperty("force-gamemode", LogSM.booleanToString(server.getServerType(), forcedGamemodeCheckBox.isSelected()));
                config.setProperty("hardcore", LogSM.booleanToString(server.getServerType(), hardcoreModeCheckBox.isSelected()));
                if (maxBuildHeightSpinner.isEnabled()) {
                    config.setProperty("max-build-height", String.valueOf(maxBuildHeightSpinner.getValue()));
                }
                config.setProperty("view-distance", String.valueOf(viewDistanceSpinner.getValue()));
                if (simulationDistanceSpinner.isEnabled()) {
                    config.setProperty("simulation-distance", String.valueOf(simulationDistanceSpinner.getValue()));
                }
                config.setProperty("motd", MOTDTextField.getText());
                if (subMOTDTextField.isEnabled()) {
                    config.setProperty("sub-motd", subMOTDTextField.getText());
                }
                if (spawnProtectionSpinner.isEnabled()) {
                    config.setProperty("spawn-protection", String.valueOf(spawnProtectionSpinner.getValue()));
                }
                if (maxTickTimeSpinner.isEnabled()) {
                    config.setProperty("max-tick-time", String.valueOf(maxTickTimeSpinner.getValue()));
                }
                if (resourcePackURLTextField.isEnabled()) {
                    config.setProperty("resource-pack", resourcePackURLTextField.getText());
                }
                if (resourcePackPromptTextField.isEnabled()) {
                    config.setProperty("resource-pack-prompt", resourcePackPromptTextField.getText());
                }
                if (forceResourcePackCheckBox.isEnabled()) {
                    config.setProperty("require-resource-pack", LogSM.booleanToString(server.getServerType(), forceResourcePackCheckBox.isSelected()));
                }

                config.setProperty("server-ip", serverIPTextField.getText());
                config.setProperty("server-port", String.valueOf(serverPortSpinner.getValue()));
                config.setProperty("enable-query", LogSM.booleanToString(server.getServerType(), enableQueryCheckBox.isSelected()));
                if (enableQueryCheckBox.isSelected() && queryPortSpinner.isEnabled()) {
                    config.setProperty("query.port", String.valueOf(queryPortSpinner.getValue()));
                }
                if (enableRCONCheckBox.isEnabled()) {
                    config.setProperty("enable-rcon", LogSM.booleanToString(server.getServerType(), enableRCONCheckBox.isSelected()));
                    if (enableRCONCheckBox.isSelected()) {
                        config.setProperty("rcon.port", String.valueOf(RCONPortSpinner.getValue()));
                        config.setProperty("rcon.password", RCONPasswordTextField.getText());
                    }
                }
                if (blockProxyConnectionsCheckBox.isEnabled()) {
                    config.setProperty("prevent-proxy-connections", LogSM.booleanToString(server.getServerType(), blockProxyConnectionsCheckBox.isSelected()));
                }
                if (enableSnooperCheckBox.isEnabled()) {
                    config.setProperty("snooper-enabled", LogSM.booleanToString(server.getServerType(), enableSnooperCheckBox.isSelected()));
                }
                if (enableStatusCheckBox.isEnabled()) {
                    config.setProperty("enable-status", LogSM.booleanToString(server.getServerType(), enableStatusCheckBox.isSelected()));
                }
                if (rateLimitSpinner.isEnabled()) {
                    config.setProperty("rate-limit", String.valueOf(rateLimitSpinner.getValue()));
                }


                config.setProperty("max-players", String.valueOf(maxPlayersSpinner.getValue()));
                if (enableCommandBlocksCheckBox.isEnabled()) {
                    config.setProperty("enable-command-block", LogSM.booleanToString(server.getServerType(), enableCommandBlocksCheckBox.isSelected()));
                }
                config.setProperty("pvp", LogSM.booleanToString(server.getServerType(), pvpEnabledCheckBox.isSelected()));
                if (config.containsKey("online-mode")) {
                    config.setProperty("online-mode", LogSM.booleanToString(server.getServerType(), onlineModeCheckBox.isSelected()));
                } else if (config.containsKey("xbox-auth")) {
                    config.setProperty("xbox-auth", LogSM.booleanToString(server.getServerType(), onlineModeCheckBox.isSelected()));
                }
                config.setProperty("white-list", LogSM.booleanToString(server.getServerType(), whitelistEnabledCheckBox.isSelected()));
                if (broadcastConsoleToOPsCheckBox.isEnabled()) {
                    config.setProperty("broadcast-console-to-ops", LogSM.booleanToString(server.getServerType(), broadcastConsoleToOPsCheckBox.isSelected()));
                }
                if (announcePlayersAchievementsCheckBox.isEnabled()) {
                    config.setProperty("announce-player-achievements", LogSM.booleanToString(server.getServerType(), announcePlayersAchievementsCheckBox.isSelected()));
                }
                if (hideOnlinePlayersCheckBox.isEnabled()) {
                    config.setProperty("hide-online-players", LogSM.booleanToString(server.getServerType(), hideOnlinePlayersCheckBox.isSelected()));
                }
                if (OPPermLevelComboBox.isEnabled()) {
                    config.setProperty("op-permission-level", String.valueOf(OPPermLevelComboBox.getSelectedItem()).split(" - ")[0]);
                }
                if (idleTimeoutKickSpinner.isEnabled()) {
                    config.setProperty("player-idle-timeout", String.valueOf(idleTimeoutKickSpinner.getValue()));
                }
                if (textFilteringConfigTextField.isEnabled()) {
                    config.setProperty("text-filtering-config", textFilteringConfigTextField.getText());
                }
                if (entityBroadcastRangeSpinner.isEnabled()) {
                    config.setProperty("entity-broadcast-range-percentage", String.valueOf(entityBroadcastRangeSpinner.getValue()));
                }

                if (networkCompressionThresholdSpinner.isEnabled()) {
                    config.setProperty("network-compression-threshold", String.valueOf(networkCompressionThresholdSpinner.getValue()));
                }
                if (resourcePackSHA1TextField.isEnabled()) {
                    config.setProperty(config.containsKey("resource-pack-sha1") ? "resource-pack-sha1" : "resource-pack-hash", resourcePackSHA1TextField.getText());
                }
                if (enableDebugCheckBox.isEnabled()) {
                    config.setProperty("debug", LogSM.booleanToString(server.getServerType(), enableDebugCheckBox.isSelected()));
                }
                if (enableJMXMonitoringCheckBox.isEnabled()) {
                    config.setProperty("enable-jmx-monitoring", LogSM.booleanToString(server.getServerType(), enableJMXMonitoringCheckBox.isSelected()));
                }
                if (functionPermissionLevelSpinner.isEnabled()) {
                    config.setProperty("function-permission-level", String.valueOf(functionPermissionLevelSpinner.getValue()));
                }
                if (enforceSecureProfileCheckBox.isEnabled()) {
                    config.setProperty("enforce-secure-profile", LogSM.booleanToString(server.getServerType(), enforceSecureProfileCheckBox.isSelected()));
                }
                if (enableChatPreviewCheckBox.isEnabled()) {
                    config.setProperty("previews-chat", LogSM.booleanToString(server.getServerType(), enableChatPreviewCheckBox.isSelected()));
                }
                if (maxChainedNeighborUpdatesSpinner.isEnabled()) {
                    config.setProperty("max-chained-neighbor-updates", String.valueOf(maxChainedNeighborUpdatesSpinner.getValue()));
                }

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

        enableQueryCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryPortSpinner.setEnabled(enableQueryCheckBox.isSelected());
            }
        });

        enableRCONCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RCONPortSpinner.setEnabled(enableRCONCheckBox.isSelected());
                RCONPasswordTextField.setEnabled(enableRCONCheckBox.isSelected());
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
                        if (server.getServerType().equals(ServerType.PocketMine)) {
                            File serverJar = new File("servers/" + server.getDirName() + "/PocketMine-MP.phar");
                            if (!serverJar.exists()) {
                                JOptionPane.showMessageDialog(null, "You must download the server software before you can run the server.", "LogSM", JOptionPane.ERROR_MESSAGE);
                                return;
                            } else {
                                startPocketmineServer(server, serverProperties);
                            }
                        } else {
                            File serverJar = new File("servers/" + server.getDirName() + "/server.jar");
                            File forgeServerDir = new File("servers/" + server.getDirName() + "/libraries/net/minecraftforge/forge");

                            if (!(forgeServerDir.exists() && server.getServerType().equals(ServerType.Forge)) && !serverJar.exists()) {
                                JOptionPane.showMessageDialog(null, "You must download the server software before you can run the server.", "LogSM", JOptionPane.ERROR_MESSAGE);
                                return;
                            } else {
                                File eula = new File("servers/" + server.getDirName() + "/eula.txt");
                                int i = 1;
                                if (!server.getServerType().equals(ServerType.Nukkit)) {
                                    if (!eula.exists()) {
                                        i = JOptionPane.showConfirmDialog(null, "Do you agree to the Minecraft EULA? (https://www.minecraft.net/en-us/eula)", "LogSM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                    } else {
                                        i = 0;
                                    }
                                }

                                if (i == JOptionPane.YES_OPTION || server.getServerType().equals(ServerType.Nukkit)) {
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

                                    startServer(server, serverProperties, forgeServerDir.exists());
                                } else {
                                    JOptionPane.showMessageDialog(null, "You must agree to the EULA to run the server.", "LogSM", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }
                        }
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
                        OFFLINEButton.setText("RESTARTING");

                        try {
                            dos.write("stop");
                            dos.flush();
                            dos.close();
                        } catch (IOException ioException) {

                        }

                        dlm.clear();
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

        downloadServerJARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (server.getServerType().equals(ServerType.Forge)) {
                    JOptionPane.showMessageDialog(null, "You must download the forge server files manually. (If you are unsure where to install, click on Open Server Files to open the directory to install.) You must rename the forge jar to server.jar", "LogSM", JOptionPane.ERROR_MESSAGE);
                } else if (server.getServerType().equals(ServerType.Fabric)) {
                    JOptionPane.showMessageDialog(null, "You must download and use the fabric installer to install the files to the server directory manually. (If you are unsure where to install, click on Open Server Files to open the directory to install.)", "LogSM", JOptionPane.ERROR_MESSAGE);
                } else {
                    UpdaterDialog updaterDialog = switch (server.getServerType()) {
                        case Vanilla -> new UpdaterDialog(server, NetworkClient.getMojangServerURL(server.getVersion()), null);
                        case CraftBukkit -> new UpdaterDialog(server, NetworkClient.getCraftBukkitURL(server.getVersion()), null);
                        case Paper -> new UpdaterDialog(server, NetworkClient.getPaperURL(server.getVersion()), null);
                        case Purpur -> new UpdaterDialog(server, NetworkClient.getPurpurURL(server.getVersion()), null);
                        case Spigot -> new UpdaterDialog(server, NetworkClient.getSpigotURL(server.getVersion()), null);
                        case Magma -> new UpdaterDialog(server, NetworkClient.getMagmaURL(server.getVersion()), null);
                        case Mohist -> new UpdaterDialog(server, NetworkClient.getMohistURL(server.getVersion()), null);
                        case SpongeVanilla -> new UpdaterDialog(server, NetworkClient.getSpongeVanillaURL(server.getVersion()), null);
                        case Nukkit -> new UpdaterDialog(server, NetworkClient.getNukkitURL(), null);
                        case PocketMine -> new UpdaterDialog(server, NetworkClient.getPocketMineURL(), NetworkClient.getPocketMinePHPURL());
                        default -> null;
                    };
                    updaterDialog.pack();
                    updaterDialog.setLocationRelativeTo(null);
                    updaterDialog.setVisible(true);
                }
            }
        });

        autoRestartInSecondsCheckBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoRestartSpinner.setEnabled(autoRestartInSecondsCheckBox.isSelected());
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
                    if (server.getServerType().equals(ServerType.Nukkit) || server.getServerType().equals(ServerType.PocketMine)) {
                        if (NetworkClient.checkUDPServer(LogSM.getExternalIP(), Long.valueOf(String.valueOf(serverPortSpinner.getValue())).intValue())) {
                            JOptionPane.showMessageDialog(null, "Your server is accessible to the internet!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Your server isn't accessible to the internet! Follow instructions online how to port forward.", "LogSM", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        if (NetworkClient.checkTCPServer(LogSM.getExternalIP(), Long.valueOf(String.valueOf(serverPortSpinner.getValue())).intValue())) {
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

        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!list1.isSelectionEmpty() && SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem opPlayer = new JMenuItem("OP Player");
                    JMenuItem deopPlayer = new JMenuItem("De-OP Player");
                    JMenuItem kickPlayer = new JMenuItem("Kick Player");
                    JMenuItem banPlayer = new JMenuItem("Ban Player");

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

                    banPlayer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                dos.write("ban " + list1.getSelectedValue());
                                dos.newLine();
                                dos.flush();
                            } catch (IOException ioException) {

                            }
                        }
                    });

                    popupMenu.add(opPlayer);
                    popupMenu.add(deopPlayer);
                    popupMenu.add(kickPlayer);
                    popupMenu.add(banPlayer);

                    popupMenu.show(list1, e.getX(), e.getY());
                }
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

    public void loadConfiguration(Server server, File serverProperties) throws IOException {
        fis = new FileInputStream(serverProperties);
        config.load(fis);

        if (serverProperties.exists()) {
            worldTextField.setText(config.getProperty("level-name"));
            seedTextField.setText(config.getProperty("level-seed"));
            generatorSettingsTextField.setText(config.getProperty("generator-settings"));
            if (!config.containsKey("max-world-size")) {
                maxWorldSizeSpinner.setEnabled(false);
            } else {
                maxWorldSizeSpinner.setValue(Long.parseLong(config.getProperty("max-world-size")));
            }
            String s2 = config.getProperty("level-type");
            if (s2 != null) {
                s2 = s2.toLowerCase().substring(0, 1).toUpperCase() + s2.toLowerCase().substring(1);
                levelTypeComboBox.setSelectedItem(s2);
            } else {
                levelTypeComboBox.setEnabled(false);
            }
            if (!config.containsKey("allow-nether")) {
                allowNetherCheckBox.setEnabled(false);
            } else {
                allowNetherCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("allow-nether")));
            }
            if (!config.containsKey("generate-structures")) {
                generateStructuresCheckBox.setEnabled(false);
            } else {
                generateStructuresCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("generate-structures")));
            }
            if (!config.containsKey("allow-flight")) {
                allowFlightCheckBox.setSelected(false);
            } else {
                allowFlightCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("allow-flight")));
            }
            if (config.containsKey("spawn-protection")) {
                spawnProtectionSpinner.setEnabled(true);
                spawnProtectionSpinner.setValue(Integer.parseInt(config.getProperty("spawn-protection")));
            }
            if (config.containsKey("max-tick-time")) {
                maxTickTimeSpinner.setEnabled(true);
                maxTickTimeSpinner.setValue(Long.parseLong(config.getProperty("max-tick-time")));
            }
            if (!config.containsKey("spawn-npcs")) {
                enableNPCSpawningCheckBox.setEnabled(false);
            } else {
                enableNPCSpawningCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("spawn-npcs")));
            }
            if (!config.containsKey("spawn-animals")) {
                enableAnimalSpawningCheckBox.setEnabled(false);
            } else {
                enableAnimalSpawningCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("spawn-animals")));
            }
            if (config.containsKey("spawn-monsters")) {
                enableMonsterSpawningCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("spawn-monsters")));
            } else if (config.containsKey("spawn-mobs")) {
                enableMonsterSpawningCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("spawn-mobs")));
            } else {
                enableAnimalSpawningCheckBox.setEnabled(false);
            }
            if (!config.containsKey("max-build-height")) {
                maxBuildHeightSpinner.setEnabled(false);
            }
            if (config.containsKey("simulation-distance")) {
                simulationDistanceSpinner.setEnabled(true);
            }

            try {
                int i = Integer.parseInt(config.getProperty("gamemode"));
                int i1 = Integer.parseInt(config.getProperty("difficulty"));

                gamemodeComboBox.setSelectedIndex(i);
                difficultyComboBox.setSelectedIndex(i1);
            } catch (NumberFormatException e) {
                String s = config.getProperty("gamemode");
                s = s.toLowerCase().substring(0, 1).toUpperCase() + s.toLowerCase().substring(1);
                String s1 = config.getProperty("difficulty");
                s1 = s1.toLowerCase().substring(0, 1).toUpperCase() + s1.toLowerCase().substring(1);

                gamemodeComboBox.setSelectedItem(s);
                difficultyComboBox.setSelectedItem(s1);
            }

            forcedGamemodeCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("force-gamemode")));
            hardcoreModeCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("hardcore")));
            if (!config.containsKey("max-build-height")) {
                maxBuildHeightSpinner.setEnabled(false);
            } else {
                maxBuildHeightSpinner.setValue(Integer.parseInt(config.getProperty("max-build-height")));
            }
            viewDistanceSpinner.setValue(Integer.parseInt(config.getProperty("view-distance")));
            MOTDTextField.setText(config.getProperty("motd"));
            if (!config.containsKey("sub-motd")) {
                subMOTDTextField.setEnabled(false);
            } else {
                subMOTDTextField.setText(config.getProperty("sub-motd"));
            }
            if (server.getServerType().equals(ServerType.Nukkit) || server.getServerType().equals(ServerType.PocketMine)) {
                resourcePackURLTextField.setEnabled(false);
            } else {
                resourcePackURLTextField.setText(config.getProperty("resource-pack"));
            }
            serverIPTextField.setText(config.getProperty("server-ip"));
            serverPortSpinner.setValue(Integer.parseInt(config.getProperty("server-port")));

            if (LogSM.parseBoolean(config.getProperty("enable-query"))) {
                enableQueryCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("enable-query")));
                queryPortSpinner.setEnabled(true);
            }
            if (config.containsKey("query.port")) {
                queryPortSpinner.setValue(Long.parseLong(config.getProperty("query.port")));
            }

            if (!config.containsKey("enable-rcon")) {
                enableRCONCheckBox.setEnabled(false);
                RCONPortSpinner.setEnabled(false);
                RCONPasswordTextField.setEnabled(false);
            } else if (LogSM.parseBoolean(config.getProperty("enable-rcon"))) {
                enableRCONCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("enable-rcon")));
                RCONPortSpinner.setValue(Long.parseLong(config.getProperty("rcon.port")));
                RCONPasswordTextField.setText(config.getProperty("rcon.password"));
                RCONPortSpinner.setEnabled(true);
                RCONPasswordTextField.setEnabled(true);
            }
            if (!config.containsKey("prevent-proxy-connections")) {
                blockProxyConnectionsCheckBox.setEnabled(false);
            } else {
                blockProxyConnectionsCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("prevent-proxy-connections")));
            }

            if (!config.containsKey("snooper-enabled")) {
                enableSnooperCheckBox.setEnabled(false);
            } else {
                enableSnooperCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("snooper-enabled")));
            }
            maxPlayersSpinner.setValue(Integer.parseInt(config.getProperty("max-players")));
            if (!config.containsKey("enable-command-block")) {
                enableCommandBlocksCheckBox.setEnabled(false);
            } else {
                enableCommandBlocksCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("enable-command-block")));
            }
            pvpEnabledCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("pvp")));
            if (config.containsKey("online-mode")) {
                onlineModeCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("online-mode")));
            } else if (config.containsKey("xbox-auth")) {
                onlineModeCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("xbox-auth")));
            }
            whitelistEnabledCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("white-list")));
            if (!config.containsKey("broadcast-console-to-ops")) {
                broadcastConsoleToOPsCheckBox.setEnabled(false);
            } else {
                broadcastConsoleToOPsCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("broadcast-console-to-ops")));
            }

            if (!config.containsKey("announce-player-achievements")) {
                announcePlayersAchievementsCheckBox.setEnabled(false);
            } else {
                announcePlayersAchievementsCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("announce-player-achievements")));
            }

            if (!config.containsKey("hide-online-players")) {
                hideOnlinePlayersCheckBox.setEnabled(false);
            } else {
                hideOnlinePlayersCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("hide-online-players")));
            }

            if (!config.containsKey("op-permission-level")) {
                OPPermLevelComboBox.setEnabled(false);
            } else {
                OPPermLevelComboBox.setSelectedIndex(Integer.parseInt(config.getProperty("op-permission-level")) - 1);
            }
            if (!config.containsKey("player-idle-timeout")) {
                idleTimeoutKickSpinner.setEnabled(false);
            } else {
                idleTimeoutKickSpinner.setValue(Integer.parseInt(config.getProperty("player-idle-timeout")));
            }

            if (!config.containsKey("network-compression-threshold")) {
                networkCompressionThresholdSpinner.setEnabled(false);
            } else {
                networkCompressionThresholdSpinner.setValue(Integer.parseInt(config.getProperty("network-compression-threshold")));
            }
        }

        if (server.getServerType().equals(ServerType.Nukkit) || server.getServerType().equals(ServerType.PocketMine)) {
            resourcePackPromptTextField.setEnabled(false);
            forceResourcePackCheckBox.setEnabled(false);
            enableStatusCheckBox.setEnabled(false);
            rateLimitSpinner.setEnabled(false);
            textFilteringConfigTextField.setEnabled(false);
            entityBroadcastRangeSpinner.setEnabled(false);
            resourcePackSHA1TextField.setEnabled(false);
            enableDebugCheckBox.setEnabled(false);
            enableJMXMonitoringCheckBox.setEnabled(false);
            enableSyncChunkWritingCheckBox.setEnabled(false);
            functionPermissionLevelSpinner.setEnabled(false);
        } else {
            if (serverProperties.exists()) {
                if (config.containsKey("resource-pack-prompt")) {
                    resourcePackPromptTextField.setText(config.getProperty("resource-pack-prompt"));
                } else {
                    resourcePackPromptTextField.setEnabled(false);
                }
                if (config.containsKey("require-resource-pack")) {
                    forceResourcePackCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("require-resource-pack")));
                } else {
                    forceResourcePackCheckBox.setEnabled(false);
                }
                if (config.containsKey("enable-status")) {
                    enableStatusCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("enable-status")));
                } else {
                    enableStatusCheckBox.setEnabled(false);
                }
                if (config.containsKey("rate-limit")) {
                    rateLimitSpinner.setValue(Integer.parseInt(config.getProperty("rate-limit")));
                } else {
                    rateLimitSpinner.setEnabled(false);
                }
                if (config.containsKey("text-filtering-config")) {
                    textFilteringConfigTextField.setText(config.getProperty("text-filtering-config"));
                } else {
                    textFilteringConfigTextField.setEnabled(false);
                }
                if (config.containsKey("entity-broadcast-range-percentage")) {
                    entityBroadcastRangeSpinner.setValue(Integer.parseInt(config.getProperty("entity-broadcast-range-percentage")));
                } else {
                    entityBroadcastRangeSpinner.setEnabled(false);
                }
                resourcePackSHA1TextField.setText(config.containsKey("resource-pack-hash") ? config.getProperty("resource-pack-hash") : config.getProperty("resource-pack-sha1"));
                if (config.containsKey("debug")) {
                    enableDebugCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("debug")));
                } else {
                    enableDebugCheckBox.setEnabled(false);
                }
                if (config.containsKey("enable-jmx-monitoring")) {
                    enableJMXMonitoringCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("enable-jmx-monitoring")));
                } else {
                    enableJMXMonitoringCheckBox.setEnabled(false);
                }
                if (config.containsKey("sync-chunk-writes")) {
                    enableSyncChunkWritingCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("sync-chunk-writes")));
                } else {
                    enableSyncChunkWritingCheckBox.setEnabled(false);
                }
                if (config.containsKey("function-permission-level")) {
                    functionPermissionLevelSpinner.setValue(Integer.parseInt(config.getProperty("function-permission-level")));
                } else {
                    functionPermissionLevelSpinner.setEnabled(false);
                }
                if (config.containsKey("enforce-secure-profile")) {
                    enforceSecureProfileCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("enforce-secure-profile")));
                } else {
                    enforceSecureProfileCheckBox.setEnabled(false);
                }
                if (config.containsKey("previews-chat")) {
                    enableChatPreviewCheckBox.setSelected(LogSM.parseBoolean(config.getProperty("previews-chat")));
                } else {
                    enableChatPreviewCheckBox.setEnabled(false);
                }
                if (config.containsKey("max-chained-neighbor-updates")) {
                    maxChainedNeighborUpdatesSpinner.setValue(Integer.parseInt(config.getProperty("max-chained-neighbor-updates")));
                } else {
                    maxChainedNeighborUpdatesSpinner.setEnabled(false);
                }

                fis.close();
            }
        }

    }

    public void startPocketmineServer(Server server, File serverProperties) {
        if (!consoleTextPane.getText().isEmpty()) {
            consoleTextPane.setText("");
        }

        List<String> commands = new ArrayList<>();
        File phpExe;
        if (System.getProperty("os.name").startsWith("Windows")) {
            phpExe = new File("servers/" + server.getDirName() + "/bin/php/php.exe");
            commands.add(phpExe.getAbsolutePath());
            commands.add("-c");
            commands.add("bin\\php");
            commands.add("PocketMine-MP.phar");
        } else {
            commands.add("./start.sh");
        }
        if (!serverArgsTextField.getText().isEmpty()) {
            commands.addAll(Arrays.asList(serverArgsTextField.getText().split(" ")));
        }
        commands.add("--no-wizard");
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
                        fis.close();
                        loadConfiguration(server, serverProperties);
                    } catch (IOException ioException) {

                    }
                    dos = null;

                    startPocketmineServer(server, serverProperties);
                } else if (OFFLINEButton.getText().equals("AUTO-RESTARTING")) {
                    consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                    OFFLINEButton.setText("RESTARTING");
                    list1.removeAll();
                    p = null;
                    try {
                        dos.close();
                        loadConfiguration(server, serverProperties);
                        fis.close();
                    } catch (IOException ioException) {

                    }
                    dos = null;

                    startPocketmineServer(server, serverProperties);
                } else if (OFFLINEButton.getText().equals("STOPPING")) {
                    consoleTextPane.append("\n" + "Server Stopped or Crashed");
                    consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                    list1.removeAll();
                    p = null;
                    try {
                        dos.close();
                        loadConfiguration(server, serverProperties);
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
                manager.openUDPPort(server, localIPLabel.getText(), Integer.parseInt(config.getProperty("server-port")));
            }
            if (!OFFLINEButton.getText().equals("RESTARTING")) {
                OFFLINEButton.setText("STARTING");
            }
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "Can't start PocketMine Server! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void startServer(Server server, File serverProperties, boolean forgeFileArgsExist) {
        if (!consoleTextPane.getText().isEmpty()) {
            consoleTextPane.setText("");
        }

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
        if (forgeFileArgsExist) {
            commands.add("@libraries/net/minecraftforge/forge/" + new File("servers/" + server.getDirName() + "/libraries/net/minecraftforge/forge").listFiles()[0].getName() + (System.getProperty("os.name").contains("Windows") ? "/win_args.txt" : "/unix_args.txt"));
        } else {
            commands.add("-jar");
            if (server.getServerType().equals(ServerType.Fabric)) {
                commands.add(new File("servers/" + server.getDirName() + "/fabric-server-launch.jar").getAbsolutePath());
            } else {
                commands.add(new File("servers/" + server.getDirName() + "/server.jar").getAbsolutePath());
            }
            if (server.getServerType().equals(ServerType.Nukkit)) {
                commands.add("--disable-ansi");
            }
        }
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
                        loadConfiguration(server, serverProperties);
                        fis.close();
                    } catch (IOException ioException) {

                    }
                    dos = null;

                    startServer(server, serverProperties, forgeFileArgsExist);
                } else if (OFFLINEButton.getText().equals("STOPPING")) {
                    consoleTextPane.append("\n" + "Server Stopped or Crashed");
                    consoleTextPane.setCaretPosition(consoleTextPane.getDocument().getLength());
                    list1.removeAll();
                    OFFLINEButton.setText("OFFLINE");
                    cpuUsageLabel.setText("CPU Usage: 0%");
                    ramLabel.setText("RAM Usage: 0 MB");
                    p = null;
                    try {
                        dos.close();
                        loadConfiguration(server, serverProperties);
                        fis.close();
                    } catch (IOException ioException) {

                    }
                    if (UPnPPortForwardCheckBox.isSelected()) {
                        if (server.getServerType().equals(ServerType.Nukkit)) {
                            manager.closeUDPPort(server, localIPLabel.getText(), Integer.parseInt(config.getProperty("server-port")));
                        } else {
                            manager.closeTCPPort(server, localIPLabel.getText(), Integer.parseInt(config.getProperty("server-port")));
                        }
                    }
                    dos = null;
                }
            });
            if (UPnPPortForwardCheckBox.isSelected()) {
                if (server.getServerType().equals(ServerType.Nukkit)) {
                    if (!manager.isUDPOpen()) {
                        manager.openUDPPort(server, localIPLabel.getText(), Integer.parseInt(config.getProperty("server-port")));
                    }
                } else {
                    if (!manager.isTCPOpen()) {
                        manager.openTCPPort(server, localIPLabel.getText(), Integer.parseInt(config.getProperty("server-port")));
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

class PlayerListRenderer extends JLabel implements ListCellRenderer<String> {

    private Server server;
    private boolean onlineMode;

    public PlayerListRenderer(Server server, boolean onlineMode) {
        this.server = server;
        this.onlineMode = onlineMode;

        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value);

        try {
            if (server.getServerType().equals(ServerType.Nukkit) || server.getServerType().equals(ServerType.PocketMine) || server.getServerType().equals(ServerType.Bedrock) || !onlineMode) {
                setIcon(new ImageIcon(ImageIO.read(LogSM.class.getClassLoader().getResourceAsStream("Steve.png"))));
            } else {
                setIcon(new ImageIcon(ImageIO.read(new URL("https://mc-heads.net/avatar/" + value + "/22.png"))));
            }
        } catch (IOException e) {
            try {
                setIcon(new ImageIcon(ImageIO.read(LogSM.class.getClassLoader().getResourceAsStream("Steve.png"))));
            } catch (IOException ioException) {

            }
        }
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
