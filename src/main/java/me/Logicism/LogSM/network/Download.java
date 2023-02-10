package me.Logicism.LogSM.network;

import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.gui.UpdaterDialog;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Download implements Runnable {

    private UpdaterDialog updaterDialog;
    private Server server;
    private URL url;
    private JLabel downloadLabel;
    private JProgressBar progressBar;
    private JLabel speedLabel;

    public Download(UpdaterDialog updaterDialog, Server server, URL url, JLabel downloadLabel, JProgressBar progressBar, JLabel speedLabel) {
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
                downloadLabel.setText("Downloading " + server.getServerType().name() + "" + (server.getVersion() != null ? " " + server.getVersion() : "") + " Software.....");
                updaterDialog.pack();

                BrowserData bd = NetworkClient.executeGETRequest(url);

                int resCode = bd.getResponseCode();

                if (resCode == 200) {
                    long fileSize = bd.getResponseLength();
                    long downloadedFileSize = 0;

                    BufferedInputStream bis = new BufferedInputStream(bd.getResponse());
                    File file = new File("servers/" + server.getDirName() + "/server.jar");
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

                    JOptionPane.showMessageDialog(null, "Download Complete!", "LogSM", JOptionPane.INFORMATION_MESSAGE);

                    updaterDialog.dispose();
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
