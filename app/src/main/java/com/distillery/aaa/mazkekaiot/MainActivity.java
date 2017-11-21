package com.distillery.aaa.mazkekaiot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.things.pio.PeripheralManagerService;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    static final int PORT = 5657;
    float TEMP = 25;
    String SIT = "";
    UPnP upnp;
    static UDPServer server;
    static Context cntx;
    private MessageHandler msgHandel;
    private Backgroud backgroud;
    private SaveLoad saveLoad;
    private Control control;
    private static int tempSit = 0;
    Button config;
    TextView tempView;
    TextView ipView;
    TextView publicIP;
    Temp tempSens;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cntx = getApplicationContext();
        saveLoad = new SaveLoad(cntx);
        tempView = (TextView) findViewById(R.id.tempView);
        ipView = (TextView) findViewById(R.id.IPview);
        publicIP = (TextView) findViewById(R.id.publicIP);
        config = (Button) findViewById(R.id.CONFIG);
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
            }
        });
        upnp.doPortForwarding();
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> portList = manager.getGpioList();
        msgHandel = new MessageHandler(new MessageHandler.killCallback() {
            @Override
            public void killCallback() {
                server.stop();
            }
        });
        if (portList.isEmpty()) {
            Log.i("GPIO", "No GPIO port available on this device.");
        } else {
            Log.i("GPIO", "List of available ports: " + portList);
        }
        List<String> deviceList = manager.getI2cBusList();
        if (deviceList.isEmpty()) {
            Log.i("I2C", "No I2C bus available on this device.");
        } else {
            Log.i("I2c", "List of available devices: " + deviceList);
        }
        server = new UDPServer(PORT, new UDPServer.onReceive() {
            @Override
            public void onReceive(DatagramPacket data) {
                //call message handler
                Log.d("MessageRCV",new String(data.getData()));
                msgHandel.handle(new String(data.getData()), data.getAddress(), data.getPort(), server.socket);
            }
        }, new UDPServer.onClient() {
            @Override
            public void onClient() {
                //update when client join/leave
                Log.d("listChanged",server.clientlist.toString());
            }
        });
        startTimer();
        server.listen();
        tempSens = new Temp(server);
        ipView.setText(server.socket.getLocalAddress().getHostAddress() + ":" + PORT);
        String[] pins = saveLoad.loadPins().split(",");
        if(pins.length != 3){
            pins = new String[3];
            pins[0] = "13";
            pins[1] = "19";
            pins[2] = "21";
            saveLoad.savePins("13,19,21");
        }else{
            Log.d("PinsLoaded",pins.toString());
        }
        String[] temps = saveLoad.loadTemps().split(",");
        if(temps.length != 4){
            temps = new String[4];
            temps[0] = "50";  //50,70,80,97
            temps[1] = "70";
            temps[2] = "80";
            temps[3] = "97";
        }else{
            Log.d("TempsLoaded",temps.toString());
        }
        control = new Control(Integer.valueOf(pins[0]),Integer.valueOf(pins[1]),Integer.valueOf(pins[2]));
        //UDPServer server,float meth,methCallback callback1 , float eth, ethCallback callback2, float tails, tailsCallback callback3, float finish, finishCallback callback4, Control control
        backgroud = new Backgroud(tempSens, server,
                new Backgroud.noneCallback() {
                    @Override
                    public void noneCallback(float temp) {
                        SIT = "Warming";
                        TEMP = temp;
                    }
                },
                Float.valueOf(temps[0]), new Backgroud.methCallback() {
            @Override
            public void methCallback(float temp) {
                //JSONObject tmp = tempSens.floatToJSON(temp,"meth");
                SIT = "Methanol";
                TEMP = temp;
                //server.sendAll(tmp.toString());
            }
        }, Float.valueOf(temps[1]), new Backgroud.ethCallback() {
            @Override
            public void ethCallback(float temp) {
                //JSONObject tmp = tempSens.floatToJSON(temp,"eth");
                SIT = "Ethanol";
                TEMP = temp;
                //server.sendAll(tmp.toString());
            }
        }, Float.valueOf(temps[2]), new Backgroud.tailsCallback() {
            @Override
            public void tailsCallback(float temp) {
                //JSONObject tmp = tempSens.floatToJSON(temp,"tails");
                SIT = "Tails";
                TEMP = temp;
                //server.sendAll(tmp.toString());
            }
        }, Float.valueOf(temps[3]), new Backgroud.finishCallback() {
            @Override
            public void finishCallback(float temp) {
                //JSONObject tmp = tempSens.floatToJSON(temp,"finish");
                SIT = "Finished";
                TEMP = temp;
                //server.sendAll(tmp.toString());
            }
        }, control);
        backgroud.start();
        publicIP.setText(backgroud.getPublicIP());
        while(true) {
            tempView.setText(Float.toString(backgroud.temp.TEMP));
        }
    }

    private void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        Log.e("TIMER","1");
        timer.schedule(timerTask, 5000, 2500); //

    }
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.e("TIMER","2");
                JSONObject tmp = tempSens.floatToJSON(TEMP,SIT);
                server.sendAll(tmp.toString());
                handler.post(new Runnable() {
                    public void run() {
                        Log.e("TIMER","3");
                        JSONObject tmp = tempSens.floatToJSON(TEMP,SIT);
                        server.sendAll(tmp.toString());
                    }
                });
            }
        };
    }

}
