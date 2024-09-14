plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.WaterWise"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.WaterWise"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.android.material:material:1.12.0")
    implementation (libs.mpandroidchart)
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))// BOM for Firebase
    implementation("com.google.firebase:firebase-database")
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)  // Firebase Realtime Database
    implementation("com.github.TutorialsAndroid:GButton:v1.0.19")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}