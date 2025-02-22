package me.Logicism.LogSM.network;

import com.dosse.upnp.UPnP;
import me.Logicism.LogSM.LogSM;
import me.Logicism.LogSM.core.Server;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.jupnp.UpnpService;
import org.jupnp.UpnpServiceImpl;
import org.jupnp.controlpoint.ActionCallback;
import org.jupnp.model.action.ActionInvocation;
import org.jupnp.model.message.UpnpResponse;
import org.jupnp.model.meta.Action;
import org.jupnp.model.meta.RemoteDevice;
import org.jupnp.model.meta.RemoteService;
import org.jupnp.model.types.UDAServiceType;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;

public class UPnPManager {

    private UpnpService service;
    private GatewayDevice device;
    private boolean isTCPOpen = false;
    private boolean isUDPOpen = false;

    public void openTCPPort(Server server, String ip, int port) {
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling") || LogSM.getSettings().get("Port Forwarding Library").equals("jUPnP")) {
            service = new UpnpServiceImpl();

            service.getControlPoint().search();

            RemoteDevice gateway = null;
            for (RemoteDevice device : service.getRegistry().getRemoteDevices()) {
                if (device.getType().getType().equals("InternetGatewayDevice")) {
                    gateway = device;
                    break;
                }
            }

            if (gateway != null) {
                RemoteService wanService = gateway.findService(new UDAServiceType("WANIPConnection", 1));

                if (wanService != null) {
                    Action portMappingAction = wanService.getAction("AddPortMapping");

                    if (portMappingAction != null) {
                        ActionInvocation<RemoteService> invocation = new ActionInvocation<>(portMappingAction);

                        invocation.setInput("NewRemoteHost", "");
                        invocation.setInput("NewExternalPort", port);
                        invocation.setInput("NewProtocol", "TCP");
                        invocation.setInput("NewInternalPort", port);
                        invocation.setInput("NewInternalClient", ip);
                        invocation.setInput("NewEnabled", true);
                        invocation.setInput("NewPortMappingDescription", "LogServerMaker (" + server.getName() + ")");
                        invocation.setInput("NewLeaseDuration", 0);

                        service.getControlPoint().execute(new ActionCallback(invocation) {
                            @Override
                            public void success(ActionInvocation actionInvocation) {

                            }

                            @Override
                            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                            }
                        });
                    }
                }
            }
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
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling") || LogSM.getSettings().get("Port Forwarding Library").equals("jUPnP")) {
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
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling") || LogSM.getSettings().get("Port Forwarding Library").equals("jUPnP")) {
            service = new UpnpServiceImpl();

            service.getControlPoint().search();

            RemoteDevice gateway = null;
            for (RemoteDevice device : service.getRegistry().getRemoteDevices()) {
                if (device.getType().getType().equals("InternetGatewayDevice")) {
                    gateway = device;
                    break;
                }
            }

            if (gateway != null) {
                RemoteService wanService = gateway.findService(new UDAServiceType("WANIPConnection", 1));

                if (wanService != null) {
                    Action portMappingAction = wanService.getAction("AddPortMapping");

                    if (portMappingAction != null) {
                        ActionInvocation<RemoteService> invocation = new ActionInvocation<>(portMappingAction);

                        invocation.setInput("NewRemoteHost", "");
                        invocation.setInput("NewExternalPort", port);
                        invocation.setInput("NewProtocol", "UDP");
                        invocation.setInput("NewInternalPort", port);
                        invocation.setInput("NewInternalClient", ip);
                        invocation.setInput("NewEnabled", true);
                        invocation.setInput("NewPortMappingDescription", "LogServerMaker (" + server.getName() + ")");
                        invocation.setInput("NewLeaseDuration", 0);

                        service.getControlPoint().execute(new ActionCallback(invocation) {
                            @Override
                            public void success(ActionInvocation actionInvocation) {

                            }

                            @Override
                            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String s) {

                            }
                        });
                    }
                }
            }
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
        if (!LogSM.getSettings().containsKey("Port Forwarding Library") || LogSM.getSettings().get("Port Forwarding Library").equals("Cling") || LogSM.getSettings().get("Port Forwarding Library").equals("jUPnP")) {
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
