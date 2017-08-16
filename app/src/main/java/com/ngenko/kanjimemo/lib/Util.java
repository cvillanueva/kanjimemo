package com.ngenko.kanjimemo.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RelativeLayout;

import com.ngenko.kanjimemo.R;

public class Util {

	private static String TAG = "Util";

	/**
	 * Print a log only if the PRINT_LOG constant is true
	 * @param TAG
	 * @param message
	 */
	public static void log(String TAG, String message){
		if (Constants.PRINT_LOG == true){
			Log.e(TAG, "["+message+"]");
		}
	}

	/**
	 * Simplified get shared preference method
	 * @param key
	 * @param c
	 * @return
	 */
	public static String getSharedPreferences(String key, Context c) {
		String data="";
		try {
			SharedPreferences sharedPreferences = c.getSharedPreferences("sharedprefs",Context.MODE_PRIVATE);
			data = sharedPreferences.getString(key,"");
			log(TAG, "getSharedPreferences() key:"+key+" | data:"+data);
		}
		catch (Exception e){
			e.printStackTrace();
			data = "";
		}
		return data;
	}

	/**
	 * Simplified save share preferences method
	 * @param key
	 * @param value
	 * @param c
	 * @return
	 */
	public static boolean saveSharedPreferences(String key, String value, Context c) {
		try {
			SharedPreferences sharedPreferences = c.getSharedPreferences("sharedprefs",Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(key,value);
			editor.commit();
			return true;
		}
		catch (Exception e){
			return false;
		}
	}

	/**
	 *  Style preferences are checked before paint the screen.
	 */
	public static void checkPreferences(Context context, RelativeLayout rl){
		String selectedStyle = Util.getSharedPreferences("style_selected", context);

		Util.log(TAG, "selectedStyle:" + selectedStyle);

		if (selectedStyle.equals("kuro")){
			rl.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_kuro));
		}

		else if (selectedStyle.equals("midori")){
			rl.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_midori));
		}

		else if (selectedStyle.equals("murasaki")){
			rl.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_murasaki));
		}
	}
}
