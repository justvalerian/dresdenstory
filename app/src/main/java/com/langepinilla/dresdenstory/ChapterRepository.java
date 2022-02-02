package com.langepinilla.dresdenstory;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

//This class is modelled after general Android architecture advice, see for example https://developer.android.com/codelabs/android-training-livedata-viewmodel#0 for an introduction.

//The repository is the single source of truth for app data that all UI code will communicate with; if we would use multiple databases we would connect them here, the view model still only connects to the repository and the UI classes to the view model.

//Note that Room normally shouldn't run on the main thread (and doesn't, unless you pass allowMainThreadQueries); LiveData automatically takes care of this, for all other methods we need to define a thread to run on.

public class ChapterRepository {
    //initialize variables
    private final ChapterDAO mChapterDAO;
    private final LiveData<List<Chapter>> mAllChapters;
    private int mChapterCount;

    //constructor, here we also create the DatabaseConnection db
    ChapterRepository(Application application) {
        DatabaseConnection db = DatabaseConnection.getDatabase(application);
        mChapterDAO = db.getChapterDAO();
        mAllChapters = mChapterDAO.getAllChapters();
    }

    public LiveData<List<Chapter>> getAllChapters() {
        return mAllChapters;
    }

    public LiveData<List<Chapter>> getAllUnlockedChapters(boolean isUnlocked) {
        return mChapterDAO.getAllUnlockedChapters(isUnlocked);
    }

    public LiveData<Chapter> getChapter(int chapterNumber) {
        return mChapterDAO.getChapterInfoByNumber(chapterNumber);
    }

    //use the databaseWriteExecutor of the DatabaseConnection class to handle the thread in which we set the chapter to unlocked
    public void updateUnlocked(ChapterUpdateUnlocked chapterUpdateUnlocked) {
        DatabaseConnection.databaseWriteExecutor.execute(() -> mChapterDAO.updateUnlocked(chapterUpdateUnlocked));
    }

    //For getChapterCount, we join the created thread with the one from which the method was called, in our case the UI thread, this is because only getting the chapter count is a quick query and we want to run it (through the view model) in the OnCreate() method of the StoryActivity.
    public Integer getChapterCount() {
        Thread thread = new Thread (() -> mChapterCount = mChapterDAO.getChapterCount());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            thread.interrupt();
        }
        return mChapterCount;
    }

    public void insert(Chapter chapter) {
        DatabaseConnection.databaseWriteExecutor.execute(() -> mChapterDAO.insert(chapter));
    }


}
