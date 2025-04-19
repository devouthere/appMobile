import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    jacoco
}

android {

    namespace = "com.example.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true

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
        debug {
            enableUnitTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }

}
tasks.register<JacocoReport>("jacocoTestReportAndroid") {
    dependsOn("connectedDebugAndroidTest")

    group = "Reporting"
    description = "Generate Jacoco coverage reports for androidTest"

    val coverageSourceDirs = listOf(
        "src/main/java"
    )

    val classFiles = fileTree(
        mapOf(
            "dir" to "$buildDir/tmp/kotlin-classes/debug",
            "excludes" to listOf(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*Test*.*",
                "android/**/*.*"
            )
        )
    )

    val executionData = fileTree(buildDir) {
        include(
            "outputs/code_coverage/debugAndroidTest/connected/*.ec"
        )
    }

    sourceDirectories.setFrom(files(coverageSourceDirs))
    classDirectories.setFrom(files(classFiles))

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoTestReportAndroid/html"))
    }
}





dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.espresso.intents)

    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.hamcrest:hamcrest:2.2")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("org.mockito:mockito-core:5.3.1")


    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")


    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")


    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)


    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
}


jacoco{
    toolVersion = "0.8.8"
}




