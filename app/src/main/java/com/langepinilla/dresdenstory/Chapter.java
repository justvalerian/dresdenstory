package com.langepinilla.dresdenstory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//This class is modelled after general Android architecture advice, see for example https://developer.android.com/codelabs/android-training-livedata-viewmodel#0 for an introduction.

//This class defines how a chapter entity will be represented in the database.

@Entity(tableName = "chapters")
public class Chapter {

    @PrimaryKey //set chapter number as primary key, so has to be unique
    private int number;
    private String name;
    @ColumnInfo(name = "location_text") //this declares a different name for the column in the SQLite database
    private String locationText;
    @ColumnInfo(name = "location_lat")
    private double locationLat;
    @ColumnInfo(name = "location_lon")
    private double locationLon;
    @ColumnInfo(name = "short_description")
    private String shortDescription;
    private String content;
    private String question;
    private String hint;
    private String answer;
    private boolean unlocked;

    //getters and setters for all the variables, can be generated automatically with Alt+Insert -> 'Getter and Setter' in Android Studio

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(double locationLon) {
        this.locationLon = locationLon;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    //constructor to create a new chapter, can be generated automatically with Alt+Insert -> 'Constructor' in Android Studio

    public Chapter(int number, String name, String locationText, double locationLat, double locationLon, String shortDescription, String content, String question, String hint, String answer, boolean unlocked) {
        this.number = number;
        this.name = name;
        this.locationText = locationText;
        this.locationLat = locationLat;
        this.locationLon = locationLon;
        this.shortDescription = shortDescription;
        this.content = content;
        this.question = question;
        this.hint = hint;
        this.answer = answer;
        this.unlocked = unlocked;
    }
}

