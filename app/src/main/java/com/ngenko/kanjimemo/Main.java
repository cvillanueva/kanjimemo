package com.ngenko.kanjimemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ngenko.kanjimemo.lib.DataBaseHelper;
import com.ngenko.kanjimemo.lib.Util;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by claudio on 7/12/15.
 */

public class Main extends AppCompatActivity {

    private static String TAG = "Main";
    private RelativeLayout rlMain;
    private Button btnKanji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        rlMain = (RelativeLayout) findViewById(R.id.main_rl_main);
        btnKanji = (Button) findViewById(R.id.main_kanji_button);

        btnKanji.setOnTouchListener(playButtonListener);

        checkPreferences();

        Util.log(TAG, "getLanguage:" + Locale.getDefault().getLanguage()); // Result: en / es

        DataBaseHelper dbh = new DataBaseHelper(this);

        dbh.createDataBase();
        Util.log(TAG,"post createDataBase()");

    }

    /**
     *  Style preferences are checked before paint the screen.
     */
    private void checkPreferences(){
        String selectedStyle = Util.getSharedPreferences("style_selected", this);

        Util.log(TAG, "selectedStyle:" + selectedStyle);

        if (selectedStyle.equals("kuro")){
            rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_kuro));
            btnKanji.setBackgroundResource(R.anim.anim_button_kuro);
        }

        else if (selectedStyle.equals("midori")){
            rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_midori));
            btnKanji.setBackgroundResource(R.anim.anim_button_midori);
        }

        else if (selectedStyle.equals("murasaki")){
            rlMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_murasaki));
            btnKanji.setBackgroundResource(R.anim.anim_button_murasaki);
        }
    }

    private View.OnTouchListener playButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.v(TAG, "[Play button was pressed]");
                Intent i = new Intent(Main.this,Play.class);
                startActivity(i);
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.style) {
            Intent i = new Intent(this,SelectStyle.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.options) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
