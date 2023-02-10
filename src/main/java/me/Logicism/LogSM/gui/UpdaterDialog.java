package me.Logicism.LogSM.gui;

import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.core.ServerType;
import me.Logicism.LogSM.network.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class UpdaterDialog extends JDialog {
    private JPanel contentPane;
    private JProgressBar progressBar1;
    private JLabel downloadLabel;
    private JLabel speedLabel;

    public UpdaterDialog(Server server, URL url, URL url2) {
        try {
            setIconImage(ImageIO.read(LogSM.class.getClassLoader().getResourceAsStream("icon.png")));
        } catch (IOException ignored) {

        }

        setTitle("LogSM");

        setContentPane(contentPane);
        setModal(true);

        if (server != null) {
            if (server.getServerType().equals(ServerType.PocketMine)) {
                new Thread(new PocketMineDownload(this, server, url, url2, downloadLabel, progressBar1, speedLabel)).start();
            } else if (server.getServerType().equals(ServerType.Bedrock)) {
                new Thread(new BedrockDownload(this, server, url, downloadLabel, progressBar1, speedLabel)).start();
            } else {
                new Thread(new Download(this, server, url, downloadLabel, progressBar1, speedLabel)).start();
            }
        } else {
            downloadLabel.setText("Downloading LogSM Updater.....");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BrowserData bd = NetworkClient.executeGETRequest(new URL("https://logicism.tv/downloads/LogSMUpdater" + (System.getProperty("os.name").startsWith("Windows") ? ".exe" : ".jar")));

                        int resCode = bd.getResponseCode();

                        if (resCode == 200) {
                            long fileSize = bd.getResponseLength();
                            long downloadedFileSize = 0;

                            BufferedInputStream bis = new BufferedInputStream(bd.getResponse());
                            File file = new File("updater/LogSMUpdater" + (System.getProperty("os.name").startsWith("Windows") ? ".exe" : ".jar"));
                            FileOutputStream fos = new FileOutputStream(file);
                            byte dataBuffer[] = new byte[1024];
                            int bytesRead;
                            long start = System.nanoTime();
                            while ((bytesRead = bis.read(dataBuffer, 0, 1024)) >= 0) {
                                long elapsedTime = System.nanoTime() - start;

                                downloadedFileSize += bytesRead;

                                progressBar1.setValue(Math.toIntExact((downloadedFileSize * 100) / fileSize));
                                speedLabel.setText(Math.toIntExact((downloadedFileSize * 100) / fileSize) + "% (" + ((int) 1000000000.0 / (1024* 1024) * downloadedFileSize / + (elapsedTime + 1)) + " mb/s)");

                                fos.write(dataBuffer, 0, bytesRead);
                            }

                            bd.getResponse().close();
                            fos.flush();
                            fos.close();

                            dispose();
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Download Failed! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

                        dispose();
                    }
                }
            }).start();
        }
    }
}
