plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "es.alejandromarmol.rallyfotografico"
    compileSdk = 35

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }

    defaultConfig {
        applicationId = "es.alejandromarmol.rallyfotografico"
        minSdk = 27
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
    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("io.swagger:swagger-annotations:1.6.6")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("org.apache.httpcomponents:httpcore:4.4.16")
    implementation ("org.apache.httpcomponents:httpmime:4.5.14")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.squareup.retrofit2:retrofit:3.0.0")
    implementation ("com.github.GrenderG:Toasty:1.5.2")

    implementation("io.swagger:swagger-annotations:1.6.6")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("org.apache.httpcomponents:httpcore:4.4.16")
    implementation ("org.apache.httpcomponents:httpmime:4.5.14")
    implementation ("com.android.volley:volley:1.2.1")
    testImplementation ("junit:junit:4.13.2")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}