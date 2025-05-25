import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
     kotlin("android") version "2.1.10"
     kotlin("plugin.serialization") version "2.1.10"
}

configurations.all {
    resolutionStrategy {
        // Use consistent Supabase versions from BOM
        eachDependency {
            if (requested.group == "io.github.jan-tennert.supabase") {
                useVersion("3.1.4")
            }
        }
        // Force specific versions to avoid conflicts
        force("io.github.jan-tennert.supabase:storage-kt:3.1.4")
        force("io.github.jan-tennert.supabase:postgrest-kt:3.1.4")
        force("io.github.jan-tennert.supabase:realtime-kt:3.1.4")
    }
}

android {
    namespace = "com.coffeecorner.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.coffeecorner.app"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Read from local.properties file
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        
        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties.getProperty("SUPABASE_ANON_KEY", "")}\"")
        
        multiDexEnabled = true
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            // Handle duplicate Supabase classes
            pickFirsts += "**/UploadData.class"
            pickFirsts += "**/storage/*.class"
            pickFirsts += "**/io/github/jan/supabase/storage/*.class"
        }
        jniLibs {
            pickFirsts += "**/libc++_shared.so"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1") 
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.navigation.runtime.android)
    implementation(libs.car.ui.lib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // MultiDex support
    implementation("androidx.multidex:multidex:2.0.1")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
     // Lottie Animation
    implementation("com.airbnb.android:lottie:6.2.0")
    
    // CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // Supabase - Temporarily disabled due to dependency conflicts
    // TODO: Re-enable when server integration is ready
    // implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    // implementation("io.github.jan-tennert.supabase:postgrest-kt:3.1.4")
    // implementation("io.github.jan-tennert.supabase:storage-kt:3.1.4")
    // implementation("io.github.jan-tennert.supabase:realtime-kt:3.1.4")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    
    // Ktor (required for Supabase) - Temporarily disabled due to dependency conflicts
    // implementation("io.ktor:ktor-client-android:2.3.11")
    // implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
    // implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    
    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.9.0")
    implementation("androidx.navigation:navigation-ui:2.9.0")

    // Retrofit for network requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}