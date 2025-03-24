plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}



android {
    namespace = "com.kenyaredcross"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kenyaredcross"
        minSdk = 29
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
    buildFeatures{
        viewBinding=true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.gridlayout)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation ("com.tbuonomo:dotsindicator:5.0")

    implementation ("com.itextpdf:itext7-core:7.2.3")


    implementation(libs.glide)
    implementation(libs.circleimageview)
    implementation(libs.google.firebase.database)

    implementation(libs.firebase.ui.database)
    implementation(libs.dialogplus)
    implementation("com.google.android.gms:play-services-appset:16.0.0")

    implementation ("com.squareup.picasso:picasso:2.8")


    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.viewpager)
    implementation(libs.annotation)
    implementation(libs.annotation.jvm)
    implementation(libs.recyclerview)
    implementation(libs.firebase.vertexai)
    implementation(libs.car.ui.lib)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)



    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}

