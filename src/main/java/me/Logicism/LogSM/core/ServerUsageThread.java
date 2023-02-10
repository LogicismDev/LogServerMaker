package me.Logicism.LogSM.core;

import me.Logicism.LogSM.gui.GraphView;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import javax.swing.*;
import java.io.OutputStream;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServerUsageThread implements Runnable {

    private final Server server;
    private final Process p;
    private final JLabel cpuUsageLabel;
    private final GraphView cpuGraph;
    private final JLabel ramLabel;
    private final GraphView ramGraph;
    private final DefaultListModel<String> dlm;
    private final GraphView playerGraph;
    private final JLabel uptimeLabel;
    private final JButton OFFLINEButton;

    public ServerUsageThread(Server server, Process p, JLabel cpuUsageLabel, GraphView cpuGraph, JLabel ramLabel, GraphView ramGraph, DefaultListModel<String> dlm, GraphView playerGraph, JLabel uptimeLabel, JButton OFFLINEButton) {
        this.server = server;
        this.p = p;
        this.cpuUsageLabel = cpuUsageLabel;
        this.cpuGraph = cpuGraph;
        this.ramLabel = ramLabel;
        this.ramGraph = ramGraph;
        this.dlm = dlm;
        this.playerGraph = playerGraph;
        this.uptimeLabel = uptimeLabel;
        this.OFFLINEButton = OFFLINEButton;
    }


    @Override
    public void run() {
        SystemInfo si = new SystemInfo();
        CentralProcessor processor = si.getHardware().getProcessor();
        int cpuNumber = processor.getLogicalProcessorCount();
        OperatingSystem os = si.getOperatingSystem();
        OSProcess process;
        long previousTime = 0;
        long timeFinished = -1;
        while (p.isAlive()) {
            try {
                process = os.getProcess((int) p.pid());

                long currentTime = process.getKernelTime() + process.getUserTime();

                long timeDifference = currentTime - previousTime;
                double cpu = (100d * (timeDifference / ((double) 1000))) / (os.getFamily().equalsIgnoreCase("windows") ? cpuNumber : 1);

                DecimalFormat cpuFormat = new DecimalFormat("##0");

                int ram = (int) (process.getResidentSetSize() * 0.000001);

                DecimalFormat ramFormat = new DecimalFormat("######");

                cpuUsageLabel.setText("CPU Usage: " + cpuFormat.format(cpu) + "%");
                ramLabel.setText("RAM Usage: " + ramFormat.format(ram) + " MB");
                cpuGraph.addValue(Math.min(Integer.parseInt(cpuFormat.format(cpu)), 100));
                cpuGraph.setXLabel("CPU Usage: " + cpuFormat.format(cpu) + "%");
                ramGraph.addValue(Math.min(Integer.parseInt(ramFormat.format(ram)), server.getRAM()));
                ramGraph.setXLabel("RAM Usage: " + ramFormat.format(ram) + " MB");
                playerGraph.addValue(dlm.getSize());
                playerGraph.setXLabel("Online Players: " + dlm.getSize());

                if (OFFLINEButton.getText().equals("ONLINE")) {
                    long time = System.currentTimeMillis();
                    if (timeFinished == -1) {
                        timeFinished = time;
                    }

                    long timeElapsed = time - timeFinished;

                    long hours = TimeUnit.MILLISECONDS.toHours(timeElapsed) % 24;
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsed) % 60;

                    uptimeLabel.setText("Uptime: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
                }

                previousTime = currentTime;

                TimeUnit.SECONDS.sleep(1);
            } catch (NullPointerException | InterruptedException ignored) { }
        }
        OFFLINEButton.setText("OFFLINE");
        cpuUsageLabel.setText("CPU Usage: 0%");
        ramLabel.setText("RAM Usage: 0 MB");
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            integers.add(0);
        }
        cpuGraph.setValues(integers);
        cpuGraph.setXLabel("CPU Usage: 0%");
        ramGraph.setValues(integers);
        ramGraph.setXLabel("RAM Usage: 0 MB");
        playerGraph.setValues(integers);
        playerGraph.setXLabel("Online Players: 0");
        uptimeLabel.setText("Uptime: 00:00:00");
    }
}
