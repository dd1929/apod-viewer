# About APOD Viewer

APOD Viewer is a viewer app for the Astronomy Photo Of the Day service from NASA. For more information about APOD, go here: https://apod.nasa.gov/apod/lib/about_apod.html

This app is licensed under GNU GPL v3, a copy of which is provided in this repository. If not, see: https://www.gnu.org/licenses/

## Features

* __Basic functionality__ - View APODs within the app, irrespective of whether they are photos or videos; read the informative explanations about the APODs, as provided by NASA
* __Save to device__ - Download *image APODs* to device so that they are viewable in the device gallery
* __Set as wallpaper__ - Set *image APODs* as wallpaper on the device home screen, lock screen or both
* __Share APODs__ - Share APODs directly from APOD Viewer to other apps, including or excluding APOD title, date, description and other details
* __View in browser__ - Open the selected APOD in the official NASA APOD webpage in the browser
* __Bookmarks__ - Bookmark APODs you like so that you can find them easily later
* __Notifications__ - Receive daily notifications as new APODs become available
* __Automatic wallpaper__ - Have the latest APOD set automatically as device wallpaper (not enabled by default, can be enabled in settings)
* ...and more.

## Compilation Guide
For successful compilation, follow these steps:

### With Android Studio
1. Clone or download this git repository (https://github.com/dd1929/apod-viewer.git)
2. Open it in Android Studio.
3. Allow Android Studio to complete gradle syncing so that you have all the libraries required.
4. Proceed to compile:
    * If you have an emulator or physical device connected, go to Run > Run 'app', or use the Run button on the toolbar at the top.
    * To create an APK, go to Build > Generate Signed Bundle / APK. Use your own keystore, and make sure to check both *V1 (JAR signing)* and *V2 (Full APK signing)*. Then you can install the APK on a device.

### In the Terminal
1. Make sure Git, Java, Gradle and the Android SDK are properly installed.
2. Clone this git repository with ```git clone https://github.com/dd1929/apod-viewer.git```.
3. Navigate into the root of the repository with ```cd apod-viewer```.
4. Proceed to compile:
    * If you have an emulator or physical device connected, create and install a debug APK with ```gradle installDebug```.
    * To simply create a debug APK, simply run ```gradle assembleDebug```. The APK can be found in *apod-viewer\app\build\outputs\apk\debug*. Then you can install the APK on a device.
  
## Feedback

If you find any bugs, create an issue here: https://github.com/dd1929/apod-viewer/issues if you have a Github account. Else, mail me here: dd1929@protonmail.com

Feedback will be appreciated, whether good or bad. For suggestions, feature requests, bug reports, etc. mail me at dd1929@protonmail.com.
