package com.distillery.aaa.mazkekaiot;

import android.util.Log;

import com.offbynull.portmapper.PortMapperFactory;
import com.offbynull.portmapper.gateway.Bus;
import com.offbynull.portmapper.gateway.Gateway;
import com.offbynull.portmapper.gateways.network.NetworkGateway;
import com.offbynull.portmapper.gateways.process.ProcessGateway;
import com.offbynull.portmapper.mapper.MappedPort;
import com.offbynull.portmapper.mapper.PortMapper;
import com.offbynull.portmapper.mapper.PortType;

import java.util.List;

/**
 * Created by AvivLaptop on 03/11/2017.
 */

public class UPnP {
    // Start gateways
    public UPnP(int PORT) {
        Gateway network = NetworkGateway.create();
        Gateway process = ProcessGateway.create();
        Bus networkBus = network.getBus();
        Bus processBus = process.getBus();

        // Discover port forwarding devices and take the first one found
        try {
            List<PortMapper> mappers = PortMapperFactory.discover(networkBus, processBus);
            PortMapper mapper = mappers.get(0);
            MappedPort mappedPort = mapper.mapPort(PortType.TCP, PORT, PORT, 60);
            System.out.println("Port mapping added: " + mappedPort);
            while(true) {
                mappedPort = mapper.refreshPort(mappedPort, mappedPort.getLifetime() / 2L);
                System.out.println("Port mapping refreshed: " + mappedPort);
                Thread.sleep(mappedPort.getLifetime() * 1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
