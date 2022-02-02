package com.langepinilla.dresdenstory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

//The second of our three activities and the one that displays the contents for each chapter using fragments.

public class StoryActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;
    FragmentManager fm;
    int numberOfChapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //code for the tabs and the chapter fragment view
        tabLayout = findViewById(R.id.tab_story);
        pager2 = findViewById(R.id.view_pager2);
        fm = getSupportFragmentManager();

        //initiate ViewModel, which holds the connection to the repository and therefore to the database
        ChapterViewModel mChapterViewModel = new ViewModelProvider(this).get(ChapterViewModel.class);

        //load number of chapters on activity start and use this to initialize fragment adapter
        numberOfChapters = mChapterViewModel.getChapterCount();

        adapter = new FragmentAdapter(fm, getLifecycle(), numberOfChapters);
        pager2.setAdapter(adapter);

        //observe for changes in the database and set tab icons depending on lock/unlock
        mChapterViewModel.getAllChapters().observe(this, chapters -> {
            pager2.setAdapter(adapter);
            //reset tabs before adding them
            tabLayout.removeAllTabs();
            //store tabs in array to be able to change icons later
            TabLayout.Tab[] chapterTabs = new TabLayout.Tab[numberOfChapters];
            //create introduction tab
            chapterTabs[0] = tabLayout.newTab().setText(R.string.chapter_title_introduction);
            //create all other tabs
            for (int i = 1; i < numberOfChapters; i++) {
                chapterTabs[i] = tabLayout.newTab().setText(getText(R.string.chapter_title) + String.valueOf(i));
            }
            //set icon according to unlocked variable of the chapter
            for (int i = 0; i < numberOfChapters; i++) {
                if (chapters.get(i).isUnlocked()) {
                    chapterTabs[i].setIcon(R.drawable.ic_lock_open);
                } else {
                    chapterTabs[i].setIcon(R.drawable.ic_lock);
                }
            }
            //add all tabs to tab layout
            for (TabLayout.Tab tab : chapterTabs) {
                tabLayout.addTab(tab);
            }
        });

        //define what happens on tab click
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //define what happens on page change
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        //code for the bottom navigation / main menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.story);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {

                    switch (menuItem.getItemId()) {
                        case R.id.map:
                            startActivity(new Intent(getApplicationContext()
                                    , MapsActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.story:
                            return true;
                        case R.id.info:
                            startActivity(new Intent(getApplicationContext()
                                    , InfoActivity.class));
                            overridePendingTransition(0, 0);
                            return true;

                    }
                    return false;

                }
        );
    }

    //check whether the activity was called with the extra to start the tutorial or the extra to go to a specific chapter and the extra to display chapter unlocked text
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            //go to chapter, if not passed, go to introduction
            int goToChapter = intent.getExtras().getInt("goToChapter", 0);
            pager2.setCurrentItem(goToChapter);
            //check if chapter was just unlocked
            if(intent.getExtras().getBoolean("newlyUnlocked", false)) {
                Snackbar.make(this.findViewById(R.id.coordinator_layout),
                        R.string.chapter_feedback_unlocked,
                        Snackbar.LENGTH_SHORT)
                        .setAction(R.string.chapter_feedback_unlocked_button, snackView -> {
                        })
                        .show();
            }
            //check if tutorial is to be displayed
            if (intent.getExtras().getBoolean("tutorial", false)) {
                tutorialStoryScreen();
            }
        }
    }

    //middle part of the tutorial, about the story screen
    private void tutorialStoryScreen() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.tutorial_story_title)
                .setMessage(R.string.tutorial_story)
                .setNeutralButton(R.string.button_close, (dialog, which) -> {
                    //leave empty, just closes the dialog
                })
                .setNegativeButton(R.string.button_back, (dialog, which) -> {
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("tutorial", true);
                    startActivity(intent);
                })
                .setPositiveButton(R.string.button_next, (dialog, which) -> {
                    Intent intent = new Intent(this, InfoActivity.class);
                    intent.putExtra("tutorial", true);
                    startActivity(intent);
                })
                .show();
    }
}