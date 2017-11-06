package com.distillery.aaa.mazkekaiot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    SaveLoad saveLoad;
    EditText eth,meth,tails,finish,sol1,sol2,hotplate;
    Button ok,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        saveLoad = new SaveLoad(getApplicationContext());
        eth = (EditText) findViewById(R.id.ethIN);
        meth = (EditText) findViewById(R.id.methIN);
        tails = (EditText) findViewById(R.id.tailsIN);
        finish = (EditText) findViewById(R.id.finishIN);
        sol1 = (EditText) findViewById(R.id.solonoid1IN);
        sol2 = (EditText) findViewById(R.id.solonoid2IN);
        hotplate = (EditText) findViewById(R.id.hotplateIN);
        ok = (Button) findViewById(R.id.OK);
        cancel = (Button) findViewById(R.id.CANCEL);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        load();
    }

    private void load(){
        String[] temps = saveLoad.loadTemps().split(",");//0eth/1meth/2tails/3finish
        String[] pins = saveLoad.loadPins().split(",");//0sol1/1sol2/2hotplate
        if(temps.length == 4) {
            eth.setText(temps[0]);
            meth.setText(temps[1]);
            tails.setText(temps[2]);
            finish.setText(temps[3]);
        }
        if(pins.length == 3){
            sol1.setText(pins[0]);
            sol2.setText(pins[1]);
            hotplate.setText(pins[2]);
        }
    }

    private void save(){
        String result = "";
        result += eth.getText().toString();
        result += "," + meth.getText().toString();
        result += "," + tails.getText().toString();
        result += "," + finish.getText().toString();
        saveLoad.saveTemps(result);
        result = sol1.getText().toString();
        result += "," + sol2.getText().toString();
        result += "," + hotplate.getText().toString();
        saveLoad.savePins(result);
    }
}
