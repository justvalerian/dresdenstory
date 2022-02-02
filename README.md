# DresdenStory

## About

<img alt="DresdenStory launcher icon" align="right" src="https://user-images.githubusercontent.com/46158468/152214954-5f9fb7ce-b909-4521-ac51-cf80a8756b5e.png" />

DresdenStory is an app to get to know or rediscover the most exciting places in Dresden in a different and more interactive way compared to regular city guides. To reach this aim we utilized storytelling (the different points are chapters in a connected narrative), images and challenges (questions that need to be answered after each chapter) to let users enjoy the experience of getting to know or rediscovering Dresden.

This project was created by Ana Pinilla and Valerian Lange as part of the [Cartography M.Sc. programme](https://cartographymaster.eu/).

The app was written in Java only (no Kotlin), as this was a course requirement and helps to make the code approachable for beginners.
The app was written for Android 12 (API level 31).

## How to work with this project

### 1. Build the app in Android Studio and run it on a virtual device

1. Clone the repository or download the zipped project and unzip it.
2. Then open the folder in Android Studio.
3. For the app to work properly you will need to add your own Google Maps API key. Find instructions and then paste your key [here](https://github.com/justvalerian/dresdenstory/blob/main/app/src/debug/res/values/google_maps_api.xml).
4. To run the app you will then need to [set up a virtual device](https://developer.android.com/studio/run/managing-avds) or connect a phone using Android API level 31.

### 2. Look at the code

In [app/src/main](https://github.com/justvalerian/dresdenstory/tree/main/app/src/main), you can find the main parts of the code of the app. If you are unfamiliar with Android projects, try reading an introduction to the Android project folder structure first.

## Functions

The user interface of the app is structured into three main activities:

### Map activity

See the location where each chapter takes place. Get directions to reach each point.
  
![annotated map activity](https://user-images.githubusercontent.com/46158468/152212114-d1488e41-f99c-4578-ae8b-0c9035e8964d.PNG)

### Story activity

Find historical references told through storytelling, images and challenges.

![annotated story activity screenshot](https://user-images.githubusercontent.com/46158468/152212115-e6c0d7ea-efce-4961-a966-acfad19ec503.PNG)

![annotated story activity interaction screenshot](https://user-images.githubusercontent.com/46158468/152212116-2c17ad36-cd1e-4702-b3e8-cb0d4ea98246.PNG)

### Info activity

Find additional information about the app.

![annotated info activity screenshot](https://user-images.githubusercontent.com/46158468/152212110-b493ed29-a8b6-43ec-8403-164a3bb42253.PNG)
