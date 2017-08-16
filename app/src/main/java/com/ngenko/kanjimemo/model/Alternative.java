package com.ngenko.kanjimemo.model;

/**
 * Created by claudio on 9/14/15.
 */
public class Alternative {

    private int index;
    private String word;

    public Alternative(int index, String word) {
        this.setIndex(index);
        this.setWord(word);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
