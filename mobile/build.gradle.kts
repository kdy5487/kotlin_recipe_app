import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")  // 추가한 줄
    id("com.google.gms.google-services")  // Google Services 플러그인 추가
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.myapplication"  // 기본 네임스페이스로 사용
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.myapplication"  // 기본 applicationId 사용
        minSdk = 28
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
        viewBinding = true  // View Binding 활성화
    }
}




dependencies {
    // 공통 라이브러리
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.3.3")
    implementation("org.jsoup:jsoup:1.15.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")


    // 프로젝트 모듈
    implementation(project(":shared"))
    implementation(project(":mylibrary"))

    // Firebase 라이브러리
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation ("com.google.firebase:firebase-storage:20.2.0")
    implementation ("com.google.mlkit:barcode-scanning:17.0.2")
    implementation ("com.google.firebase:firebase-database:20.2.2")
    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.1.0")
    implementation("com.google.firebase:firebase-messaging:23.2.0")

    // 추가 라이브러리
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // 테스트 라이브러리
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 추가 의존성
    releaseImplementation(libs.my.library)
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation(libs.androidx.benchmark.macro)
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")



    // Firebase Authentication 라이브러리
    implementation("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.0.0")

    // Google Sign-In 라이브러리
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

}

