plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

android {
    namespace = "id.deeromptech.addnewsdata"
    compileSdk = 34

    defaultConfig {
        applicationId = "id.deeromptech.addnewsdata"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))

    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")

    //Firebase Cloud Firestore
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-firestore-ktx")
    //FirebaseUI for Cloud Firestore
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Icon / Image
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
//    implementation("com.github.CanHub:Android-Image-Cropper:4.3.0")
    implementation("com.vanniktech:android-image-cropper:4.3.3")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.android.gms:play-services-base:18.2.0")

    // Progress Button
    implementation("com.github.razir.progressbutton:progressbutton:2.1.0")

    // RxKotlin & Retrofit2
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")
    implementation("com.google.code.gson:gson:2.10.1")

    // Live Data
    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
}