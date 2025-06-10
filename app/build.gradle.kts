import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.colearnhub"
    compileSdk = 35

    val properties = Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { load(it) }
        }
    }

    val key: String = properties.getProperty("supabasekey") ?: ""
    val url: String = properties.getProperty("supabaseurl") ?: ""

    defaultConfig {
        applicationId = "com.example.colearnhub"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "supabasekey", "\"$key\"")
        buildConfigField("String", "supabaseurl", "\"$url\"")
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
    buildFeatures{
            compose = true
            viewBinding = true
            buildConfig = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material Design 3 com Compose
    implementation("androidx.compose.material3:material3")

    // Suporte a previews no Android Studio
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("io.github.jan-tennert.supabase:storage-kt:1.4.7")

    // Testes de UI com Compose
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Ícones
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Navegação com Compose
    implementation("androidx.navigation:navigation-compose:2.8.9")

    // Imagens com Coil e Compose
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ViewModel com Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")

    implementation("io.ktor:ktor-client-android:3.1.3")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.lottie.compose)
}