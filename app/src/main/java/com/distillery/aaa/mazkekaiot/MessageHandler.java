package com.distillery.aaa.mazkekaiot;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by AvivLaptop on 12/10/2017.
 */

public class MessageHandler {
    private InetAddress ip;
    private int port;
    private Context cntx;
    DatagramSocket socket;
    killCallback listener;
    public MessageHandler(killCallback callback){
        this.listener = callback;
    }
    public void handle(String data, InetAddress ip, int port, DatagramSocket socket){
        cntx = MainActivity.cntx;
        this.socket = socket;
        this.ip = ip;
        this.port = port;
        JSONObject json = new JSONObject();
        String msg = "";
        try {
            json = new JSONObject(data);
        }catch (JSONException e){
            e.printStackTrace();
        }
        try {
            msg = json.getString("msg");
        }catch (JSONException e){
            e.printStackTrace();
        }
        switch (msg){
            case "hi":
                handleHi(json);
                break;
            case "bye":
                handleBye(json);
                break;
            case "set":
                handleSet(json);
                break;
            case "get":
                handleGet(json);
                break;
            case "kill":
                handleKill(json);
                break;
            case "getpins":
                handleGetPins(json);
                break;
            case "setpins":
                handleSetPins(json);
                break;
            default:
                Log.e("MSG RCV","Got a bad msg...- " + msg);
                break;
        }
    }

    private void handleHi(JSONObject obj){

    }

    private void handleBye(JSONObject obj){
        ArrayList<Client> list = MainActivity.server.clientlist;
        for(int i = 0 ; i < list.size() ; i++){
            if(list.get(i).ip.equals(ip) && list.get(i).port == port){
                MainActivity.server.clientlist.remove(i);
            }
        }
    }

    private void handleSet(JSONObject obj){
        SaveLoad save = new SaveLoad(cntx);
        String abc = "";
        try{
            abc = obj.getString("meth");
            abc += ",";
            abc += obj.getString("eth");
            abc += ",";
            abc += obj.getString("tails");
            abc += ",";
            abc += obj.getString("finish");
        }catch (JSONException e){
            e.printStackTrace();
        }
        save.saveTemps(abc);
    }

    private void handleGet(JSONObject obj){
        SaveLoad load = new SaveLoad(cntx);
        String[] loaded = load.loadTemps().split(",");
        obj = new JSONObject();
        if(loaded.length !=4){
            try {
                obj.put("msg", "temps");
                obj.put("eth", 75);
                obj.put("meth", 60);
                obj.put("tails", 90);
                obj.put("finish", 95);
            }catch (JSONException e){

            }
        }else {
            try { //0eth/1meth/2tails/3finish
                obj.put("msg", "temps");
                obj.put("eth", loaded[0]);
                obj.put("meth", loaded[1]);
                obj.put("tails", loaded[2]);
                obj.put("finish", loaded[3]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        DatagramPacket packet = new DatagramPacket(obj.toString().getBytes(),obj.toString().getBytes().length);
        packet.setAddress(ip);
        packet.setPort(port);
        try {
            socket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleKill(JSONObject obj){
        listener.killCallback();
    }

    private void handleGetPins(JSONObject obj){
        SaveLoad load = new SaveLoad(cntx);
        String[] loaded = load.loadPins().split(",");
        obj = new JSONObject();
        if(loaded.length !=4){
            try {
                obj.put("msg", "pins");
                obj.put("solonoid1", 13);
                obj.put("solonoid2", 19);
                obj.put("plate", 21);
            }catch (JSONException e){

            }
        }else {
            try {
                obj.put("msg", "pins");
                obj.put("solonoid1", loaded[0]);
                obj.put("solonoid2", loaded[1]);
                obj.put("plate", loaded[2]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        DatagramPacket packet = new DatagramPacket(obj.toString().getBytes(),obj.toString().getBytes().length);
        packet.setAddress(ip);
        packet.setPort(port);
        try {
            socket.send(packet);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleSetPins(JSONObject obj){
        SaveLoad save = new SaveLoad(cntx);
        String abc = "";
        try{
            abc = obj.getString("solonoid1");
            abc += ",";
            abc += obj.getString("solonoid2");
            abc += ",";
            abc += obj.getString("plate");
        }catch (JSONException e){
            e.printStackTrace();
        }
        save.savePins(abc);
    }

    public interface killCallback{
        public void killCallback();
    }
}
