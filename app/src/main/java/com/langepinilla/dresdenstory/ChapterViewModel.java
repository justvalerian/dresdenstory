package com.langepinilla.dresdenstory;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

//this class is modelled after general Android architecture advice, see for example https://developer.android.com/codelabs/android-training-livedata-viewmodel#0 for an introduction

//

public class ChapterViewModel extends AndroidViewModel {

    private ChapterRepository mRepository;
    private LiveData<List<Chapter>> mAllChapters;

    public ChapterViewModel(Application application) {
        super(application);
        mRepository = new ChapterRepository(application);
        mAllChapters = mRepository.getAllChapters();
    }

    LiveData<List<Chapter>> getAllChapters() {
        return mAllChapters;
    }

    public LiveData<List<Chapter>> getAllUnlockedChapters(boolean isUnlocked) {
        return mRepository.getAllUnlockedChapters(isUnlocked);
    }

    LiveData<Chapter> getChapter(int chapterNumber) {
        return mRepository.getChapter(chapterNumber);
    }

    int getChapterCount() {
        return mRepository.getChapterCount();
    }

    public void updateUnlocked(ChapterUpdateUnlocked chapterUpdateUnlocked) {
        mRepository.updateUnlocked(chapterUpdateUnlocked);
    }

    public void insert(Chapter chapter) {
        mRepository.insert(chapter);
    }
}
