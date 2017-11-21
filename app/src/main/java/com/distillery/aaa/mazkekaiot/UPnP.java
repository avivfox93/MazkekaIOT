package com.distillery.aaa.mazkekaiot;

import android.util.Log;


import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.registry.RegistryListener;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by AvivLaptop on 03/11/2017.
 */

public class UPnP {
    private int PORT;
    // Start gateways
    public UPnP(int PORT) {
        this.PORT = PORT;
    }

    public void doPortForwarding() {
        try {
            PortMapping[] desiredMapping = new PortMapping[2];
            desiredMapping[0] = new PortMapping(PORT, InetAddress.getLocalHost().getHostAddress(),
                    PortMapping.Protocol.TCP, " TCP POT Forwarding");


            desiredMapping[1] = new PortMapping(PORT, InetAddress.getLocalHost().getHostAddress(),
                    PortMapping.Protocol.UDP, " UDP POT Forwarding");


            UpnpService upnpService = new UpnpServiceImpl();
            RegistryListener registryListener = new PortMappingListener(desiredMapping);
            upnpService.getRegistry().addListener(registryListener);

            upnpService.getControlPoint().search();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }

    }
}
