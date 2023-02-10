package me.Logicism.LogSM.network;

import com.dosse.upnp.UPnP;
import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.model.PortMapping;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class UPnPManager {

    private UpnpService service;
    private GatewayDevice device;
    private boolean isTCPOpen = false;
    private boolean isUDPOpen = false;

    public void openTCPPort(Server server, String ip, int port) {
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling")) {
            PortMapping portMapping = new PortMapping(port, ip, PortMapping.Protocol.TCP, "LogServerMaker (" + server.getName() + ")");

            service = new UpnpServiceImpl(new PortMappingListener(portMapping));
            service.getControlPoint().search();
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("WaifUPnP")) {
            UPnP.openPortTCP(port);
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("weupnp")) {
            GatewayDiscover discover = new GatewayDiscover();
            try {
                discover.discover();
                GatewayDevice device = discover.getValidGateway();

                if (device != null) {
                    device.addPortMapping(port, port, ip, "TCP", "LogServerMaker (" + server.getName() + ")");
                }
            } catch (IOException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        isTCPOpen = true;
    }

    public void closeTCPPort(Server server, String ip, int port) {
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling")) {
            service.shutdown();
            service = null;
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("WaifUPnP")) {
            UPnP.closePortTCP(port);
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("weupnp")) {
            try {
                device.deletePortMapping(port, "TCP");

                device = null;
            } catch (IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        isTCPOpen = false;
    }

    public void openUDPPort(Server server, String ip, int port) {
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling")) {
            PortMapping portMapping = new PortMapping(port, ip, PortMapping.Protocol.UDP, "LogServerMaker (" + server.getName() + ")");

            service = new UpnpServiceImpl(new PortMappingListener(portMapping));
            service.getControlPoint().search();
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("WaifUPnP")) {
            UPnP.openPortUDP(port);
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("weupnp")) {
            GatewayDiscover discover = new GatewayDiscover();
            try {
                discover.discover();
                GatewayDevice device = discover.getValidGateway();

                if (device != null) {
                    device.addPortMapping(port, port, ip, "UDP", "LogServerMaker (" + server.getName() + ")");
                }
            } catch (IOException | SAXException | ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        isUDPOpen = true;
    }

    public void closeUDPPort(Server server, String ip, int port) {
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling")) {
            service.shutdown();
            service = null;
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("WaifUPnP")) {
            UPnP.closePortUDP(port);
        } else if (LogSM.getSettings().get("Port Forwarding Library").equals("weupnp")) {
            try {
                device.deletePortMapping(port, "UDP");

                device = null;
            } catch (IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        isUDPOpen = false;
    }

    public boolean isTCPOpen() {
        return isTCPOpen;
    }

    public boolean isUDPOpen() {
        return isUDPOpen;
    }
}
