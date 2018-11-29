# SafetyNet
SafetyNet: Android app for private group crowdfunding. Users can join groups with other users. Within these groups money can be added to the groups pool of money. When needed, users can then withdrawal money from the pool, up to an amount specified by the groups administrator. The identitiy of the person who made the withdrawal will remain anonymous provided they repay the borrowed amount to the group pool within the amount of time specified by the group administrator.


## Building
How to build the application on your own machine.
### Requirements
The app depends on Firebase Firestore NoSQL Database for data storage and Firebase Storage for image storage. It also requires PayPal Payout API keys. It also requires a server to facilitate payments, which can be found in the SafetyNetServer repository of this organization.
Either ask the maintainers for the associated "google-services.json" and PayPal API key files for Firebase & PayPal access or start your own project under Firebase and PayPal.
### Compile
This project was developed using the Android Studio IDE. If you want an IDE, either Android Studio or IntelliJ IDEA IDE will be suitable. If using an IDE,, consult it's docs for compiling Gradle projects.
To build on the command line, from the root directory of the project execute 
`./gradlew clean`
To first clean the project, then generate the debug apk with 
`./gradlew assembleDebug`
To build the apk and immediately deploy it to a connected device, run
`./gradlew installDebug`
To install the apk to a connected device
`adb -d install path/to/your_app.apk`
To run our JUnit 4 tests use 
`./gradlew check`
To run the Android JUnit 4 tests, a device or emulator needs to be connected, then run
`./gradlew connectedCheck`

## Issues and Bugs
If you encounter any issues with the app, please open an issue on this repository. 


