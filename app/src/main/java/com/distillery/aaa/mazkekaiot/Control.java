package com.distillery.aaa.mazkekaiot;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by AvivLaptop on 11/10/2017.
 */

public class Control {
    PeripheralManagerService manager;
    private Gpio solonoid1;
    private Gpio solonoid2;
    private Gpio hotplate;
    private int solo1Pin = 0;
    private int solo2Pin = 0;
    private int platePin = 0;

    public  Control(int solonoid1pin, int solonoid2pin, int hotplatepin){
        manager = new PeripheralManagerService();
        solo1Pin = solonoid1pin;
        solo2Pin = solonoid2pin;
        platePin = hotplatepin;
        try {
            solonoid1 = manager.openGpio(getBCM(solonoid1pin));
            solonoid2 = manager.openGpio(getBCM(solonoid2pin));
            hotplate = manager.openGpio(getBCM(hotplatepin));
            initialize();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void initialize(){
        try {
            solonoid1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            solonoid1.setActiveType(Gpio.ACTIVE_HIGH);
            solonoid2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            solonoid2.setActiveType(Gpio.ACTIVE_HIGH);
            hotplate.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            hotplate.setActiveType(Gpio.ACTIVE_HIGH);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void kill(){
        try {
            solonoid1.setValue(false);
            solonoid2.setValue(false);
            hotplate.setValue(false);
            solonoid1.close();
            solonoid2.close();
            hotplate.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        solonoid1 = null;
        solonoid2 = null;
        hotplate = null;
    }

    public String getPins(){
        String values = solo1Pin + "," + solo2Pin + "," + platePin;
        return values;
    }

    public void changeSolonoid(boolean sit){
        try {
            if (sit) {
                solonoid1.setValue(true);
                solonoid2.setValue(false);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                solonoid1.setValue(false);
            } else {
                solonoid1.setValue(false);
                solonoid2.setValue(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                solonoid2.setValue(false);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setPlate(boolean sit){
        try {
            hotplate.setValue(sit);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String getBCM(int pin){
        String value = "";
        switch (pin){
            case 3:
                value = "BCM2";
                break;
            case 5:
                value = "BCM3";
                break;
            case 7:
                value = "BMC4";
                break;
            case 8:
                value = "BCM14";
                break;
            case 10:
                value = "BCM15";
                break;
            case 11:
                value = "BCM17";
                break;
            case 12:
                value = "BCM18";
                break;
            case 13:
                value = "BCM27";
                break;
            case 15:
                value = "BCM22";
                break;
            case 16:
                value = "BCM23";
                break;
            case 18:
                value= "BCM24";
                break;
            case 19:
                value = "BCM10";
                break;
            case 21:
                value = "BCM9";
                break;
            case 22:
                value = "BCM25";
                break;
            case 23:
                value = "BCM11";
                break;
            case 24:
                value = "BCM8";
                break;
            case 26:
                value = "BCM7";
                break;
            case 27:
                value = "BCM0";
                break;
            case 28:
                value = "BCM1";
                break;
            case 29:
                value = "BCM5";
                break;
            case 31:
                value = "BCM6";
                break;
            case 32:
                value = "BCM12";
                break;
            case 33:
                value = "BCM13";
                break;
            case 35:
                value = "BCM19";
                break;
            case 36:
                value = "BCM16";
                break;
            case 37:
                value = "BCM26";
                break;
            case 38:
                value = "BCM20";
                break;
            case 40:
                value = "BCM21";
                break;
            default:
                value = "";
                break;
        }
        return value;
    }
}