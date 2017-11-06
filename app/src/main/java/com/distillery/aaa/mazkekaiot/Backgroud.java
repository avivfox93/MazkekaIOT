package com.distillery.aaa.mazkekaiot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by AvivLaptop on 11/10/2017.
 */

public class Backgroud {
    private UDPServer server;
    private float meth;
    private float eth;
    private float tails;
    private float finish;
    public String publicIP;
    public noneCallback callback0;
    public methCallback callback1;
    public ethCallback callback2;
    public tailsCallback callback3;
    public finishCallback callback4;
    private Control control;
    public Thread start;
    public Thread getPublicIP;
    public Temp temp;
    public Backgroud(Temp temp, UDPServer server, noneCallback callback0, float meth,methCallback callback1 , float eth, ethCallback callback2, float tails, tailsCallback callback3, float finish, finishCallback callback4, Control control){
        this.server = server;
        this.eth = eth;
        this.meth = meth;
        this.tails = tails;
        this.finish = finish;
        this.callback0 = callback0;
        this.callback1 = callback1;
        this.callback2 = callback2;
        this.callback3 = callback3;
        this.callback4 = callback4;
        this.control = control;
        this.temp = temp;
    }

    public void start(){
        start = new Thread("start") {
            public void run() {
                boolean sit = true;
                control.changeSolonoid(false);
                control.setPlate(true);
                while(sit) {
                    while(temp.getTemp() < meth){
                        callback0.noneCallback(temp.getTemp());
                    }
                    while(temp.getTemp() >= meth && temp.getTemp() < eth){
                        callback1.methCallback(temp.getTemp());
                    }
                    while(temp.getTemp() >= eth && temp.getTemp() < tails){
                        callback2.ethCallback(temp.getTemp());
                        control.changeSolonoid(true);
                    }
                    while(temp.getTemp() >= tails && temp.getTemp() < finish){
                        callback3.tailsCallback(temp.getTemp());
                        control.changeSolonoid(false);
                    }
                    while(temp.getTemp() >= finish){
                        callback4.finishCallback(temp.getTemp());
                        control.setPlate(false);
                        sit = false;
                    }
//
//                    if (temp.getTemp() <= meth) {
//                        callback1.methCallback(temp.getTemp());
//                    }
//                    else if (temp.getTemp() <= eth) {
//                        callback2.ethCallback(temp.getTemp());
//                        control.changeSolonoid(true);
//                    }
//                    else if (temp.getTemp() <= tails) {
//                        callback3.tailsCallback(temp.getTemp());
//                        control.changeSolonoid(false);
//                    }
//                    else if (temp.getTemp() >= finish) {
//                        callback4.finishCallback(temp.getTemp());
//                        control.setPlate(false);
//                        sit = false;
//                    }
                }
            }
        };
        start.start();
    }

    public interface noneCallback{
        void noneCallback(float temp);
    }

    public interface methCallback{
        void methCallback(float temp);
    }

    public interface ethCallback{
        void ethCallback(float temp);
    }

    public interface tailsCallback{
        void tailsCallback(float temp);
    }

    public interface finishCallback{
        void finishCallback(float temp);
    }

    public String getPublicIP() {
        getPublicIP = new Thread("start") {
            public void run() {
                try {
                    URL connection = new URL("http://checkip.amazonaws.com/");
                    URLConnection con = connection.openConnection();
                    String str;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    str = reader.readLine();
                    System.out.println("Public IP is: " + str);
                    publicIP = str;
                } catch (
                        IOException e)

                {
                    e.printStackTrace();
                    publicIP = "";
                }
            }
        };
        getPublicIP.start();
        return publicIP;
    }

    public void kill(){
        control.kill();
        temp.kill();
    }
}
