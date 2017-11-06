package com.distillery.aaa.mazkekaiot;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by AvivLaptop on 11/10/2017.
 */

public class Temp {
    // UART Device Name
    private static String UartName = "UART1";
    Float temp = new Float(25.0);
    //private static final int TempAddress = 0x50;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private UartDevice tempSensor;
    public static float TEMP;
    UDPServer server;
    private HandlerThread mInputThread;
    private Handler mInputHandler;
    private Runnable mTransferUartRunnable = new Runnable() {
        @Override
        public void run() {
            uartRead();
        }
    };
    public Temp(UDPServer server){
        mInputThread = new HandlerThread("InputThread");
        mInputThread.start();
        mInputHandler = new Handler(mInputThread.getLooper());
        this.server = server;
        try {
            final PeripheralManagerService manager = new PeripheralManagerService();
            List aaa = manager.getUartDeviceList();
            for(int i = 0 ; i < aaa.size() ; i++){
                Log.e("UART",aaa.get(i).toString());
                //UartName = aaa.get(i).toString();
            }
            tempSensor = manager.openUartDevice(UartName);
            mInputHandler.post(mTransferUartRunnable);
            tempSensor.setBaudrate(115200);
            tempSensor.setDataSize(8);
            tempSensor.setParity(UartDevice.PARITY_NONE);
            tempSensor.setStopBits(1);
            tempSensor.registerUartDeviceCallback(new UartDeviceCallback() {
                @Override
                public boolean onUartDeviceDataAvailable(UartDevice uart) {
                    uartRead();
                    return true;
                }
                @Override
                public void onUartDeviceError(UartDevice uart, int error) {
                    Log.w(UartName, uart + ": Error event " + error);
                }
            }, mInputHandler);
            Log.d("connecting","Connecting to " + UartName);
            //back();
        } catch (IOException e) {
            Log.w("Temp", "Unable to access UART device", e);
        }
    }
    public Float getTemp(){
        //server.sendStringAll(Float.toString(TEMP));
        return TEMP;
    }

    public void uartRead(){
        try{
            Log.e(UartName,"Recived data from uart");
            try {
                TEMP = readUartBuffer(tempSensor);
                Log.e(UartName, Float.toString(TEMP));
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void back(){
        final Handler handler = new Handler();
        Runnable runnable;
        while (true) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("handler", "temp background is running...");
                        writeUartData(tempSensor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this,1000);
                }
            };
            handler.postDelayed(runnable, 1000);
            runnable.run();
        }
    }

    public void writeUartData(UartDevice uart) throws IOException {
        byte[] buffer = "1".getBytes();
        int count = uart.write(buffer, buffer.length);
        Log.d("UART", "Wrote " + count + " bytes to peripheral");
    }

    public Float readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        final int maxCount = 8;
        byte[] buffer = new byte[maxCount];

        int count;
        while ((count = uart.read(buffer, buffer.length)) > 0) {
            Log.d("UART", "Read " + count + " bytes from peripheral " + new Float(new String(buffer)));
            if(count > 6) {
                temp = new Float(new String(buffer));
            }
        }
        return temp;
    }

    public JSONObject floatToJSON(float temp, String sit){
        JSONObject tmp = new JSONObject();
        try{
            tmp.put("msg","temp");
            tmp.put("temp",temp);
            tmp.put("sit",sit);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  tmp;
    }

    public void kill(){
        try {
            tempSensor.close();
            tempSensor = null;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
