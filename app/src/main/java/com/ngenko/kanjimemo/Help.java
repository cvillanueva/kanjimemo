package com.ngenko.kanjimemo;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ngenko.kanjimemo.lib.MyExpandableAdapter;
import com.ngenko.kanjimemo.lib.Util;

import java.util.ArrayList;

public class Help extends AppCompatActivity {

	private static String TAG = "Help";
	private LinearLayout llMain;
	ArrayList<String> parentItems = new ArrayList<String>();
	ArrayList<Object> childItems = new ArrayList<Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		llMain = (LinearLayout) findViewById(R.id.help_ll_main);

		checkPreferences();
		fillExpandable();
	}

	/**
	 *  Style preferences are checked before paint the screen.
	 */
	private void checkPreferences() {
		String selectedStyle = Util.getSharedPreferences("style_selected", this);

		Util.log(TAG, "selectedStyle:" + selectedStyle);

		if (selectedStyle.equals("kuro")){
			llMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_kuro));
		}

		else if (selectedStyle.equals("midori")){
			llMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_midori));
		}

		else if (selectedStyle.equals("murasaki")){
			llMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_murasaki));
		}
	}

	private void fillExpandable() {

		ExpandableListView expandableList = (ExpandableListView) findViewById(R.id.help_elv);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		expandableList.setLayoutParams(lp);
		expandableList.setDividerHeight(2);
		expandableList.setGroupIndicator(null);
		expandableList.setClickable(true);

		setGroupParents();
		setChildData();

		MyExpandableAdapter adapter = new MyExpandableAdapter(parentItems, childItems);

		adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
		expandableList.setAdapter(adapter);
		expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Util.log(TAG,"Press onChildClick()");
				return false;
			}
		});
	}

	public void setGroupParents() {
		parentItems.add(getResources().getString(R.string.help_question1));
		parentItems.add(getResources().getString(R.string.help_question2));
		parentItems.add(getResources().getString(R.string.help_question3));
//		parentItems.add("Enterprise Java");
	}

	public void setChildData() {

		// Android
		ArrayList<String> child = new ArrayList<String>();
		child.add(getResources().getString(R.string.help_answer1));
		childItems.add(child);

		// Core Java
		child = new ArrayList<String>();
		child.add(getResources().getString(R.string.help_answer2));
		childItems.add(child);

		// Desktop Java
		child = new ArrayList<String>();
		child.add(getResources().getString(R.string.help_answer3));
		childItems.add(child);

	}
}
