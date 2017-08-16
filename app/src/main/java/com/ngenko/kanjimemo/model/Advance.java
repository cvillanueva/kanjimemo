package com.ngenko.kanjimemo.model;

/**
 * Created by claudio on 9/26/15.
 */
public class Advance {

    private int kIndex;
    private int consecutiveHits;
    private int mistakes;
    private int totalHits;

    public Advance(int kIndex, int consecutiveHits, int mistakes, int totalHits) {
        this.setkIndex(kIndex);
        this.setConsecutiveHits(consecutiveHits);
        this.setMistakes(mistakes);
        this.setTotalHits(totalHits);
    }

    public int getkIndex() {
        return kIndex;
    }

    public void setkIndex(int kIndex) {
        this.kIndex = kIndex;
    }

    public int getConsecutiveHits() {
        return consecutiveHits;
    }

    public void setConsecutiveHits(int consecutiveHits) {
        this.consecutiveHits = consecutiveHits;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

}
