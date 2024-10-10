Sure! Here's the content formatted in `README.md` style for your GitHub repository:

---

# WaterWise Android App

WaterWise is an Android application designed to help users track their daily water intake, set hydration goals, and monitor their progress.
## Features
- User authentication via Firebase (Sign-up, Login, Logout).
- Track daily water intake and view progress through a pie chart.
- Set and update hydration goals.
- View historical water intake records.
- Navigation bar to easily switch between Home, History, and Settings pages.
- Data stored securely in Firebase Firestore with offline support.

## Prerequisites
Before setting up the project, ensure that you have the following tools installed:
- [Android Studio](https://developer.android.com/studio) (Arctic Fox or higher)
- Java Development Kit (JDK) 8
- Firebase account (to configure Firebase Authentication and Firestore)

## Project Setup

### 1. Clone the Repository
Clone this repository to your local machine:

```bash
git clone https://github.com/tiakavousi/WaterWise.git
cd WaterWise
```


### 2. Open the Project in Android Studio
1. Open Android Studio and click on `File > Open`.
2. Navigate to the project directory and select the folder.
3. Android Studio will sync the project automatically.



### 3. Add Firebase to Your Project

To set up Firebase Authentication and Firestore in your Android app:

1. Register your app with Firebase:
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Click on `Add Project` and create a new project.
   - Follow the steps to register the Android app using the package name `com.example.WaterWise`.
   
2. Download the `google-services.json` file:
   - After registering your app, download the `google-services.json` file.
   - Place the `google-services.json` file in the `app/` directory of your project.

3. Add Firebase SDK dependencies:
   The necessary Firebase SDKs are already added in the `build.gradle` file (app level),
   including dependencies for Firebase Authentication, Firestore, and Realtime Database.

5. Sync the project with Gradle files:
   Once you've added the `google-services.json` file, Android Studio should automatically sync the project.
   If it doesn’t, you can manually sync by selecting `Sync Now` in the top-right corner.
   
#### How to Set Up Firebase Authentication

1. In the Firebase Console, navigate to `Authentication` from the project dashboard.
2. Enable `Email/Password` sign-in method:
   - Click on the `Sign-in method` tab.
   - Enable `Email/Password`.
   
3. Add the Firebase Authentication SDK in your app's `build.gradle`:
   ```gradle
   implementation("com.google.firebase:firebase-auth")
   ``` 


### 4. Build Configuration
Ensure that the project’s Gradle build files are correctly configured. 
Here's a breakdown of the key components of the `build.gradle` file (app level):

- Firebase SDK setup:
  ```gradle
  implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
  implementation("com.google.firebase:firebase-auth")
  implementation("com.google.firebase:firebase-firestore")
  ```

- Java version compatibility:
  ```gradle
  compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
  }
  ```

- Unit testing dependencies:
  ```gradle
  testImplementation("junit:junit:4.13.2")
  
  ```

Sync the project and implement the sign-up, login, and logout logic in your app using Firebase Authentication.



### 5. Run the App

To run the app on an Android emulator or a physical device:

1. In Android Studio, select an available device from the device dropdown menu.
2. Click on the `Run` button (the green play button) to build and launch the app.



### 6. Testing

WaterWise uses JUnit for unit testing.

#### Running Unit Tests
To run the unit tests, follow these steps:

1. Open the `Project` tab in Android Studio.
2. Navigate to `src/test/java` to view the test classes.
3. Right-click on a test class (e.g., `HistoryUtilsTest`) and select `Run`.
4. Android Studio will display the results in the Run window.
