package me.Logicism.LogSM.network;

import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.gui.UpdaterDialog;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BedrockDownload implements Runnable {

    private UpdaterDialog updaterDialog;
    private Server server;
    private URL url;
    private JLabel downloadLabel;
    private JProgressBar progressBar;
    private JLabel speedLabel;

    public BedrockDownload(UpdaterDialog updaterDialog, Server server, URL url, JLabel downloadLabel, JProgressBar progressBar, JLabel speedLabel) {
        this.updaterDialog = updaterDialog;
        this.server = server;
        this.url = url;
        this.downloadLabel = downloadLabel;
        this.progressBar = progressBar;
        this.speedLabel = speedLabel;
    }

    @Override
    public void run() {
        if (url != null) {
            try {
                downloadLabel.setText("Downloading " + server.getServerType().name() + " Software.....");
                updaterDialog.pack();

                BrowserData bd = NetworkClient.executeGETRequest(url);

                int resCode = bd.getResponseCode();

                if (resCode == 200) {
                    long fileSize = bd.getResponseLength();
                    long downloadedFileSize = 0;

                    BufferedInputStream bis = new BufferedInputStream(bd.getResponse());
                    File file = new File("bedrock-server.zip");
                    FileOutputStream fos = new FileOutputStream(file);
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    long start = System.nanoTime();
                    while ((bytesRead = bis.read(dataBuffer, 0, 1024)) >= 0) {
                        long elapsedTime = System.nanoTime() - start;

                        downloadedFileSize += bytesRead;

                        progressBar.setValue(Math.toIntExact((downloadedFileSize * 100) / fileSize));
                        speedLabel.setText(Math.toIntExact((downloadedFileSize * 100) / fileSize) + "% (" + ((int) 1000000000.0 / (1024* 1024) * downloadedFileSize / + (elapsedTime + 1)) + " mb/s)");

                        fos.write(dataBuffer, 0, bytesRead);
                    }

                    bd.getResponse().close();
                    fos.flush();
                    fos.close();

                    downloadLabel.setText("Extracting " + server.getServerType().name() + " Software.....");
                    updaterDialog.pack();

                    LogSM.unzipFile(file, "servers/" + server.getDirName(), progressBar, speedLabel);

                    file.delete();

                    if (System.getProperty("os.name").startsWith("Windows")) {
                        JOptionPane.showMessageDialog(null, "Download Complete!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                        updaterDialog.dispose();
                    } else {
                        List<String> commands = new ArrayList<>();
                        commands.add("chmod");
                        commands.add("+x");
                        commands.add("bedrock_server");
                        ProcessBuilder processBuilder = new ProcessBuilder(commands).directory(new File("servers/" + server.getDirName()));
                        Process process = processBuilder.start();
                        while (process.isAlive()) {
                            System.out.println("");
                        }

                        process.onExit().thenAccept(p -> {
                            JOptionPane.showMessageDialog(null, "Download Complete!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                            updaterDialog.dispose();
                        });
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Download Failed! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
                updaterDialog.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Download Failed! Cannot retrieve URL!", "LogSM", JOptionPane.ERROR_MESSAGE);
            updaterDialog.dispose();
        }
    }
}
