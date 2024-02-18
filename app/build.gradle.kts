plugins {
    id("com.android.application")
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
    implementation ("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
//    implementation ("com.android.support:gridlayout-v7:28.0.0")
//    implementation ("com.android.support:appcompat-v7:28.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}