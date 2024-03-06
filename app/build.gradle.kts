plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.nebula.NebulaApp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nebula.NebulaApp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.prolificinteractive:material-calendarview:1.4.3")
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")
    implementation ("com.applandeo:material-calendar-view:1.9.0")
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.1.0")
//    implementation ("com.firebase:geofire-java:3.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")
    implementation ("com.google.firebase:firebase-storage")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.0.3")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.1.0")

    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation ("com.googlecode.libphonenumber:libphonenumber:8.12.29")
    implementation ("androidx.biometric:biometric:1.1.0")

    implementation ("androidx.core:core:1.6.0")
    implementation ("com.github.prolificinteractive.geocoder:geocoder:0.1.0")
    implementation ("com.github.prolificinteractive.geocoder:geocoder-googlemaps:0.1.0")
    implementation ("com.github.prolificinteractive.geocoder:geocoder-okhttp:0.1.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}