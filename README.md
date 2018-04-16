![Alt text](https://github.com/calemccammon/apkloader/blob/master/Capture.PNG "Screenshot")

<b>Application: APKLoader</b><br/>
Author: Cale McCammon

APKLoader is a Swing GUI for common ADB commands to install APKs to Android phones. 
Commands include: install, upgrade (install -r), and test (install -t). 
The GUI allows the user to select the path to their ADB and APK file and and to connect to a device. 
The paths will be remembered the next time the user launches the application.

Using APKLoader:

1. Select the Browse button by the ADB Path text field. Select your adb.exe file.
2. Select the Browse button by the APK File text field. Select the APK file you want to install.
3. Connect your device by pressing the Connect Device button. You will need to enable developer options on the phone and
trust your computer. Instructions can be found here: https://www.digitaltrends.com/mobile/how-to-get-developer-options-on-android/
4. Select your install type. There are three types:
   -Install New: Performs a new installation.
   -Upgrade/Replace: Upgrades or replaces an existing version of the application with the selected one.
   -Install Test Build: Installs unsigned builds.
5. Select Load APK. Installation time will vary depending on the Android OS version and the size of the APK.

Tools used:

1. Java
2. Swing
