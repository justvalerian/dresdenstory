package com.langepinilla.dresdenstory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

//The first of our three activities and the one that is shown on app startup, shows the tutorial on first start and in general shows the map with markers for each chapter.

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleMap.OnPolygonClickListener {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //call method to check if app is on first start
        checkFirstRun();

        //initiate ViewModel, which holds the connection to the repository and therefore to the database
        ChapterViewModel mChapterViewModel = new ViewModelProvider(this).get(ChapterViewModel.class);

        //observe getAllUnlockedChapters which only returns the unlocked chapters aka the ones we want to display in the map
        mChapterViewModel.getAllUnlockedChapters(true).observe(this, unlockedChapters -> {
            //check that there are actually some unlocked chapters and a map to display them on
            if (mMap != null && unlockedChapters != null && unlockedChapters.size() > 0) {
                //initialize an array of markers to fit the number of unlocked chapters
                Marker[] markers = new Marker[unlockedChapters.size()];
                //create the actual markers and pass the content for each from the database
                for (int i = 0; i < unlockedChapters.size(); i++) {
                    markers[i] = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(unlockedChapters.get(i).getLocationLat(), unlockedChapters.get(i).getLocationLon()))
                            .title(getString(R.string.chapter_title) + unlockedChapters.get(i).getNumber())
                            .snippet(unlockedChapters.get(i).getShortDescription())
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker_satin", 96, 120)))
                            .zIndex(i)
                    );
                    if (markers[i] != null) {
                        //after marker creation, set an object holding the chapter number and location text for each as the marker tag
                        markers[i].setTag(new MarkerTag(unlockedChapters.get(i).getNumber(), unlockedChapters.get(i).getLocationText()));
                    }
                }
                //change title for introduction, make latest unlocked chapter (will be last in array) different color
                if (markers[0] != null) {
                    markers[0].setTitle(getString(R.string.chapter_title_introduction));
                }
                //set a different icon for the latest chapter so that it's more prominent on the map
                markers[markers.length - 1].setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker_yellow", 112, 140)));
            }
        });

        //initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //code for the bottom navigation / main menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.map);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {

                    switch (menuItem.getItemId()) {
                        case R.id.map:
                            return true;
                        case R.id.story:
                            startActivity(new Intent(getApplicationContext()
                                    , StoryActivity.class));
                            overridePendingTransition(0, 0);
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

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        //initialize map
        mMap = map;

        //set custom map style
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        //check if permission to access user location was given
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1340);
        }

        //set camera position
        CameraPosition cam_pos =
                new CameraPosition.Builder()
                        .target(new LatLng(51.05105, 13.73908)).zoom(16)
                        .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam_pos));

        mMap.clear();
        mMap.setInfoWindowAdapter(new MapMarkerInfoWindowAdapter(MapsActivity.this));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //set padding so that map controls are placed above the bottom navigation menu of the app
        mMap.setPadding(0, 0, 0, 250);

        //enable zooming and user location button
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //add polygon showing the area in which the story takes place, set it to be clickable and set the listener
        Polygon walkingArea = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(51.05339, 13.73819), new LatLng(51.05322, 13.73696), new LatLng(51.05053, 13.73593), new LatLng(51.04918, 13.7364), new LatLng(51.04902, 13.73812), new LatLng(51.04875, 13.74004), new LatLng(51.05197, 13.74239), new LatLng(51.05238, 13.74222), new LatLng(51.05292, 13.74103))
                .strokeColor(ContextCompat.getColor(this, R.color.satin))
        );
        walkingArea.setClickable(true);
        mMap.setOnPolygonClickListener(this);

        //set info window click listener
        mMap.setOnInfoWindowClickListener(this);
    }

    //as we want to transport more than one variable in the marker tag, we create a class so we can create an object to pass as the tag for a marker
    public static class MarkerTag {

        private final int chapterNumber;
        private final String chapterLocationText;

        public MarkerTag(int chapterNumber, String chapterLocationText) {
            this.chapterNumber = chapterNumber;
            this.chapterLocationText = chapterLocationText;
        }

        public int getChapterNumber() {
            return chapterNumber;
        }

        public String getChapterLocationText() {
            return chapterLocationText;
        }
    }

    //handle user location permission
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(this,
                    R.string.map_location_permission, Toast.LENGTH_LONG).show();

        }
    }

    //when info window is clicked set intent extra to the respective chapter and then start the StoryActivity
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

        Intent intent = new Intent(this, StoryActivity.class);

        if (marker.getTag() instanceof MarkerTag) {
            int chapterNumber = ((MarkerTag) marker.getTag()).getChapterNumber();
            intent.putExtra("goToChapter", chapterNumber);
        }

        startActivity(intent);
    }

    //on click on the walkable area show an informative snackbar
    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        Snackbar.make(findViewById(R.id.coordinator_layout),
                R.string.map_walking_area,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.button_got_it, view -> {
                })
                .show();
    }

    //function to resize map marker icons from https://stackoverflow.com/questions/14851641/change-marker-size-in-google-maps-api-v2
    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    //check whether the activity was called with the extra to go back to last visible tutorial dialog or the extra to restart the tutorial from the beginning
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            if (intent.getExtras().getBoolean("tutorial", false)) {
                tutorialMapScreen();
            } else if (intent.getExtras().getBoolean("tutorialRestart", false)) {
                firstRunInfo();
            }
        }
    }

    //function to enable tutorial only on first run, adapted following https://stackoverflow.com/questions/7217578/check-if-application-is-on-its-first-run#7217834
    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run, just return out of the function
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            //this will be executed after a new install
            firstRunInfo();

        } else if (currentVersionCode > savedVersionCode) {
            //this will be executed after an upgrade, add here in case of a version update
            firstRunInfo();
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    //The following functions are to display the info dialog on the first run and the consecutive tutorial dialogs
    private void firstRunInfo() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.tutorial_welcome_title)
                .setMessage(R.string.tutorial_welcome)
                .setNeutralButton(R.string.button_got_it, (dialog, which) -> {
                    //leave empty, just closes the dialog
                })
                .setPositiveButton(R.string.button_explain, (dialog, which) -> tutorialBottomMenu())
                .show();
    }

    private void tutorialBottomMenu() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.tutorial_menu_title)
                .setMessage(R.string.tutorial_menu)
                .setNeutralButton(R.string.button_close, (dialog, which) -> {
                    //leave empty, just closes the dialog
                })
                .setNegativeButton(R.string.button_back, (dialog, which) -> firstRunInfo())
                .setPositiveButton(R.string.button_next, (dialog, which) -> tutorialMapScreen())
                .show();
    }

    private void tutorialMapScreen() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.tutorial_map_title)
                .setMessage(R.string.tutorial_map)
                .setNeutralButton(R.string.button_close, (dialog, which) -> {
                    //leave empty, just closes the dialog
                })
                .setNegativeButton(R.string.button_back, (dialog, which) -> tutorialBottomMenu())
                .setPositiveButton(R.string.button_next, (dialog, which) -> {
                    Intent intent = new Intent(this, StoryActivity.class);
                    intent.putExtra("tutorial", true);
                    startActivity(intent);
                })
                .show();
    }
}