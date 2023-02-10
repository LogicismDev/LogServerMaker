package me.Logicism.LogSM.core;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Server {

    private String name;
    private String version;
    private String dirName;
    private int ram;
    private ServerType serverType;
    private String serverArgs;
    private String javaHome;
    private int autoRestart;
    private boolean upnpEnabled;

    public Server() {
        name = "";

        String dirName = "";
        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            char c = (char)(r.nextInt(26) + 'a');
            dirName += c;
        }
        this.dirName = dirName.toUpperCase();
    }

    public Server(JSONObject jsonObject, String dirName) {
        name = jsonObject.getString("name");
        this.dirName = dirName;
        ram = jsonObject.getInt("ram");
        try {
            serverType = ServerType.valueOf(jsonObject.getString("serverType"));
        } catch (IllegalArgumentException e) {

        }
        if (jsonObject.has("version")) {
            version = jsonObject.getString("version");
        }
        if (jsonObject.has("serverArgs")) {
            serverArgs = jsonObject.getString("serverArgs");
        }
        if (jsonObject.has("javaHome")) {
            javaHome = jsonObject.getString("javaHome");
        }
        if (jsonObject.has("autoRestart")) {
            autoRestart = jsonObject.getInt("autoRestart");
        } else {
            autoRestart = -1;
        }
        if (jsonObject.has("upnpEnabled")) {
            upnpEnabled = jsonObject.getBoolean("upnpEnabled");
        }
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getRAM() {
        return ram;
    }

    public void setRAM(int ram) {
        this.ram = ram;
    }

    public String getDirName() {
        return dirName;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getServerArgs() {
        return serverArgs;
    }

    public void setServerArgs(String serverArgs) {
        this.serverArgs = serverArgs;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public int getAutoRestart() {
        return autoRestart;
    }

    public void setAutoRestart(int autoRestart) {
        this.autoRestart = autoRestart;
    }

    public boolean isUpnpEnabled() {
        return upnpEnabled;
    }

    public void setUpnpEnabled(boolean upnpEnabled) {
        this.upnpEnabled = upnpEnabled;
    }

    public void save() throws IOException {
        File serverDir = new File("servers/" + getDirName());
        if (!serverDir.exists()) {
            serverDir.mkdir();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", getName()).put("ram", getRAM()).put("serverArgs", getServerArgs()).put("serverType", getServerType().name()).put("version", getVersion()).put("javaHome", getJavaHome()).put("autoRestart", getAutoRestart()).put("upnpEnabled", isUpnpEnabled());

        File logSMjson = new File(serverDir, "logsm.json");

        FileWriter fileWriter = new FileWriter(logSMjson);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
