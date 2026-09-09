plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.rafiq"
    compileSdk = 35

    val geminiApiKey: String = run {
        val secretsFile: java.io.File = rootProject.file("gradle-secrets.properties")
        var value: String? = null
        if (secretsFile.exists()) {
            value = secretsFile.readLines()
                .map { it.trim() }
                .firstOrNull { it.startsWith("GEMINI_API_KEY=") }
                ?.substringAfter("GEMINI_API_KEY=")
                ?.trim()
        }
        value
            ?: (project.findProperty("GEMINI_API_KEY") as? String)
            ?: "YOUR_API_KEY_HERE"
    }

    val openAiApiKey: String = run {
        val secretsFile: java.io.File = rootProject.file("gradle-secrets.properties")
        var value: String? = null
        if (secretsFile.exists()) {
            value = secretsFile.readLines()
                .map { it.trim() }
                .firstOrNull { it.startsWith("OPENAI_API_KEY=") }
                ?.substringAfter("OPENAI_API_KEY=")
                ?.trim()
        }
        value
            ?: (project.findProperty("OPENAI_API_KEY") as? String)
            ?: ""
    }

    val openAiBaseUrl: String = run {
        val secretsFile: java.io.File = rootProject.file("gradle-secrets.properties")
        var value: String? = null
        if (secretsFile.exists()) {
            value = secretsFile.readLines()
                .map { it.trim() }
                .firstOrNull { it.startsWith("OPENAI_BASE_URL=") }
                ?.substringAfter("OPENAI_BASE_URL=")
                ?.trim()
        }
        value
            ?: (project.findProperty("OPENAI_BASE_URL") as? String)
            ?: "https://api.openai.com/v1/chat/completions"
    }

    val openAiModel: String = run {
        val secretsFile: java.io.File = rootProject.file("gradle-secrets.properties")
        var value: String? = null
        if (secretsFile.exists()) {
            value = secretsFile.readLines()
                .map { it.trim() }
                .firstOrNull { it.startsWith("OPENAI_MODEL=") }
                ?.substringAfter("OPENAI_MODEL=")
                ?.trim()
        }
        value
            ?: (project.findProperty("OPENAI_MODEL") as? String)
            ?: "gpt-3.5-turbo"
    }

    val geminiBaseUrl: String = run {
        val secretsFile: java.io.File = rootProject.file("gradle-secrets.properties")
        var value: String? = null
        if (secretsFile.exists()) {
            value = secretsFile.readLines()
                .map { it.trim() }
                .firstOrNull { it.startsWith("GEMINI_BASE_URL=") }
                ?.substringAfter("GEMINI_BASE_URL=")
                ?.trim()
        }
        value
            ?: (project.findProperty("GEMINI_BASE_URL") as? String)
            ?: "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions"
    }

    val geminiModel: String = run {
        val secretsFile: java.io.File = rootProject.file("gradle-secrets.properties")
        var value: String? = null
        if (secretsFile.exists()) {
            value = secretsFile.readLines()
                .map { it.trim() }
                .firstOrNull { it.startsWith("GEMINI_MODEL=") }
                ?.substringAfter("GEMINI_MODEL=")
                ?.trim()
        }
        value
            ?: (project.findProperty("GEMINI_MODEL") as? String)
            ?: "gemini-2.5-flash"
    }

    defaultConfig {
        applicationId = "com.example.rafiq"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")
        buildConfigField("String", "OPENAI_BASE_URL", "\"$openAiBaseUrl\"")
        buildConfigField("String", "OPENAI_MODEL", "\"$openAiModel\"")
        buildConfigField("String", "GEMINI_BASE_URL", "\"$geminiBaseUrl\"")
        buildConfigField("String", "GEMINI_MODEL", "\"$geminiModel\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
            buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")
            buildConfigField("String", "OPENAI_BASE_URL", "\"$openAiBaseUrl\"")
            buildConfigField("String", "OPENAI_MODEL", "\"$openAiModel\"")
            buildConfigField("String", "GEMINI_BASE_URL", "\"$geminiBaseUrl\"")
            buildConfigField("String", "GEMINI_MODEL", "\"$geminiModel\"")
        }
        debug {
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
            buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")
            buildConfigField("String", "OPENAI_BASE_URL", "\"$openAiBaseUrl\"")
            buildConfigField("String", "OPENAI_MODEL", "\"$openAiModel\"")
            buildConfigField("String", "GEMINI_BASE_URL", "\"$geminiBaseUrl\"")
            buildConfigField("String", "GEMINI_MODEL", "\"$geminiModel\"")
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
        compose = true
        buildConfig = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)

    // MediaPipe Gesture Recognizer
    implementation(libs.mediapipe.tasks.vision)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // osmdroid (OpenStreetMap — no API key required)
    implementation(libs.osmdroid.android)

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
