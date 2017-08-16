package com.ngenko.kanjimemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.ngenko.kanjimemo.lib.Util;

public class SelectStyle extends AppCompatActivity {

    private static String TAG = "SelectStyle";
    private RelativeLayout rlMain;
    RadioButton rbKuro;
    RadioButton rbMidori;
    RadioButton rbMurasaki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_style);

        rlMain = (RelativeLayout) findViewById(R.id.select_style_rl_main);

        rbKuro = (RadioButton)findViewById(R.id.select_style_rb_kuro);
        rbMidori = (RadioButton)findViewById(R.id.select_style_rb_midori);
        rbMurasaki = (RadioButton)findViewById(R.id.select_style_rb_murasaki);

        String selectedStyle = Util.getSharedPreferences("style_selected", this);
        checkPreferences();

        if (selectedStyle.equals("kuro")){ rbKuro.setChecked(true); rbKuro.setSelected(true);}
        else if (selectedStyle.equals("midori")){ rbMidori.setChecked(true); rbMidori.setSelected(true);}
        else if (selectedStyle.equals("murasaki")){ rbMurasaki.setChecked(true); rbMurasaki.setSelected(true);}

        rbKuro.setOnTouchListener(rbKuroListener);
        rbMidori.setOnTouchListener(rbMidoriListener);
        rbMurasaki.setOnTouchListener(rbMurasakiListener);
    }

    private View.OnTouchListener rbKuroListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Util.log(TAG, "Presiono rbKuro");
            if (event.getAction() == MotionEvent.ACTION_UP){
                rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_kuro));
                Util.saveSharedPreferences("style_selected", "kuro", SelectStyle.this);

                Intent intent = new Intent("event_change_style");
                intent.putExtra("message", "kuro");
                LocalBroadcastManager.getInstance(SelectStyle.this).sendBroadcast(intent);
            }
            return false;
        }
    };

    private View.OnTouchListener rbMidoriListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Util.log(TAG, "Presiono rbMidori");
            if (event.getAction() == MotionEvent.ACTION_UP){
                rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_midori));
                Util.saveSharedPreferences("style_selected", "midori", SelectStyle.this);

                Intent intent = new Intent("event_change_style");
                intent.putExtra("message", "midori");
                LocalBroadcastManager.getInstance(SelectStyle.this).sendBroadcast(intent);
            }
            return false;
        }
    };

    private View.OnTouchListener rbMurasakiListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Util.log(TAG, "Presiono rbMurasaki");
            if (event.getAction() == MotionEvent.ACTION_UP){
                rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_murasaki));
                Util.saveSharedPreferences("style_selected", "murasaki", SelectStyle.this);

                Intent intent = new Intent("event_change_style");
                intent.putExtra("message", "murasaki");
                LocalBroadcastManager.getInstance(SelectStyle.this).sendBroadcast(intent);
            }
            return false;
        }
    };

    /**
     *  Style preferences are checked before paint the screen.
     */
    private void checkPreferences(){
        String selectedStyle = Util.getSharedPreferences("style_selected", this);

        Util.log(TAG, "selectedStyle:" + selectedStyle);

        if (selectedStyle.equals("kuro")){
            rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_kuro));
            rbKuro.setChecked(true);
        }

        else if (selectedStyle.equals("midori")){
            rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_midori));
            rbMidori.setChecked(true);
        }

        else if (selectedStyle.equals("murasaki")){
            rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_murasaki));
            rbMurasaki.setChecked(true);
        }
    }

}
