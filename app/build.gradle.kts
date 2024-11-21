plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.uphill"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.uphill"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)

    // web
    implementation(libs.okhttp3.okhttp)

    implementation("org.hildan.krossbow:krossbow-stomp-core:7.0.0")
    implementation("org.hildan.krossbow:krossbow-websocket-builtin:7.0.0")
    implementation("org.hildan.krossbow:krossbow-websocket-okhttp:7.0.0")


    // activity detection
    implementation(project(":sdk"))
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("jp.wasabeef:glide-transformations:4.3.0")



    // background processing
    implementation(libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)
    implementation(libs.play.services.basement)
    implementation(libs.androidx.activity)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // firebase auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)

    // camera
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.extensions)

    // qrcode
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.core)


    // background work
    implementation ("androidx.work:work-runtime-ktx:2.7.1")

}