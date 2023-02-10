package me.Logicism.LogSM;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import me.Logicism.LogSM.core.Server;
import me.Logicism.LogSM.core.ServerType;
import me.Logicism.LogSM.gui.ServerSelection;
import me.Logicism.LogSM.gui.UpdateDialog;
import me.Logicism.LogSM.gui.UpdaterDialog;
import me.Logicism.LogSM.network.BrowserData;
import me.Logicism.LogSM.network.NetworkClient;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LogSM {

    public static List<String> vanillaVersions = new ArrayList<>();
    public static List<String> paperVersions = new ArrayList<>();
    public static List<String> purpurVersions = new ArrayList<>();
    public static List<String> spigotVersions = Arrays.asList("1.19.3", "1.19.2", "1.19.1", "1.19", "1.18.2", "1.18.1", "1.18", "1.17.1", "1.17", "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.15.2", "1.15.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.14.1", "1.14", "1.13.2", "1.13.1", "1.13", "1.12.2", "1.12.1", "1.12", "1.11.2", "1.11.1", "1.11", "1.10.2", "1.10", "1.9.4", "1.9.2", "1.9.1", "1.9", "1.8.8", "1.8.7", "1.8.6", "1.8.5", "1.8.4", "1.8.3", "1.8.2", "1.8.1", "1.8", "1.7.10");
    public static List<String> forgeVersions = Arrays.asList("1.19.3", "1.19.2", "1.19.1", "1.19", "1.18.2", "1.18.1", "1.18", "1.17.1", "1.16.5", "1.16.4", "1.16.3", "1.16.2", "1.16.1", "1.15", "1.14.4", "1.14.3", "1.14.2", "1.13.2", "1.12.2", "1.12.1", "1.12", "1.11.2", "1.11", "1.10.2", "1.10", "1.9.4", "1.9", "1.8.9", "1.8.8", "1.8", "1.7.10");
    public static List<String> magmaVersions = Arrays.asList("1.18.2", "1.16.5", "1.12.2");
    public static List<String> mohistVersions = Arrays.asList("1.19.3", "1.18.2", "1.16.5", "1.12.2", "1.7.10");
    public static List<String> spongeVanillaVersions = Arrays.asList("1.19.3", "1.19.2", "1.18.2", "1.18.1", "1.17.1", "1.16.5", "1.16.4", "1.15.2", "1.12.2", "1.11.2", "1.10.2", "1.9.4", "1.8.9");
    public static List<String> fabricVersions = new ArrayList<>();

    public static String VERSION = "3.5";

    private static List<Server> serverList = new ArrayList<>();
    private static String externalIP;
    private static Map<String, String> settings;

    public static void main(String[] args) {
        UIManager.installLookAndFeel("Flat Light", FlatLightLaf.class.getCanonicalName());
        UIManager.installLookAndFeel("Flat Dark", FlatDarkLaf.class.getCanonicalName());
        UIManager.installLookAndFeel("Flat IntelliJ", FlatIntelliJLaf.class.getCanonicalName());
        UIManager.installLookAndFeel("Flat Darcula", FlatDarculaLaf.class.getCanonicalName());

        try {
            BrowserData bd = NetworkClient.executeGETRequest(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"));

            BufferedReader br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            bd.getResponse().close();

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray versions = jsonObject.getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                if (versions.getJSONObject(i).getString("id").equals("1.7.9")) {
                    break;
                }
                vanillaVersions.add(versions.getJSONObject(i).getString("id"));
            }

            bd = NetworkClient.executeGETRequest(new URL("https://papermc.io/api/v2/projects/paper"));

            br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            bd.getResponse().close();

            jsonObject = new JSONObject(sb.toString());
            versions = jsonObject.getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                paperVersions.add(0, versions.getString(i));
            }

            bd = NetworkClient.executeGETRequest(new URL("https://api.purpurmc.org/v2/purpur/"));

            br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            bd.getResponse().close();

            jsonObject = new JSONObject(sb.toString());
            versions = jsonObject.getJSONArray("versions");
            for (int i = versions.length(); i > 0; i--) {
                purpurVersions.add(0, versions.getString(i - 1));
            }

            Collections.reverse(purpurVersions);

            bd = NetworkClient.executeGETRequest(new URL("https://meta.fabricmc.net/v2/versions/game"));

            br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            bd.getResponse().close();

            versions = new JSONArray(sb.toString());
            for (int i = 0; i < versions.length(); i++) {
                fabricVersions.add(versions.getJSONObject(i).getString("version"));
            }
        } catch (IOException | JSONException e) {

        }

        File settingsFile = new File("logsm.settings");
        if (!settingsFile.exists()) {
            settings = new HashMap<>();
            settings.put("Theme", UIManager.getSystemLookAndFeelClassName());
            try {
                saveSettings();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Can't Save Settings! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

                return;
            }
        } else {
            try {
                loadSettings();
            } catch (IOException | ClassNotFoundException ioException) {
                JOptionPane.showMessageDialog(null, "Can't Load Settings! " + ioException.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);

                return;
            }
        }

        try {
            UIManager.setLookAndFeel(settings.get("Theme"));
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            JOptionPane.showMessageDialog(null, "We can't set your previous theme! We are setting it to your system based theme!", "LogSM", JOptionPane.ERROR_MESSAGE);

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException ignored) {

            }
        }

        externalIP = NetworkClient.getExternalIP();

        File serversDir = new File("servers");

        if (!serversDir.exists()) {
            serversDir.mkdir();
        }

        try {
            for (File file : serversDir.listFiles()) {
                File logSMJSON = new File(file, "logsm.json");

                if (logSMJSON.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(logSMJSON));
                    String s;
                    StringBuilder sb = new StringBuilder();

                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                    }

                    br.close();

                    JSONObject jsonObject = new JSONObject(sb.toString());

                    Server server = new Server(jsonObject, file.getName());
                    serverList.add(server);
                }
            }

        } catch (IOException e) {

        }

        ServerSelection serverSelection = new ServerSelection();
        serverSelection.pack();
        serverSelection.setLocationRelativeTo(null);
        serverSelection.setVisible(true);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Runtime.getRuntime().gc();
            }
        }, 5, 5, TimeUnit.MINUTES);

        if (LogSM.getSettings().containsKey("Check for Updates") && LogSM.getSettings().get("Check for Updates").equals("true")) {
            File updaterdir = new File("updater");
            if (!updaterdir.exists()) {
                updaterdir.mkdir();
            }

            File file = new File("updater/LogSMUpdater" + (System.getProperty("os.name").startsWith("Windows") ? ".exe" : ".jar"));

            if (!file.exists()) {
                UpdaterDialog updaterDialog = new UpdaterDialog(null, null, null);
                updaterDialog.pack();
                updaterDialog.setLocationRelativeTo(null);
                updaterDialog.setVisible(true);
            }

            try {
                String updatedVersion = IOUtils.toString(new URL("https://raw.githubusercontent.com/LogicismDev/LogServerMaker/main/version"), StandardCharsets.UTF_8);

                if (Double.parseDouble(updatedVersion) > Double.parseDouble(VERSION)) {
                    UpdateDialog updateDialog = new UpdateDialog();
                    updateDialog.pack();
                    updateDialog.setLocationRelativeTo(null);
                    updateDialog.setVisible(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadSettings() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("logsm.settings")));
        settings = (Map<String, String>) ois.readObject();
        ois.close();
    }

    public static void saveSettings() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("logsm.settings")));
        oos.writeObject(settings);
        oos.close();
    }

    public static Map<String, String> getSettings() {
        return settings;
    }

    public static List<Server> getServerList() {
        return serverList;
    }

    public static String getExternalIP() {
        return externalIP;
    }

    public static void deleteDirectory(File file) {
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                if (file1.isDirectory()) {
                    deleteDirectory(file1);
                } else {
                    file1.delete();
                }
            }
        }

        file.delete();

    }

    public static boolean parseBoolean(String s) {
        if (s.equals("on") || s.equals("true")) {
            return true;
        } else if (s.equals("off") || s.equals("false")) {
            return false;
        }

        return false;
    }

    public static String booleanToString(ServerType serverType, boolean bool) {
        if (serverType.equals(ServerType.Nukkit) || serverType.equals(ServerType.PocketMine)) {
            if (bool) {
                return "on";
            } else {
                return "off";
            }
        } else {
            return String.valueOf(bool);
        }
    }

    public static void unzipFile(File file, String dir, JProgressBar progressBar, JLabel speedLabel) throws IOException {
        speedLabel.setText("0%");
        byte[] buffer = new byte[4096];

        ZipFile zf = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zf.entries();
        progressBar.setMaximum(zf.size());
        int i = 0;
        while (entries.hasMoreElements()) {
            ZipEntry ze = entries.nextElement();
            InputStream zis = zf.getInputStream(ze);
            String fileName = ze.getName();
            File newFile = new File(dir + "/" + fileName);
            if (ze.isDirectory()) {
                newFile.mkdirs();
            } else {
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            progressBar.setValue(i++);
            speedLabel.setText(i + "%");
            zis.close();
        }

        zf.close();
    }
}
