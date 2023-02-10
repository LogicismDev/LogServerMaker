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

public class PocketMineDownload implements Runnable {

    private UpdaterDialog updaterDialog;
    private Server server;
    private URL url;
    private URL url2;
    private JLabel downloadLabel;
    private JProgressBar progressBar;
    private JLabel speedLabel;

    public PocketMineDownload(UpdaterDialog updaterDialog, Server server, URL url, URL url2, JLabel downloadLabel, JProgressBar progressBar, JLabel speedLabel) {
        this.updaterDialog = updaterDialog;
        this.server = server;
        this.url = url;
        this.url2 = url2;
        this.downloadLabel = downloadLabel;
        this.progressBar = progressBar;
        this.speedLabel = speedLabel;
    }

    @Override
    public void run() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                downloadLabel.setText("Downloading " + server.getServerType().name() + " Software.....");
                updaterDialog.pack();

                BrowserData bd = NetworkClient.executeGETRequest(url);

                int resCode = bd.getResponseCode();

                if (resCode == 200) {
                    long fileSize = bd.getResponseLength();
                    long downloadedFileSize = 0;

                    BufferedInputStream bis = new BufferedInputStream(bd.getResponse());
                    File file = new File("servers/" + server.getDirName() + "/PocketMine-MP.phar");
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
                }

                downloadLabel.setText("Downloading " + server.getServerType().name() + " PHP.....");
                updaterDialog.pack();

                bd = NetworkClient.executeGETRequest(url2);

                resCode = bd.getResponseCode();

                if (resCode == 200) {
                    long fileSize = bd.getResponseLength();
                    long downloadedFileSize = 0;

                    BufferedInputStream bis = new BufferedInputStream(bd.getResponse());
                    File file = new File("PHP.zip");
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

                    downloadLabel.setText("Extracting " + server.getServerType().name() + " " + server.getVersion() + " .....");

                    LogSM.unzipFile(file, "servers/" + server.getDirName(), progressBar, speedLabel);

                    file.delete();
                }

                JOptionPane.showMessageDialog(null, "Download Complete!", "LogSM", JOptionPane.INFORMATION_MESSAGE);

                updaterDialog.dispose();
            } else {
                downloadLabel.setText("Downloading " + server.getServerType().name() + " Software.....");
                updaterDialog.pack();

                BrowserData bd = NetworkClient.executeGETRequest(url);

                int resCode = bd.getResponseCode();

                if (resCode == 200) {
                    long fileSize = bd.getResponseLength();
                    long downloadedFileSize = 0;

                    BufferedInputStream bis = new BufferedInputStream(bd.getResponse());
                    File file = new File("servers/" + server.getDirName() + "/installer.sh");
                    FileOutputStream fos = new FileOutputStream(file);
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(dataBuffer, 0, 1024)) >= 0) {
                        downloadedFileSize += bytesRead;

                        progressBar.setValue(Math.toIntExact((downloadedFileSize * 100) / fileSize));

                        fos.write(dataBuffer, 0, bytesRead);
                    }

                    bd.getResponse().close();
                    fos.flush();
                    fos.close();

                    List<String> commands = new ArrayList<>();
                    commands.add("chmod");
                    commands.add("+x");
                    commands.add("installer.sh");
                    ProcessBuilder processBuilder = new ProcessBuilder(commands).directory(new File("servers/" + server.getDirName()));
                    processBuilder.start();

                    commands = new ArrayList<>();
                    commands.add("./installer.sh");
                    processBuilder = new ProcessBuilder(commands).directory(new File("servers/" + server.getDirName()));

                    Process process = processBuilder.start();
                    while (process.isAlive()) {
                        System.out.println("");
                    }

                    commands = new ArrayList<>();
                    commands.add("chmod");
                    commands.add("+x");
                    commands.add("start.sh");
                    processBuilder = new ProcessBuilder(commands).directory(new File("servers/" + server.getDirName()));
                    process = processBuilder.start();
                    process.onExit().thenAccept(p -> {
                        JOptionPane.showMessageDialog(null, "Download Complete!", "LogSM", JOptionPane.INFORMATION_MESSAGE);

                        updaterDialog.dispose();
                    });

                    file.delete();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Download Failed! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

            updaterDialog.dispose();
        }
    }
}
