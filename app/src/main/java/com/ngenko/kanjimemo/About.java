package com.ngenko.kanjimemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.ngenko.kanjimemo.lib.Util;

public class About extends AppCompatActivity {

	private RelativeLayout rlMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		rlMain = (RelativeLayout) findViewById(R.id.about_rl_main);

		Util.checkPreferences(this,rlMain);
	}
}
