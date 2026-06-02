plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "2.2.0"
    id("com.google.gms.google-services")


}

android {
    namespace = "com.example.FixNow"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.FixNow"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime)
    implementation(libs.core.ktx)
    implementation(libs.work.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)

    implementation("androidx.work:work-runtime:2.9.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.0")

    // --- RETROFIT (Para conectar con tu API PHP) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
// --- GSON (Para convertir JSON a Java) ---
    implementation("com.google.code.gson:gson:2.10.1")


    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.mapbox.maps:android:11.2.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // Librería específica para Notificaciones Push (FCM)
    implementation("com.google.firebase:firebase-messaging")


}