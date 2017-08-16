package com.ngenko.kanjimemo.model;

/**
 * Created by claudio on 9/13/15.
 */
public class Kanji {

    private int index;
    private int id;
    private String shin;
    private String grade;
    private String onyomi;
    private String kunyomi;
    private String spanish;
    private String english;

    public Kanji(int index, int id, String shin, String grade, String onyomi, String kunyomi, String spanish, String english) {
        this.index = index;
        this.id = id;
        this.shin = shin;
        this.grade = grade;
        this.onyomi = onyomi;
        this.kunyomi = kunyomi;
        this.spanish = spanish;
        this.english = english;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShin() {
        return shin;
    }

    public void setShin(String shin) {
        this.shin = shin;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getOnyomi() {
        return onyomi;
    }

    public void setOnyomi(String onyomi) {
        this.onyomi = onyomi;
    }

    public String getKunyomi() {
        return kunyomi;
    }

    public void setKunyomi(String kunyomi) {
        this.kunyomi = kunyomi;
    }

    public String getSpanish() {
        return spanish;
    }

    public void setSpanish(String spanish) {
        this.spanish = spanish;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
