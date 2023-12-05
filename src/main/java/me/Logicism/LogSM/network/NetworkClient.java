package me.Logicism.LogSM.network;

import com.whirvis.jraknet.RakNetException;
import com.whirvis.jraknet.client.RakNetClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class NetworkClient {

    public static BrowserData executeGETRequest(URL url) throws IOException {
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36");
        c.setConnectTimeout(30000);
        c.setReadTimeout(30000);

        int resCode = c.getResponseCode();
        long resLength = c.getContentLength();

        return new BrowserData(c.getURL().toString(), c.getHeaderFields(), resCode, resLength, c.getInputStream() != null ? c.getInputStream() : c.getErrorStream());
    }

    public static URL getMojangServerURL(String version) {
        try {
            BrowserData bd = executeGETRequest(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"));

            BufferedReader br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            bd.getResponse().close();

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray versions = jsonObject.getJSONArray("versions");

            URL versionURL = null;
            for (int i = 0; i < versions.length(); i++) {
                JSONObject versionObject = versions.getJSONObject(i);
                if (versionObject.getString("id").equals(version)) {
                    versionURL = new URL(versionObject.getString("url"));
                    break;
                }
            }

            bd = executeGETRequest(versionURL);

            br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            jsonObject = new JSONObject(sb.toString());
            JSONObject downloadObject = jsonObject.getJSONObject("downloads").getJSONObject("server");

            return new URL(downloadObject.getString("url"));
        } catch (IOException e) {
            return null;
        }
    }

    public static URL getBedrockURL() {
        try {
            Document document = Jsoup.connect("https://www.minecraft.net/en-us/download/server/bedrock/").get();

            if (System.getProperty("os.name").startsWith("Windows")) {
                Element element = document.selectFirst("#main-content > div > div > div.page-section-container.aem-GridColumn.aem-GridColumn--default--12 > div > div > div > div.server-card.aem-GridColumn.aem-GridColumn--default--12 > div > div > div > div:nth-child(1) > div.card-footer > div > a");

                return new URL(element.attr("href"));
            } else {
                Element element = document.selectFirst("#main-content > div > div > div.page-section-container.aem-GridColumn.aem-GridColumn--default--12 > div > div > div > div.server-card.aem-GridColumn.aem-GridColumn--default--12 > div > div > div > div:nth-child(2) > div.card-footer > div > a");

                return new URL(element.attr("href"));
            }

        } catch (IOException e) {

        }

        return null;
    }

    public static URL getCraftBukkitURL(String version) {
        try {
            switch (version) {
                case "1.20.2":
                case "1.20.1":
                case "1.19.4":
                case "1.19.3":
                case "1.19.2":
                case "1.19.1":
                case "1.19":
                case "1.18.2":
                case "1.18.1":
                case "1.18":
                case "1.17.1":
                case "1.17":
                    return new URL("https://download.getbukkit.org/craftbukkit/craftbukkit-" + version + ".jar");
                case "1.16.5":
                case "1.16.4":
                case "1.16.3":
                case "1.16.2":
                case "1.16.1":
                case "1.15.2":
                case "1.15.1":
                case "1.15":
                case "1.14.4":
                case "1.14.3":
                case "1.14.2":
                case "1.14.1":
                case "1.14":
                case "1.13.2":
                case "1.13.1":
                case "1.13":
                case "1.12.2":
                case "1.12":
                case "1.11.2":
                case "1.11.1":
                case "1.11":
                    return new URL("https://cdn.getbukkit.org/craftbukkit/craftbukkit-" + version + ".jar");
                case "1.10.2":
                case "1.10":
                case "1.9.4":
                case "1.9.2":
                case "1.9":
                case "1.8.8":
                case "1.8.7":
                case "1.8.6":
                case "1.8.5":
                case "1.8.4":
                case "1.8.3":
                case "1.8":
                    return new URL("https://cdn.getbukkit.org/craftbukkit/craftbukkit-" + version + "-R0.1-SNAPSHOT-latest.jar");
                case "1.7.10":
                    return new URL("https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.7.10-R0.1-20140808.005431-8.jar");
            }
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getSpigotURL(String version) {
        try {
            switch (version) {
                case "1.20.2":
                case "1.20.1":
                case "1.19.4":
                case "1.19.3":
                case "1.19.2":
                case "1.19.1":
                case "1.19":
                case "1.18.2":
                case "1.18.1":
                case "1.18":
                case "1.17.1":
                case "1.17":
                    return new URL("https://download.getbukkit.org/spigot/spigot-" + version + ".jar");
                case "1.16.5":
                case "1.16.4":
                case "1.16.3":
                case "1.16.2":
                case "1.16.1":
                case "1.15.2":
                case "1.15.1":
                case "1.15":
                case "1.14.4":
                case "1.14.3":
                case "1.14.2":
                case "1.14.1":
                case "1.14":
                case "1.13.2":
                case "1.13.1":
                case "1.13":
                case "1.12.2":
                case "1.12":
                case "1.11.2":
                case "1.11.1":
                case "1.11":
                    return new URL("https://cdn.getbukkit.org/spigot/spigot-" + version + ".jar");
                case "1.10.2":
                case "1.10":
                case "1.9.4":
                case "1.9.2":
                case "1.9":
                case "1.8.8":
                case "1.8.7":
                case "1.8.6":
                case "1.8.5":
                case "1.8.4":
                case "1.8.3":
                case "1.8":
                    return new URL("https://cdn.getbukkit.org/spigot/spigot-" + version + "-R0.1-SNAPSHOT-latest.jar");
                case "1.7.10":
                    return new URL("https://cdn.getbukkit.org/spigot/spigot-1.7.10-SNAPSHOT-b1657.jar");
            }
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getPaperURL(String version) {
        try {
            BrowserData bd = executeGETRequest(new URL("https://papermc.io/api/v2/projects/paper/versions/" + version));

            BufferedReader br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray builds = jsonObject.getJSONArray("builds");

            int latestBuild = builds.getInt(builds.length() - 1);

            return new URL("https://papermc.io/api/v2/projects/paper/versions/" + version + "/builds/" + latestBuild + "/downloads/paper-" + version + "-" + latestBuild + ".jar");
        } catch (IOException e) {

        }

        return null;
    }

    public static URL getPurpurURL(String version) {
        try {
            return new URL("https://api.purpurmc.org/v2/purpur/" + version + "/latest/download");
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getMagmaURL(String version) {
        try {
            switch (version) {
                case "1.19.3":
                case "1.18.2":
                case "1.16.5":
                case "1.12.2":
                    return new URL("https://api.magmafoundation.org/api/v2/" + version + "/latest/download");
            }
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getMohistURL(String version) {
        try {
            BrowserData bd = executeGETRequest(new URL("https://mohistmc.com/api/v2/projects/mohist/" + version + "/builds"));

            BufferedReader br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray builds = jsonObject.getJSONArray("builds");

            return new URL(builds.getJSONObject(builds.length() - 1).getString("url"));
        } catch (IOException e) {

        }

        return null;
    }

    public static URL getNukkitURL() {
        try {
            return new URL("https://ci.opencollab.dev/job/NukkitX/job/Nukkit/job/master/lastStableBuild/artifact/target/nukkit-1.0-SNAPSHOT.jar");
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getPocketMineURL() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                Document document = Jsoup.connect("https://github.com/pmmp/PocketMine-MP/releases/latest").get();

                Element element = document.selectFirst("#repo-content-pjax-container > div > div > div > div.Box-body > div.mb-3.pb-md-4.border-md-bottom > div > div:nth-child(2) > a > span > span");

                return new URL("https://github.com/pmmp/PocketMine-MP/releases/download/" + element.text() + "/PocketMine-MP.phar");
            } else {
                return new URL("https://raw.githubusercontent.com/pmmp/php-build-scripts/master/installer.sh");
            }
        } catch (IOException e) {

        }

        return null;
    }

    public static URL getGeyserURL() {
        try {
            return new URL("https://download.geysermc.org/v2/projects/geyser/versions/latest/builds/latest/downloads/standalone");
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getBungeeCordURL() {
        try {
            return new URL("https://ci.md-5.net/job/BungeeCord/lastStableBuild/artifact/bootstrap/target/BungeeCord.jar");
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getNanoLimboURL() {
        try {
            Document document = Jsoup.connect("https://github.com/Nan1t/NanoLimbo/releases/latest").get();

            Element element = document.selectFirst("#repo-content-pjax-container > div > nav > ol > li.breadcrumb-item.breadcrumb-item-selected > a");

            return new URL("https://github.com/Nan1t/NanoLimbo/releases/download/" + element.text() + "/NanoLimbo-" + element.text().replace("v", "") + "-all.jar");
        } catch (IOException e) {

        }

        return null;
    }

    public static URL getWaterfallURL() {
        try {
            BrowserData bd = executeGETRequest(new URL("https://papermc.io/api/v2/projects/waterfall"));

            BufferedReader br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray versions = jsonObject.getJSONArray("versions");

            String latestVersion = versions.getString(versions.length() - 1);

            bd = executeGETRequest(new URL("https://papermc.io/api/v2/projects/waterfall/versions/" + latestVersion));

            br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            jsonObject = new JSONObject(sb.toString());
            JSONArray builds = jsonObject.getJSONArray("builds");

            int latestBuild = builds.getInt(builds.length() - 1);

            return new URL("https://papermc.io/api/v2/projects/waterfall/versions/" + latestVersion + "/builds/" + latestBuild + "/downloads/waterfall-" + latestVersion + "-" + latestBuild + ".jar");
        } catch (IOException e) {

        }

        return null;
    }

    public static URL getVelocityURL() {
        try {
            BrowserData bd = executeGETRequest(new URL("https://papermc.io/api/v2/projects/velocity"));

            BufferedReader br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray versions = jsonObject.getJSONArray("versions");

            String latestVersion = versions.getString(versions.length() - 1);

            bd = executeGETRequest(new URL("https://papermc.io/api/v2/projects/velocity/versions/" + latestVersion));

            br = new BufferedReader(new InputStreamReader(bd.getResponse()));
            sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }

            jsonObject = new JSONObject(sb.toString());
            JSONArray builds = jsonObject.getJSONArray("builds");

            int latestBuild = builds.getInt(builds.length() - 1);

            return new URL("https://papermc.io/api/v2/projects/velocity/versions/" + latestVersion + "/builds/" + latestBuild + "/downloads/waterfall-" + latestVersion + "-" + latestBuild + ".jar");
        } catch (IOException e) {

        }

        return null;
    }

    public static URL getSpongeVanillaURL(String version) {
        try {
            switch (version) {
                case "1.20.2":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.20.2-11.0.0-RC1435/spongevanilla-1.20.2-11.0.0-RC1435-universal.jar");
                case "1.20.1":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.20.1-11.0.0-RC1365/spongevanilla-1.20.1-11.0.0-RC1365-universal.jar");
                case "1.19.4":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.19.4-10.0.0-RC1439/spongevanilla-1.19.4-10.0.0-RC1439-universal.jar");
                case "1.19.3":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.19.3-10.0.0-RC1277/spongevanilla-1.19.3-10.0.0-RC1277-universal.jar");
                case "1.19.2":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.19.2-10.0.0-RC1239/spongevanilla-1.19.2-10.0.0-RC1239-universal.jar");
                case "1.18.2":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.18.2-9.0.0-RC1157/spongevanilla-1.18.2-9.0.0-RC1157-universal.jar");
                case "1.18.1":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.18.1-9.0.0-RC1119/spongevanilla-1.18.1-9.0.0-RC1119-universal.jar");
                case "1.17.1":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.17.1-9.0.0-RC977/spongevanilla-1.17.1-9.0.0-RC977-universal.jar");
                case "1.16.5":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.16.5-8.0.0-RC1118/spongevanilla-1.16.5-8.0.0-RC1118-universal.jar");
                case "1.16.4":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.16.4-8.0.0-RC390/spongevanilla-1.16.4-8.0.0-RC390-universal.jar");
                case "1.15.2":
                    return new URL("https://repo.spongepowered.org/repository/maven-snapshots/org/spongepowered/spongevanilla/1.15.2-8.0.0-SNAPSHOT/spongevanilla-1.15.2-8.0.0-20201201.044712-3-universal.jar");
                case "1.12.2":
                    return new URL("https://repo.spongepowered.org/repository/maven-releases/org/spongepowered/spongevanilla/1.12.2-7.4.7/spongevanilla-1.12.2-7.4.7.jar");
                case "1.11.2":
                    return new URL("https://repo.spongepowered.org/repository/legacy-transfer/org/spongepowered/spongevanilla/1.11.2-6.1.0-BETA-27/spongevanilla-1.11.2-6.1.0-BETA-27.jar");
                case "1.10.2":
                    return new URL("https://repo.spongepowered.org/repository/legacy-transfer/org/spongepowered/spongevanilla/1.10.2-5.2.0-BETA-403/spongevanilla-1.10.2-5.2.0-BETA-403.jar");
                case "1.9.4":
                    return new URL("https://repo.spongepowered.org/repository/legacy-transfer/org/spongepowered/spongevanilla/1.9.4-5.0.0-BETA-83/spongevanilla-1.9.4-5.0.0-BETA-83.jar");
                case "1.8.9":
                    return new URL("https://repo.spongepowered.org/repository/legacy-transfer/org/spongepowered/spongevanilla/1.8.9-4.2.0-BETA-352/spongevanilla-1.8.9-4.2.0-BETA-352.jar");
            }
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static URL getWaterdogPEURL() {
        try {
            Document document = Jsoup.connect("https://github.com/WaterdogPE/WaterdogPE/releases/latest").get();

            Element element = document.selectFirst("#repo-content-pjax-container > div > nav > ol > li.breadcrumb-item.breadcrumb-item-selected > a");

            return new URL("https://github.com/WaterdogPE/WaterdogPE/releases/download/" + element.text() + "/Waterdog.jar");
        } catch (IOException e) {

        }

        return null;
    }

    public static URL getPocketMinePHPURL() {
        try {
            return new URL("https://github.com/pmmp/PHP-Binaries/releases/download/php-8.1-latest/PHP-Windows-x64-PM5.zip");
        } catch (MalformedURLException e) {

        }

        return null;
    }

    public static String getExternalIP() {
        try {
            BrowserData bd = executeGETRequest(new URL("http://checkip.amazonaws.com/"));

            if (bd.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(bd.getResponse()));
                String s;
                StringBuilder sb = new StringBuilder();

                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }

                return sb.toString();
            }
        } catch (IOException e) {
            return "0.0.0.0";
        }

        return "0.0.0.0";
    }

    public static boolean checkTCPServer(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);

            if (socket.isConnected()) {
                socket.close();

                return true;
            } else {
                socket.close();

                return checkMCServer(ip, port);
            }
        } catch (IOException e) {
            return checkMCServer(ip, port);
        }
    }

    public static boolean checkMCServer(String ip, int port) {
        try {
            BrowserData bd = executeGETRequest(new URL("https://api.mcsrvstat.us/simple/" + ip + ":" + port));

            int resCode = bd.getResponseCode();

            if (resCode == 200) {
                bd.getResponse().close();

                return true;
            } else if (resCode == 404) {
                bd.getResponse().close();

                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return false;
    }

    public static boolean checkMCBedrockServer(String ip, int port) {
        try {
            BrowserData bd = executeGETRequest(new URL("https://api.mcsrvstat.us/bedrock/simple/" + ip + ":" + port));

            int resCode = bd.getResponseCode();

            if (resCode == 200) {
                bd.getResponse().close();

                return true;
            } else if (resCode == 404) {
                bd.getResponse().close();

                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return false;
    }

    public static boolean checkUDPServer(String ip, int port) {
        try {
            RakNetClient client = new RakNetClient();

            client.connect(ip, port);

            if (client.isConnected()) {
                client.disconnect();

                return true;
            } else {
                client.disconnect();

                return checkMCBedrockServer(ip, port);
            }
        } catch (IOException | RakNetException e) {
            return checkMCBedrockServer(ip, port);
        }
    }
}
