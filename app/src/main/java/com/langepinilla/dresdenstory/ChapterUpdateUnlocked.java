package com.langepinilla.dresdenstory;

//partial entity of entity Chapter used to update a chapter's 'unlocked'-variable
// this needs the chapter number to find the actual chapter in the database and an unlocked state so it can set the new state in the database
public class ChapterUpdateUnlocked {
    int number;
    boolean unlocked;

    public ChapterUpdateUnlocked(int number, boolean unlocked) {
        this.number = number;
        this.unlocked = unlocked;
    }
}
