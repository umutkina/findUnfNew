package com.umutkina.findunfollowersapp.modals;

import java.io.Serializable;

/**
 * Created by mac on 17/01/16.
 */
public class YdsWord implements Serializable {

    private static final long serialVersionUID = 4229945411325978779L;

    public YdsWord(int id, String word, String translatedWord, String similarWord, String oppositeWord, int read) {
        this.id = id;
        this.word = word;
        this.translatedWord = translatedWord;
        this.similarWord = similarWord;
        this.oppositeWord = oppositeWord;
        this.read = read;
    }

    public YdsWord() {
    }

    ;
    int id;
    String word;
    String translatedWord;
    String similarWord;
    String oppositeWord;
    int read;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public void setTranslatedWord(String translatedWord) {
        this.translatedWord = translatedWord;
    }

    public String getSimilarWord() {
        return similarWord;
    }

    public void setSimilarWord(String similarWord) {
        this.similarWord = similarWord;
    }

    public String getOppositeWord() {
        return oppositeWord;
    }

    public void setOppositeWord(String oppositeWord) {
        this.oppositeWord = oppositeWord;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }
}
