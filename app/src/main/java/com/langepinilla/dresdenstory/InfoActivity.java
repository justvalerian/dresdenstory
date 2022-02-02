package com.langepinilla.dresdenstory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

//The third of our three activities. This activity shows additional information about the app and allows to restart the tutorial.

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //create the button to restart the tutorial
        Button tutorialButton = findViewById(R.id.info_button);
        tutorialButton.setOnClickListener(view -> {
            //on click start an intent with extra tutorialRestart which opens MapsActivity and then reopens the first tutorial window
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("tutorialRestart", true);
            startActivity(intent);
        });

        //make links in TextView for references clickable
        TextView textView = findViewById(R.id.info_about_us_2);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        //code for the bottom navigation / main menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.info);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {

                    switch (menuItem.getItemId()) {
                        case R.id.map:
                            startActivity(new Intent(getApplicationContext()
                                    , MapsActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.story:
                            startActivity(new Intent(getApplicationContext()
                                    , StoryActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.info:
                            return true;

                    }
                    return false;

                }
        );
    }

    //check whether the activity was called with the extra to start the tutorial
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            if (intent.getExtras().getBoolean("tutorial", false)) {
                tutorialInfoScreen();
            }
        }
    }

    //final part of the tutorial, about the info screen
    private void tutorialInfoScreen() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.tutorial_info_title)
                .setMessage(R.string.tutorial_info)
                .setNeutralButton(R.string.button_close, (dialog, which) -> {
                    //leave empty, just closes the dialog
                })
                .setNegativeButton(R.string.button_back, (dialog, which) -> {
                    Intent intent = new Intent(this, StoryActivity.class);
                    intent.putExtra("tutorial", true);
                    startActivity(intent);
                })
                .setPositiveButton(R.string.tutorial_end, (dialog, which) -> {
                    Intent intent = new Intent(this, MapsActivity.class);
                    //without extra here, so that it just starts the maps activity
                    startActivity(intent);
                })
                .show();
    }
}