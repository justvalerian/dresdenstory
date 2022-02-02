package com.langepinilla.dresdenstory;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//This class is modelled after general Android architecture advice, see for example https://developer.android.com/codelabs/android-training-livedata-viewmodel#0 for an introduction.

//The DAO is the interface to define methods to access and manipulate the SQLite database, here you can create Java methods that execute SQL queries.
@Dao
public interface ChapterDAO {
    @Insert
    void insert(Chapter chapter);

    @Update(entity = Chapter.class)
    void updateUnlocked(ChapterUpdateUnlocked chapterUpdateUnlocked);

    @Query("DELETE FROM chapters")
    void deleteAll();

    @Query("SELECT COUNT(number) FROM chapters")
    int getChapterCount();

    @Query("SELECT * FROM chapters")
    LiveData<List<Chapter>> getAllChapters();

    @Query("SELECT * FROM chapters WHERE unlocked = :unlock")
    LiveData<List<Chapter>> getAllUnlockedChapters(boolean unlock);

    @Query("SELECT * FROM chapters WHERE number = :chapter_number")
    LiveData<Chapter> getChapterInfoByNumber(int chapter_number);
}