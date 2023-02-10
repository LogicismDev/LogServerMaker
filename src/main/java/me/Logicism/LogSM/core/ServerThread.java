package me.Logicism.LogSM.core;

import me.Logicism.LogSM.gui.BedrockServerPage;
import me.Logicism.LogSM.gui.ConfigServerPage;
import me.Logicism.LogSM.gui.ServerPage;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerThread implements Runnable {

    private final JFrame serverPage;
    private final Server server;
    private final Process p;
    private final JButton OFFLINEButton;
    private final JTextArea textArea;
    private final DefaultListModel<String> dlm;
    private ScheduledFuture sf;

    private int autoRestartSeconds;

    public ServerThread(JFrame serverPage, Server server, Process p, JButton OFFLINEButton, JTextArea textArea, DefaultListModel<String> dlm) {
        this.serverPage = serverPage;
        this.server = server;
        this.p = p;
        this.OFFLINEButton = OFFLINEButton;
        this.textArea = textArea;
        this.dlm = dlm;
        this.autoRestartSeconds = server.getAutoRestart();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(new SequenceInputStream(p.getInputStream(), p.getErrorStream()));
        DataOutputStream dos = new DataOutputStream(p.getOutputStream());
        
        while (p.isAlive()) {
            while (scanner.hasNextLine()) {
                try {
                    String line = scanner.nextLine();

                    if (server.getServerType().equals(ServerType.Geyser)) {
                        line = line.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
                    } else if (server.getServerType().equals(ServerType.Nukkit) || server.getServerType().equals(ServerType.WaterdogPE)) {
                        line = line.replaceAll(" \u001B\\[[\\d;]*[^\\d;]", "").replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
                    }

                    if (((line.contains("INFO]: Done (") || line.contains("INFO] Done (")) && line.contains("! For help, type \"help\"")) || (line.contains("INFO] Done (") && line.contains("! Run /geyser help for help!")) || (line.contains(" INFO] Server started.")) || line.contains("INFO] Listening on /") || line.contains("INFO]: Listening on /") || line.endsWith("[FML]: Unloading dimension -1") || line.contains("[minecraft/DedicatedServer]: Done") || line.contains("[INFO]: Server started on ")) {
                        OFFLINEButton.setText("ONLINE");

                        if (server.getAutoRestart() != -1) {
                            if (serverPage instanceof ServerPage) {
                                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                                sf = ses.scheduleAtFixedRate(new Runnable() {
                                    @Override
                                    public void run() {
                                        autoRestartSeconds--;
                                        if (autoRestartSeconds == 900) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 15 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 600) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 10 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 300) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 5 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 180) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 3 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 120) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 2 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 60) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 1 MINUTE**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 0) {
                                            OFFLINEButton.setText("AUTO-RESTARTING");
                                            ((ServerPage) serverPage).restartServer();
                                        }
                                    }
                                },0, 1, TimeUnit.SECONDS);
                            } else if (serverPage instanceof BedrockServerPage) {
                                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                                sf = ses.scheduleAtFixedRate(new Runnable() {
                                    @Override
                                    public void run() {
                                        autoRestartSeconds--;
                                        if (autoRestartSeconds == 900) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 15 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 600) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 10 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 300) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 5 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 180) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 3 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 120) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 2 MINUTES**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 60) {
                                            try {
                                                dos.writeUTF("say **SERVER RESTARTS IN 1 MINUTE**");
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        }
                                        if (autoRestartSeconds == 0) {
                                            OFFLINEButton.setText("AUTO-RESTARTING");
                                            ((BedrockServerPage) serverPage).restartServer();
                                        }
                                    }
                                },0, 1, TimeUnit.SECONDS);
                            } else if (serverPage instanceof ConfigServerPage) {
                                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                                sf = ses.scheduleAtFixedRate(new Runnable() {
                                    @Override
                                    public void run() {
                                        autoRestartSeconds--;
                                        if (server.getServerType().equals(ServerType.BungeeCord) || server.getServerType().equals(ServerType.Waterfall)) {
                                            if (autoRestartSeconds == 900) {
                                                try {
                                                    dos.writeUTF("alert **SERVER RESTARTS IN 15 MINUTES**");
                                                } catch (IOException ioException) {
                                                    ioException.printStackTrace();
                                                }
                                            }
                                            if (autoRestartSeconds == 600) {
                                                try {
                                                    dos.writeUTF("alert **SERVER RESTARTS IN 10 MINUTES**");
                                                } catch (IOException ioException) {
                                                    ioException.printStackTrace();
                                                }
                                            }
                                            if (autoRestartSeconds == 300) {
                                                try {
                                                    dos.writeUTF("alert **SERVER RESTARTS IN 5 MINUTES**");
                                                } catch (IOException ioException) {
                                                    ioException.printStackTrace();
                                                }
                                            }
                                            if (autoRestartSeconds == 180) {
                                                try {
                                                    dos.writeUTF("alert **SERVER RESTARTS IN 3 MINUTES**");
                                                } catch (IOException ioException) {
                                                    ioException.printStackTrace();
                                                }
                                            }
                                            if (autoRestartSeconds == 120) {
                                                try {
                                                    dos.writeUTF("alert **SERVER RESTARTS IN 2 MINUTES**");
                                                } catch (IOException ioException) {
                                                    ioException.printStackTrace();
                                                }
                                            }
                                            if (autoRestartSeconds == 60) {
                                                try {
                                                    dos.writeUTF("alert **SERVER RESTARTS IN 1 MINUTE**");
                                                } catch (IOException ioException) {
                                                    ioException.printStackTrace();
                                                }
                                            }
                                            if (autoRestartSeconds == 0) {
                                                OFFLINEButton.setText("AUTO-RESTARTING");
                                                ((ConfigServerPage) serverPage).restartServer(server);
                                            }
                                        }
                                    }
                                },0, 1, TimeUnit.SECONDS);
                            }
                        }
                    }

                    if ((server.getServerType().equals(ServerType.Vanilla) || server.getServerType().equals(ServerType.CraftBukkit) || server.getServerType().equals(ServerType.Spigot) || server.getServerType().equals(ServerType.Paper) || server.getServerType().equals(ServerType.Purpur) || server.getServerType().equals(ServerType.Forge) || server.getServerType().equals(ServerType.SpongeVanilla) || server.getServerType().equals(ServerType.Fabric) || server.getServerType().equals(ServerType.Nukkit) || server.getServerType().equals(ServerType.PocketMine)) && line.contains("logged in with entity id")) {
                        String username;
                        if (server.getServerType().equals(ServerType.Nukkit)) {
                            username = line.split("\\[/")[0].split("] ")[1];
                        } else {
                            username = line.split("\\[/")[0].split(": ")[1];
                        }
                        dlm.addElement(username);
                    } else if (server.getServerType().equals(ServerType.Bedrock) && line.contains("Player connected:")) {
                        String username = line.split(", xuid:")[0].split("connected: ")[1];
                        dlm.addElement(username);
                    } else if (server.getServerType().equals(ServerType.Geyser) && line.contains("(logged in as: ") && line.contains("has connected to remote java server on address")) {
                        String username = line.split(" \\(logged")[0].split("] ")[1];
                        dlm.addElement(username);
                    } else if (((server.getServerType().equals(ServerType.BungeeCord) || server.getServerType().equals(ServerType.Waterfall) || server.getServerType().equals(ServerType.WaterdogPE))) && line.contains("] <->")) {
                        String username = line.split("] <->")[0].split("INFO] \\[")[1].split("\\|")[0];
                        dlm.addElement(username);
                    } else if (server.getServerType().equals(ServerType.Velocity) && line.contains("INFO]: [connected player] ") && line.contains("has connected")) {
                        String username = line.split("INFO]: \\[connected player] ")[1].split("\\(/")[0];
                        if (!dlm.contains(username)) {
                            dlm.addElement(username);
                        }
                    } else if (server.getServerType().equals(ServerType.NanoLimbo) && line.contains(" connected (")) {
                        String username = line.split("INFO]: Player ")[1].split(" connected \\(/")[0];
                        dlm.addElement(username);
                    }

                    if ((server.getServerType().equals(ServerType.Vanilla) || server.getServerType().equals(ServerType.CraftBukkit) || server.getServerType().equals(ServerType.Spigot) || server.getServerType().equals(ServerType.Paper) || server.getServerType().equals(ServerType.Purpur) || server.getServerType().equals(ServerType.Forge) || server.getServerType().equals(ServerType.SpongeVanilla) || server.getServerType().equals(ServerType.Fabric)) && line.contains("lost connection: ")) {
                        String username = line.split(" lost connection: ")[0].split(": ")[1];
                        dlm.removeElement(username);
                    } else if ((server.getServerType().equals(ServerType.Nukkit) || server.getServerType().equals(ServerType.PocketMine)) && line.contains(" logged out due to")) {
                        String username = null;
                        if (server.getServerType().equals(ServerType.Nukkit)) {
                            username = line.split("\\[/")[0].split("] ")[1];
                        } else if (server.getServerType().equals(ServerType.PocketMine)) {
                            username = line.split("\\[/")[0].split(": ")[1];
                        }
                        dlm.removeElement(username);
                    } else if (server.getServerType().equals(ServerType.Bedrock) && line.contains("Player disconnected:")) {
                        String username = line.split(", xuid:")[0].split("disconnected: ")[1];
                        dlm.removeElement(username);
                    } else if (server.getServerType().equals(ServerType.Geyser) && line.contains("has disconnected from remote Java server on address")) {
                        String username = line.split(" has disconnected")[0].split("] ")[1];
                        dlm.removeElement(username);
                    } else if (((server.getServerType().equals(ServerType.BungeeCord) || server.getServerType().equals(ServerType.Waterfall) || server.getServerType().equals(ServerType.WaterdogPE))) && line.contains("] <->")) {
                        String username = line.split("] -> UpstreamBridge has disconnected")[0].split("INFO] \\[")[1].split("\\|")[1];
                        dlm.removeElement(username);
                    } else if (server.getServerType().equals(ServerType.Velocity) && line.contains("INFO]: [connected player] ") && line.contains("has disconnected")) {
                        String username = line.split("INFO]: \\[connected player] ")[1].split("\\(/")[0];
                        dlm.removeElement(username);
                    } else if (server.getServerType().equals(ServerType.NanoLimbo) && line.contains(" disconnected")) {
                        String username = line.split("INFO]: Player ")[1].split(" disconnected")[0];
                        dlm.removeElement(username);
                    }

                    String finalLine = line;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            textArea.append(finalLine + "\n");
                            trunkTextArea(textArea);
                            textArea.setCaretPosition(textArea.getDocument().getLength());
                        }
                    });
                } catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) {
                    ignored.printStackTrace();
                }
            }
        }

        if (sf != null && !sf.isDone()) {
            sf.cancel(true);

            sf = null;
        }
    }

    public void trunkTextArea(JTextArea txtWin) {
        int numLinesToTrunk = txtWin.getLineCount() - 256;
        if(numLinesToTrunk > 0)
        {
            try
            {
                int posOfLastLineToTrunk = txtWin.getLineEndOffset(numLinesToTrunk - 1);
                txtWin.replaceRange("",0,posOfLastLineToTrunk);
            }
            catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
}
