package com.distillery.aaa.mazkekaiot;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AvivLaptop on 11/10/2017.
 */

public class SaveLoad {

    private Context cntx;
    private static final String PREFS_NAME = "com.AEI.mazkekaIOT";
    private static final String PINS_KEY = "Pins";
    private static final String TEMPS_KEY = "Temps";

    public SaveLoad(Context cntx){
        this.cntx = cntx;
    }

    public String loadPins() {
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, 0);
        String values = prefs.getString(PINS_KEY, null);
        if (values != null) {
            return values;
        } else {
            return " , ";
        }
    }

    public String loadTemps() {
        SharedPreferences prefs = cntx.getSharedPreferences(PREFS_NAME, 0);
        String values = prefs.getString(TEMPS_KEY, null);
        if (values != null) {
            return values;
        } else {
            return " , ";
        }
    }

    public void savePins(String values){
        SharedPreferences.Editor prefs = cntx.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PINS_KEY, values);
        //Toast.makeText(cntx,"Pins saved",Toast.LENGTH_SHORT).show();
        prefs.apply();
    }

    public void saveTemps(String values){
        SharedPreferences.Editor prefs = cntx.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(TEMPS_KEY, values);
        //Toast.makeText(cntx,"Temps saved",Toast.LENGTH_SHORT).show();
        prefs.apply();
    }

}
