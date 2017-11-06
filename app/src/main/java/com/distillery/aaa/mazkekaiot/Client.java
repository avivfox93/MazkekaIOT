package com.distillery.aaa.mazkekaiot;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by AvivLaptop on 12/10/2017.
 */

public class Client {
    InetAddress ip;
    int port;
    public Client(InetAddress ip, int port){
        this.ip = ip;
        this.port = port;
    }
}
