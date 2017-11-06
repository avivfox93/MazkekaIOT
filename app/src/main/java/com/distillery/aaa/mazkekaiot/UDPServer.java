package com.distillery.aaa.mazkekaiot;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by AvivLaptop on 11/10/2017.
 */

public class UDPServer {
    public DatagramSocket socket;
    private onReceive listener;
    private int PORT;
    private onClient clientListener;
    private Thread listen;
    private boolean sit = true;
    private UPnP upnp;
    public ArrayList<Client> clientlist = new ArrayList<>();

    public UDPServer(int port, onReceive udpCallback, onClient clientListen){
        this.listener = udpCallback;
        this.clientListener = clientListen;
        this.PORT = port;
        //upnp = new UPnP(port);
        registerService();
        try {
            socket = new DatagramSocket(port);
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
        }catch (SocketException e){
            e.printStackTrace();
        }
    }

    interface  onClient{
        void onClient();
    }
    interface onReceive{
        void onReceive(DatagramPacket data);
    }

    public void listen(){
        sit = true;
        listen = new Thread("listen"){
            public void run() {
                while (sit) {
                    byte[] buff = new byte[1024];
                    DatagramPacket data = new DatagramPacket(buff, buff.length);
                    try {
                        socket.receive(data);
                        if(checkList(data.getAddress(),data.getPort())){
                            clientlist.add(new Client(data.getAddress(),data.getPort()));
                        }else{
                            Log.d("aaa","Already a client");
                        }
                        listener.onReceive(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        listen.start();
    }

    public void sendAll(String data){
        DatagramPacket packet = new DatagramPacket(data.getBytes(),data.getBytes().length);
        for(int i = 0 ; i < clientlist.size() ; i++) {
            packet.setPort(clientlist.get(i).port);
            packet.setAddress(clientlist.get(i).ip);
            try {
                socket.send(packet);
                Log.d("SERVERsend", new String(packet.getData()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendStringAll(String data){
        JSONObject message = new JSONObject();
        try{
            message.put("msg","temp");
            message.put("temp",data);
        }catch (JSONException e){
            e.printStackTrace();
        }
        DatagramPacket packet = new DatagramPacket(message.toString().getBytes(),message.toString().getBytes().length);
        for(int i = 0 ; i < clientlist.size() ; i++) {
            packet.setPort(clientlist.get(i).port);
            packet.setAddress(clientlist.get(i).ip);
            try {
                socket.send(packet);
                Log.d("SERVERsend", new String(packet.getData()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        sit = false;
        socket.close();
        socket = null;
    }

    private boolean checkList(InetAddress ip, int port){
        for(int i = 0 ; i < clientlist.size() ; i++){
            if(clientlist.get(i).ip.equals(ip) && clientlist.get(i).port == port) {
                return false;
            }
        }
        return true;
    }

    public void registerService() {
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("Mazkeka");
        serviceInfo.setServiceType("mazkeka_udp.");
        serviceInfo.setPort(PORT);
    }
}
