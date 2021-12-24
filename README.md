## About APOD

APOD is a viewer app for the Astronomy Photo Of the Day service from NASA. It allows you to view the latest APOD, as well as backtrack to previous APODs, to as far as the first APOD from 16 June, 1995. You can also view details about each picture. For more information about APOD, go here: https://apod.nasa.gov/apod/lib/about_apod.html

This is a really simple app, and is licensed under GNU GPL v3, a copy of which is provided in this archive. If not, see: https://www.gnu.org/licenses/

## Compilation Guide
For successful compilation, follow these steps:
1. Clone or download this git repository (https://github.com/dd1929/apod-viewer.git)
2. Open it with Android Studio. The latest version has been compiled with Android Studio 4.1.3, so I do not guarantee successful compilation with other versions of Android Studio, especially older ones.
3. Allow Android Studio to complete gradle syncing so that you have all the libraries required.
4. Navigate to app/src/main/java/org/dd1929/apod/JSONFetcher.java and in API_KEY, replace "yourAPIKeyHere" with your own APOD API key, which you can generate here (an email ID is necessary): https://api.nasa.gov/
5. Proceed to compile:
    * If you have an emulator or physical device connected, go to Run > Run 'app', or use the Run button on the toolbar at the top.
    * To create an APK, go to Build > Generate Signed Bundle / APK. Use your own keystore, and make sure to check both *V1 (JAR signing)* and *V2 (Full APK signing)*. Then you can install the APK on a device.
  
## Feedback

If you find any bugs, create an issue here: https://github.com/dd1929/apod-viewer/issues if you have a Github account. Else, mail me here: dd1929@protonmail.com

Feedback will be appreciated, whether good or bad. For suggestions, feature requests, bug reports, etc. mail me at dd1929@protonmail.com.
