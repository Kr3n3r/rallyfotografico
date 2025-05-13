plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "es.alejandromarmol.rallyfotografico"
    compileSdk = 34

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
}

dependencies {

    implementation("io.swagger:swagger-annotations:1.6.6")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("org.apache.httpcomponents:httpcore:4.4.16")
    implementation ("org.apache.httpcomponents:httpmime:4.5.14")
    implementation ("com.android.volley:volley:1.2.1")
    testImplementation ("junit:junit:4.13.2")

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}