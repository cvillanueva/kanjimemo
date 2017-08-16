package com.ngenko.kanjimemo.lib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.ngenko.kanjimemo.model.Advance;
import com.ngenko.kanjimemo.model.Kanji;

import java.util.ArrayList;

/**
 * Created by claudio on 9/13/15.
 */
public class Query {

    private static String TAG = "Query";
    private static updateProgressInterface listener = null;

    public static Kanji getKanji(Context context, int index) {
        DataBaseHelper dbh = new DataBaseHelper(context);

        try {
//            SQLiteDatabase db = dbh.getReadableDatabase();
            SQLiteDatabase db = dbh.getWritableDatabase();

            if (db != null) {
                String query = "SELECT k_index, k_id, k_shin, k_grade, k_onyomi, k_kunyomi, k_spanish, k_english FROM kanji WHERE k_index = " + index;
                String[] args = new String[0];
                Cursor c = db.rawQuery(query, args);
                c.moveToNext();
                return new Kanji(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7));
            } else {
                return null;
            }
        }
        catch (SQLiteCantOpenDatabaseException e) {
            Util.log(TAG,"CATCH");
            e.printStackTrace();
            return null;
        }
//        finally {
//            Util.log(TAG,"FINALLY");
//            // 500 ms pause
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
////                    my_button.setBackgroundResource(R.drawable.defaultcard);
//                }
//            }, 500);
//            return null;
//        }

    }

//    /*   E L I M I N A R  E S T E M E T O D O  S E  D E M O R A  M U C H O   */
//    public static void initializeAdvance(Context context) {
//        DataBaseHelper th = new DataBaseHelper(context);
//        SQLiteDatabase db = th.getWritableDatabase();
//
//        if(db != null) {
//            for (int i=1; i<1945; i++) {
//                db.execSQL("INSERT INTO advance (k_index,adv_consecutive_hits,adv_mistake,adv_total_hits) VALUES (" + i + ",0,0,0)");
//            }
//        }
//        db.close();
//        th.close();
//    }

    public static void updateAdvance(Context context, int k_index, int currentConsecutiveHits, int currentTotalHits, String k_shin) {
//        Advance advance = getAdvance(context, k_index);
//        Util.log(TAG,"------ updateAdvance() ------------------------------------------");
//        Util.log(TAG,"k_index:"+k_index);
//        Util.log(TAG,"k_shin:"+k_shin);
//        Util.log(TAG,"currentConsecutiveHits:"+currentConsecutiveHits);
//        Util.log(TAG,"currentTotalHits:"+currentTotalHits);

        DataBaseHelper th = new DataBaseHelper(context);
        SQLiteDatabase db = th.getWritableDatabase();

        if(db != null) {
            db.execSQL("UPDATE advance set adv_consecutive_hits = " + currentConsecutiveHits + ", adv_total_hits = " + currentTotalHits +
                       " WHERE k_index = "+k_index);
        }
        db.close();
        th.close();
    }


    public static void resetAdvance(Context context, int k_index, int currentConsecutiveHits, int currentMistakes) {
//        Advance advance = getAdvance(context, k_index);
        Util.log(TAG,"------ resetAdvance() ------------------------------------------");
        Util.log(TAG,"k_index:"+k_index);
        Util.log(TAG,"currentConsecutiveHits:"+currentConsecutiveHits);
        Util.log(TAG,"currentMistakes:"+currentMistakes);

        DataBaseHelper th = new DataBaseHelper(context);
        SQLiteDatabase db = th.getWritableDatabase();

        if(db != null) {
            db.execSQL("UPDATE advance set adv_consecutive_hits = " + currentConsecutiveHits + ", adv_mistake = " + currentMistakes +
                    " WHERE k_index = "+k_index);
        }
        db.close();
        th.close();
    }

    public static Advance[] getAdvance(Context context, int downLimitIndex, int upLimitIndex){
//        Util.log(TAG,"------ getAdvance() ------------------------------------------");
//        Util.log(TAG, "downLimitIndex:" + downLimitIndex);
//        Util.log(TAG, "upLimitIndex:" + upLimitIndex);

        DataBaseHelper dbh = new DataBaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        String[] args = new String[0];
        Advance[] advance = new Advance[Constants.KANJI_X_LEVEL];

        String query = "";

        for (int i=downLimitIndex; i<=upLimitIndex; i++) {
            if (i<upLimitIndex) {
                query += "select k_index,adv_consecutive_hits,adv_mistake,adv_total_hits from advance where k_index = " + i + " union ";
            }
            else {
                query += "select k_index,adv_consecutive_hits,adv_mistake,adv_total_hits from advance where k_index = " + i;
            }
        }

        Cursor c = db.rawQuery(query, args);

        int i = 0;
        if (c.moveToFirst()){
            do{
                advance[i] = new Advance(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3));
//                Util.log(TAG," k_index:"+c.getInt(0)+" adv_consecutive_hits:"+c.getInt(1)+" adv_mistake:"+c.getInt(2)+" adv_total_hits:"+c.getInt(3)) ;
                i++;
            }
            while (c.moveToNext());
        }
        if (c != null && !c.isClosed()){
            c.close();
        }

        db.close();
        dbh.close();

        return advance;
    }

    public static boolean checkLevel(Context context, int downLimitIndex, int upLimitIndex, int k_index, int updateAction, String kShin) {
        DataBaseHelper dbh = new DataBaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        String[] args = new String[0];
        int sum = 0;

        Util.log(TAG,"------ checkLevel() ------------------------------------------");
        Util.log(TAG, "downLimitIndex:" + downLimitIndex);
        Util.log(TAG, "upLimitIndex:" + upLimitIndex);
        Util.log(TAG, "k_index:" + k_index);
        Util.log(TAG, "kShin:" + kShin);

        String query = "";

        for (int i=downLimitIndex; i<=upLimitIndex; i++) {
            if (i<upLimitIndex) {
                query += "select advance.k_index,adv_consecutive_hits,adv_total_hits,adv_mistake,kanji.k_shin from advance,kanji where advance.k_index=kanji.k_index and advance.k_index = " + i + " union ";
            }
            else {
                query += "select advance.k_index,adv_consecutive_hits,adv_total_hits,adv_mistake,kanji.k_shin from advance,kanji where advance.k_index=kanji.k_index and advance.k_index = " + i;
            }
        }

        Util.log(TAG,"query:"+query);
        int consecutivehitsToUpdate = 0;
        int totalhitsToUpdate = 0;
        int mistakeToUpdate = 0;
        String kShinToUpdate = "";

        Cursor c = db.rawQuery(query, args);
        ArrayList<String> data = new ArrayList<String>();

        if (c.moveToFirst()){
            do{
                data.add(c.getString(0));
                if (c.getInt(0) == k_index) {
                    consecutivehitsToUpdate = c.getInt(1) + 1;
                    totalhitsToUpdate = c.getInt(2) + 1;
                    mistakeToUpdate = c.getInt(3) + 1;
                    kShinToUpdate = c.getString(4);
                }
                if (c.getInt(1) > 5) {
                    sum = sum + 5;
                }
                else {
                    sum = sum + c.getInt(1);
                }
                Util.log(TAG, "k_index:" + c.getInt(0));
                Util.log(TAG, "adv_consecutive_hits:" + c.getInt(1));
                Util.log(TAG, "k_shin:" + c.getString(4));
                Util.log(TAG, "pre sum:" + sum+ " ---------------------");

            }
            while (c.moveToNext());
        }
        if (c != null && !c.isClosed()){
            c.close();
        }

        db.close();
        dbh.close();

        if (updateAction == Constants.INCREMENT_ADVANCE) {
            updateAdvance(context, k_index, consecutivehitsToUpdate, totalhitsToUpdate, kShin);
        }
        else {
            resetAdvance(context, k_index, consecutivehitsToUpdate, mistakeToUpdate);
        }

        Util.log(TAG, "sum:" + sum);
        listener.updateCurrentProgress(sum);
        Util.log(TAG, "****************************************");
        if (sum >= (Constants.KANJI_X_LEVEL * Constants.NEEDED_HITS_X_KANJI)) {
            String levelString = Util.getSharedPreferences("level", context);
            Util.log(TAG, "checkLevel() levelString:" + levelString);
            int newLevel = Integer.parseInt(levelString) + 1;
            Util.log(TAG, "checkLevel() newLevel:" + newLevel);
            Util.saveSharedPreferences("level", String.valueOf(newLevel), context);
            return true;
        }
        else {
            return false;
        }
    }

    public static void updateProgressListener(Object invokerClass) {
        listener = (updateProgressInterface) invokerClass;
    }

    public interface updateProgressInterface {
        void updateCurrentProgress(int percentage);
    }
}
