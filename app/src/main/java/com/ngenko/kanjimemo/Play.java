package com.ngenko.kanjimemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ngenko.kanjimemo.lib.Constants;
import com.ngenko.kanjimemo.lib.DataBaseHelper;
import com.ngenko.kanjimemo.lib.Query;
import com.ngenko.kanjimemo.lib.Util;
import com.ngenko.kanjimemo.model.Advance;
import com.ngenko.kanjimemo.model.Alternative;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Play extends AppCompatActivity implements Query.updateProgressInterface {

    private static String TAG = "Play";
    private RelativeLayout rlMain;
    private int level;
    private int playCurrentLevel;
    private int currentAdvace;
    private int currentSubtract;
    private int downLimitIndex;
    private int upLimitIndex;
    private int rightAlternative;
    private int currentKanjiIndex;
    private String currentKanjiShin;
    private String currentKanjiOnyomi;
    private String currentKanjiKunyomi;
    private String currentKanjiSpanish;
    private String currentKanjiEnglish;
    private TextView tvLevel;
    private TextView tvKanji;
    private TextView tvOnyomi1;
    private TextView tvOnyomi2;
    private TextView tvOnyomi3;
    private TextView tvKunyomi1;
    private TextView tvKunyomi2;
    private TextView tvKunyomi3;
    private RelativeLayout rlFirstAlternative;
    private RelativeLayout rlSecondAlternative;
    private RelativeLayout rlThirdAlternative;
    private TextView tvFirstAlternative;
    private TextView tvSecondAlternative;
    private TextView tvThirdAlternative;
    private RelativeLayout rlGlobalProgress;
    private RelativeLayout rlCurrentProgress;
    private TextView tvGlobalPercentage;
    private TextView tvCurrentPercentage;

    private String[] kanjiShinArray;
    private String[] kanjiOnyomiArray;
    private String[] kanjiKunyomiArray;
    private String[] kanjiSpanishArray;
    private String[] kanjiEnglishArray;
    private Advance[] advance;
    private int sum;
    private int subtract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play);

        subtract = 0;

        DataBaseHelper dbh = new DataBaseHelper(this);
        dbh.createDataBase();

        rlMain = (RelativeLayout) findViewById(R.id.play_rl_main);
        tvLevel = (TextView) findViewById(R.id.play_tv_level);
        tvKanji = (TextView) findViewById(R.id.play_kanji);
        tvOnyomi1 = (TextView) findViewById(R.id.play_onyomi_1);
        tvOnyomi2 = (TextView) findViewById(R.id.play_onyomi_2);
        tvOnyomi3 = (TextView) findViewById(R.id.play_onyomi_3);
        tvKunyomi1 = (TextView) findViewById(R.id.play_kunyomi_1);
        tvKunyomi2 = (TextView) findViewById(R.id.play_kunyomi_2);
        tvKunyomi3 = (TextView) findViewById(R.id.play_kunyomi_3);
        rlFirstAlternative = (RelativeLayout) findViewById(R.id.play_rl_first_alternative);
        rlSecondAlternative = (RelativeLayout) findViewById(R.id.play_rl_second_alternative);
        rlThirdAlternative = (RelativeLayout) findViewById(R.id.play_rl_third_alternative);
        tvFirstAlternative = (TextView) findViewById(R.id.play_first_alternative);
        tvSecondAlternative = (TextView) findViewById(R.id.play_second_alternative);
        tvThirdAlternative = (TextView) findViewById(R.id.play_third_alternative);
        rlGlobalProgress = (RelativeLayout) findViewById(R.id.play_rl_progress_global);
        rlCurrentProgress = (RelativeLayout) findViewById(R.id.play_rl_progress_current);
        tvGlobalPercentage = (TextView) findViewById(R.id.play_global_percentage);
        tvCurrentPercentage = (TextView) findViewById(R.id.play_current_percentage);

        kanjiShinArray = getResources().getStringArray(R.array.kanji_shin_array);
        kanjiOnyomiArray = getResources().getStringArray(R.array.kanji_onyomi_array);
        kanjiKunyomiArray = getResources().getStringArray(R.array.kanji_kunyomi_array);
        kanjiSpanishArray = getResources().getStringArray(R.array.kanji_spanish_array);
        kanjiEnglishArray = getResources().getStringArray(R.array.kanji_english_array);
        advance = new Advance[Constants.KANJI_X_LEVEL];

        rlFirstAlternative.setOnTouchListener(rlFirstAlternativeListener);
        rlSecondAlternative.setOnTouchListener(rlSecondAlternativeListener);
        rlThirdAlternative.setOnTouchListener(rlThirdAlternativeListener);

        ViewTreeObserver vtoG = rlGlobalProgress.getViewTreeObserver();
        vtoG.addOnGlobalLayoutListener(rlGlobalPressOnGlobalLayoutListener);

        ViewTreeObserver vtoC = rlCurrentProgress.getViewTreeObserver();
        vtoC.addOnGlobalLayoutListener(rlCurrentPressOnGlobalLayoutListener);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("event_change_style"));

        Util.checkPreferences(this,rlMain);
        checkGlobalAdvance();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Util.log(TAG, "Got message: " + message);
            Util.checkPreferences(Play.this, rlMain);
        }
    };

    /**
     * Check the global progress of the game
     * This is the sum of al the games played until now
     */
    private void checkGlobalAdvance() {
        playCurrentLevel = 1;
        String levelString = Util.getSharedPreferences("level", this);
        Util.log(TAG, "level:" + levelString);

        if (levelString.equals("")) {
            Util.log(TAG, "Set level to 1");
            Util.saveSharedPreferences("level", "1", this);
            levelString = "1";
        }
        level = Integer.parseInt(levelString);
        upLimitIndex = Constants.KANJI_X_LEVEL * level;
        downLimitIndex = upLimitIndex - (Constants.KANJI_X_LEVEL-1);

        advance = Query.getAdvance(Play.this, downLimitIndex, upLimitIndex);
        sum = 0;
        for (int i=0; i<Constants.KANJI_X_LEVEL; i++) {
            if (advance[i].getConsecutiveHits() > 5) {
                sum = sum + 5;
            }
            else {
                sum = sum + advance[i].getConsecutiveHits();
            }
        }
        Util.log(TAG, "checkGlobalAdvance() sum:" + sum);
        paintCurrentProgress(sum,0);
        paintNewPlay();
    }

    /**
     * Paint the new play
     *
     */
    private void paintNewPlay() {
        Util.log(TAG, "------ paintNewPlay() ------------------------------------------");
        // Paint global progress
//        paintGlobalProgress(level);

          Util.log(TAG, "downLimitIndex:" + downLimitIndex);
          Util.log(TAG, "upLimitIndex:" + upLimitIndex);
          Util.log(TAG, "playCurrentLevel:" + playCurrentLevel);
          Util.log(TAG, "level:" + level);

        int superDownLimitIndex = downLimitIndex;
        int superUpLimitIndex = upLimitIndex;

        if (level > 1) {
            if (playCurrentLevel == 0) {
                superDownLimitIndex = 1;
                superUpLimitIndex = upLimitIndex - Constants.KANJI_X_LEVEL;
            }
        }

          Util.log(TAG, "superDownLimitIndex:" + superDownLimitIndex);

        // Kanji
        int genIndexKanji = -1;

        if (sum >= 50) {
            Util.log(TAG,"SUM >= 50");
            // Print advance
            ArrayList<Integer> list = new ArrayList<Integer>();

            Util.log(TAG, "****************** ADVANCE ******************");
            for (int i = 0; i < Constants.KANJI_X_LEVEL; i++) {
                Util.log(TAG, "index:" + advance[i].getkIndex() + " consecutive_hits:" + advance[i].getConsecutiveHits());
                if (advance[i].getConsecutiveHits() < 5) {
                    list.add(advance[i].getkIndex());
                }
            }
            genIndexKanji = randomInList(list) - 1; // -1 is for array
        }
        else {
            genIndexKanji = randomInRange(superDownLimitIndex, superUpLimitIndex + 1, "genIndexKanji") - 1; // -1 is for array
        }

        currentKanjiIndex = genIndexKanji;
        Util.log(TAG,"currentKanjiIndex:"+currentKanjiIndex);

        currentKanjiShin = kanjiShinArray[genIndexKanji];
        currentKanjiOnyomi = kanjiOnyomiArray[genIndexKanji];
        currentKanjiKunyomi = kanjiKunyomiArray[genIndexKanji];
        Util.log(TAG,"currentKanjiShin:"+currentKanjiShin);
        tvKanji.setText(currentKanjiShin);

        // Kunyomi
        String[] arrayKunyomi = currentKanjiKunyomi.split("\\|", -1);
        String kunyomi = currentKanjiKunyomi;
        int qKunyomi = arrayKunyomi.length;
        Util.log(TAG,"currentKanjiKunyomi:"+currentKanjiKunyomi);
        Util.log(TAG,"qKunyomi:"+qKunyomi);

        if (qKunyomi == 0) {
            tvKunyomi1.setText("");
            tvKunyomi2.setText("");
            tvKunyomi3.setText("");
        }
        else if (qKunyomi == 1) {
            tvKunyomi1.setText(kunyomi);
            tvKunyomi2.setText("");
            tvKunyomi3.setText("");
        }
        else if (qKunyomi == 2) {
            tvKunyomi1.setText(arrayKunyomi[0]);
            tvKunyomi2.setText(arrayKunyomi[1]);
            tvKunyomi3.setText("");
        }
        else if (qKunyomi >= 3) {
            tvKunyomi1.setText(arrayKunyomi[0]);
            tvKunyomi2.setText(arrayKunyomi[1]);
            tvKunyomi3.setText(arrayKunyomi[2]);
        }

        // Onyomi
        String[] arrayOnyomi = currentKanjiOnyomi.split("\\|", -1);
        String onyomi = currentKanjiOnyomi;
        int qOnyomi = arrayOnyomi.length;
        Util.log(TAG,"currentKanjiOnyomi:"+currentKanjiOnyomi);
        Util.log(TAG,"qOnyomi:"+qOnyomi);

        if (qOnyomi == 0) {
            tvOnyomi1.setText("");
            tvOnyomi2.setText("");
            tvOnyomi3.setText("");
        }
        else if (qOnyomi == 1) {
            tvOnyomi1.setText(currentKanjiOnyomi);
            tvOnyomi2.setText("");
            tvOnyomi3.setText("");
        }
        else if (qOnyomi == 2) {
            tvOnyomi1.setText(arrayOnyomi[0]);
            tvOnyomi2.setText(arrayOnyomi[1]);
            tvOnyomi3.setText("");
        }
        else if (qOnyomi >= 3) {
            tvOnyomi1.setText(arrayOnyomi[0]);
            tvOnyomi2.setText(arrayOnyomi[1]);
            tvOnyomi3.setText(arrayOnyomi[2]);
        }

        rightAlternative = randomInRange(1, 4, "rightAlternative");
          Util.log(TAG, "genAlternativePos:" + rightAlternative);

        String rawWord;
        String word;

        if (Locale.getDefault().getLanguage().equals("es")) {
            rawWord = kanjiSpanishArray[genIndexKanji];

        }
        else {
            rawWord = kanjiEnglishArray[genIndexKanji];
        }

        String[] arrayWord = rawWord.split("\\|", -1);
        int qWords = arrayWord.length;
          Util.log(TAG,"rawWord:"+rawWord);
          Util.log(TAG,"qWords:"+qWords);

        if (qWords > 1) {
            int genPosWord = randomInRange(0,qWords, "genPosWord");
            Util.log(TAG,"genPosWord:"+genPosWord);
            word = arrayWord[genPosWord];
        }
        else {
            word = rawWord;
        }

        Alternative wrongAlternative1; // = getWrongAlternative(genIndexKanji+1, -1, word, "abcxyz");
        Alternative wrongAlternative2; // = getWrongAlternative(genIndexKanji+1, wrongAlternative1.getIndex(), word, wrongAlternative1.getWord());

        Util.log(TAG,"word:"+word);

        do {
            wrongAlternative1 = getWrongAlternative(genIndexKanji+1, -1);
            Util.log(TAG,"do while word1:"+wrongAlternative1.getWord());
        }
        while (wrongAlternative1.getWord().equals(word));

        Util.log(TAG,"word1:"+wrongAlternative1.getWord());

        do {
            wrongAlternative2 = getWrongAlternative(genIndexKanji+1, wrongAlternative1.getIndex());
            Util.log(TAG,"do while word2:"+wrongAlternative2.getWord());
        }
        while (wrongAlternative2.getWord().equals(word) || wrongAlternative2.getWord().equals(wrongAlternative1.getWord()));

        Util.log(TAG,"word2:"+wrongAlternative2.getWord());
        if (rightAlternative == 1) {
            tvFirstAlternative.setText(word);
            tvSecondAlternative.setText(wrongAlternative1.getWord());
            tvThirdAlternative.setText(wrongAlternative2.getWord());
        }
        else if (rightAlternative == 2) {
            tvFirstAlternative.setText(wrongAlternative1.getWord());
            tvSecondAlternative.setText(word);
            tvThirdAlternative.setText(wrongAlternative2.getWord());
        }
        else if (rightAlternative == 3) {
            tvFirstAlternative.setText(wrongAlternative1.getWord());
            tvSecondAlternative.setText(wrongAlternative2.getWord());
            tvThirdAlternative.setText(word);
        }

        if (playCurrentLevel == 0) {
            playCurrentLevel = 1;
        }
        else if (playCurrentLevel == 1) {
            playCurrentLevel = 2;
        }
        else {
            playCurrentLevel = 0;
        }
    }

    /**
     * Return wrong options for a play
     * @param kanjiIndex
     * @param alternativeIndex
     * @return
     */
    private Alternative getWrongAlternative(int kanjiIndex, int alternativeIndex) {
        int genIndexKanji;

        while (true) {
            genIndexKanji = randomInRange(downLimitIndex, upLimitIndex, "genIndexKanji");

            if (genIndexKanji != kanjiIndex &&
                genIndexKanji != alternativeIndex) {
                break;
            }
        }

        String rawWord;
        String word;

        if (Locale.getDefault().getLanguage().equals("es")) {
            rawWord = kanjiSpanishArray[genIndexKanji-1];
        }
        else {
            rawWord = kanjiEnglishArray[genIndexKanji-1];
        }

        String[] arrayWord = rawWord.split("\\|", -1);
        int qWords = arrayWord.length;
          Util.log(TAG,"rawWord:"+rawWord);
          Util.log(TAG,"qWords:"+qWords);

        if (qWords > 1) {
            int genPosWord = randomInRange(0,qWords, "genPosWord");
//            Util.log(TAG,"genPosWord:"+genPosWord);
            word = arrayWord[genPosWord];
        }
        else {
            word = rawWord;
        }

        return new Alternative(genIndexKanji,word);
    }

    /**
     * Generate a value in a given range
     * @param low
     * @param high
     * @param tag
     * @return
     */
    private int randomInRange(int low, int high, String tag) {
          Util.log(TAG, "tag:" + tag);
          Util.log(TAG, "low:" + low);
          Util.log(TAG, "high:" + high);
        Random r = new Random();
        int rnd = r.nextInt(high-low) + low;
          Util.log(TAG, "rnd:" + rnd);
        return rnd;
    }

    /**
     * Return a random value in a given list
     * @param list
     * @return
     */
    private int randomInList(ArrayList<Integer> list) {
        boolean keep = true;
        int generated = -1;

//        while (keep) {
        if (list.size() > 1) {
            Random r = new Random();
            generated = r.nextInt((list.size() - 1) - 0) + 0;
            Util.log(TAG, "generated:" + generated);
            Util.log(TAG,"selected:"+list.get(generated).intValue());
            return list.get(generated).intValue();
        }
      else {
            Util.log(TAG,"selected:"+list.get(0).intValue());
            return list.get(0).intValue();
      }

    }

    /**
     * Check if the entered option is right or not
     * @param position
     */
    private void checkPlay(int position){

          Util.log(TAG, "rightAlternative:"+rightAlternative+" position:"+position);
        int updateAction;

        if (rightAlternative == position) {
            Util.log(TAG,"[Right Answer]");
            resultAnimation(R.drawable.maru_animation);
            updateAction = Constants.INCREMENT_ADVANCE;
        }
        else {
            Util.log(TAG,"[Bad Answer]");
            resultAnimation(R.drawable.batsu_animation);
            updateAction = Constants.RESET_ADVANCE;
        }

        sum = 0;
        String pre = "";
        String post = "";

        int factor = (Constants.KANJI_X_LEVEL * level) - Constants.KANJI_X_LEVEL;
        Util.log(TAG,"factor:"+factor);

        for (int i=0; i<Constants.KANJI_X_LEVEL; i++) {

            if ((i+factor) == currentKanjiIndex) {
                if (updateAction == Constants.INCREMENT_ADVANCE) {
                    Query.updateAdvance(Play.this, currentKanjiIndex+1, advance[i].getConsecutiveHits()+1, advance[i].getConsecutiveHits()+1, currentKanjiShin);
                    pre = "advance[" + i + "].getkIndex():" + advance[i].getkIndex() + " advance[" + i + "].getConsecutiveHits():" + advance[i].getConsecutiveHits();
                    advance[i].setConsecutiveHits(advance[i].getConsecutiveHits() + 1);
                    advance[i].setTotalHits(advance[i].getTotalHits() + 1);
                    post = "advance[" + i + "].getkIndex():" + advance[i].getkIndex() + " advance[" + i + "].getConsecutiveHits():" + advance[i].getConsecutiveHits();
                    if (advance[i].getConsecutiveHits() > 5) {
                        sum = sum + 5;
                    }
                    else {
                        sum = sum + advance[i].getConsecutiveHits();
                        if (subtract > 0) {
                            subtract--;
                        }
                    }

                }
                else {
                    subtract = advance[i].getConsecutiveHits() + subtract;
                    Query.resetAdvance(Play.this, currentKanjiIndex + 1, 0, advance[i].getMistakes()+1);
                    advance[i].setConsecutiveHits(0);
                    advance[i].setMistakes(advance[i].getMistakes()+1);
                }
            }
            else {
                if (advance[i].getConsecutiveHits() > 5) {
                    sum = sum + 5;
                }
                else {
                    sum = sum + advance[i].getConsecutiveHits();
                }
            }
            Util.log(TAG,"shin:"+kanjiShinArray[i+factor]+" advance["+i+"].getkIndex():"+advance[i].getkIndex()+" advance["+i+"].getConsecutiveHits():"+advance[i].getConsecutiveHits());
        }
          Util.log(TAG, "PRE "+pre);
          Util.log(TAG, "POST "+post);
          Util.log(TAG, "checkPlay() sum:" + sum);

        if (sum >= (Constants.KANJI_X_LEVEL * Constants.NEEDED_HITS_X_KANJI)) {
            String levelString = Util.getSharedPreferences("level", Play.this);
            Util.log(TAG, "checkLevel() levelString:" + levelString);
            int newLevel = Integer.parseInt(levelString) + 1;
            Util.log(TAG, "checkLevel() newLevel:" + newLevel);
            Util.saveSharedPreferences("level", String.valueOf(newLevel), Play.this);
            level++;
            downLimitIndex = downLimitIndex + Constants.KANJI_X_LEVEL;
            upLimitIndex = upLimitIndex + Constants.KANJI_X_LEVEL;
            paintGlobalProgress(level);
            advance = Query.getAdvance(Play.this, downLimitIndex, upLimitIndex);
            sum = 0;
            subtract = 0;
        }

        paintCurrentProgress(sum,subtract);
        paintNewPlay();
    }

    /**
     * Shows a maru or batsu animation
     * @param resource
     */
    private void resultAnimation(int resource){
        ImageView img = (ImageView)findViewById(R.id.play_result_img);
        img.setBackgroundResource(resource);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

        if (frameAnimation.isRunning()){
            frameAnimation.stop();
        }
        // Start the animation (looped playback by default).
        frameAnimation.start();
    }

    private View.OnTouchListener rlFirstAlternativeListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            Util.log(TAG, "first onTouch()");
            checkPlay(1);
            return false;
        }
    };

    private View.OnTouchListener rlSecondAlternativeListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            Util.log(TAG, "second onTouch()");
            checkPlay(2);
            return false;
        }
    };

    private View.OnTouchListener rlThirdAlternativeListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            Util.log(TAG, "third onTouch()");
            checkPlay(3);
            return false;
        }
    };

    /**
     * Show the Global progress on the game
     * @param currentLevel
     */
    private void paintGlobalProgress(int currentLevel) {

          Util.log(TAG,"total_levels:"+Integer.valueOf(Constants.TOTAL_LEVELS).floatValue());
          Util.log(TAG,"current_level:"+Integer.valueOf(currentLevel).floatValue());

        float percentageF = (Integer.valueOf(currentLevel).floatValue() / Integer.valueOf(Constants.TOTAL_LEVELS).floatValue()) * 100.0f;

          Util.log(TAG,"percentageF:"+percentageF);

        RelativeLayout rlPercentage = (RelativeLayout) findViewById(R.id.play_rl_global_percentage);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlPercentage.setLayoutParams(lp);

        int rlGPWidth = rlGlobalProgress.getWidth();
        float onePercent = Integer.valueOf(rlGPWidth).floatValue() / 100.0f;
        float percentageWidthF = onePercent * percentageF;
        Util.log(TAG,"Float.valueOf(percentageWidthF).intValue():"+Float.valueOf(percentageWidthF).intValue());
        if (currentLevel == 1) {
            lp.width = 0;
        }
        else {
            lp.width = Float.valueOf(percentageWidthF).intValue();
        }

        rlPercentage.setBackgroundColor(Color.parseColor("#09B50F")); // Green
        tvLevel.setText(getResources().getString(R.string.play_level)+" "+currentLevel);
        tvGlobalPercentage.setText(Float.valueOf(percentageF).intValue()+" %");
    }

    /**
     * Paint the progress of the current game
     * @param hits
     * @param subtracted
     */
    private void paintCurrentProgress(int hits, int subtracted) {
        Util.log(TAG,"paintCurrentProgress()");
        Util.log(TAG,"hits:"+hits);

        float percentageF = (Integer.valueOf(hits).floatValue() / 60.0f) * 100.0f;

        RelativeLayout rlPercentage = (RelativeLayout) findViewById(R.id.play_rl_current_percentage);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlPercentage.setLayoutParams(lp);

        int rlGPWidth = rlGlobalProgress.getWidth();

        float onePercent = Integer.valueOf(rlGPWidth).floatValue() / 100.0f;

          Util.log(TAG, "rlGPWidth:" + rlGPWidth);
          Util.log(TAG, "onePercent:" + onePercent);

//        float percentageWidthF = onePercent * Integer.valueOf(percentage).floatValue();
        float percentageWidthF = onePercent * percentageF;

        lp.width = Float.valueOf(percentageWidthF).intValue();

          Util.log(TAG, "percentageWidthF:" + percentageWidthF);
          Util.log(TAG, "lp.width:" + lp.width);

        rlPercentage.setBackgroundColor(Color.parseColor("#09B50F")); // Green

        // Paint subtracted
        RelativeLayout rlSubtracted = (RelativeLayout) findViewById(R.id.play_rl_current_subtracted);
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlSubtracted.setLayoutParams(lps);
        float subtractedWidthF = onePercent * Integer.valueOf(subtracted).floatValue();
        lps.width = Float.valueOf(subtractedWidthF).intValue();
          Util.log(TAG, "subtractedWidthF:" + subtractedWidthF);
          Util.log(TAG, "lps.width:" + lps.width);
        rlSubtracted.setBackgroundColor(Color.parseColor("#F72F2F")); // Red
        lps.addRule(RelativeLayout.RIGHT_OF, rlPercentage.getId());

        tvCurrentPercentage.setText(Float.valueOf(percentageF).intValue()+" %");
    }

    /**
     * Update in the screen the progress of the current game
     * @param percentage
     */
    public void updateCurrentProgress(int percentage) {
        paintCurrentProgress(percentage,0);
    }

    private ViewTreeObserver.OnGlobalLayoutListener rlGlobalPressOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            paintGlobalProgress(level);
            rlGlobalProgress.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener rlCurrentPressOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            paintCurrentProgress(sum, 0);
            rlCurrentProgress.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.style) {
            Intent i = new Intent(this,SelectStyle.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.help) {
            Intent i = new Intent(this,Help.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.about) {
            Intent i = new Intent(this,About.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
