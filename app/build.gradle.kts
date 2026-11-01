plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.prestaaula"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.prestaaula"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- PrestaAula ---
    implementation(libs.androidx.navigation.compose)          // Semana 6: NavHost y NavController
    implementation(libs.androidx.lifecycle.viewmodel.compose) // Semana 7: viewModel() en Compose
    implementation(libs.androidx.room.runtime)                // Semana 8: Room
    implementation(libs.androidx.room.ktx)                    // Room con corrutinas y Flow
    ksp(libs.androidx.room.compiler)                          // genera el código de Room
    implementation(libs.androidx.datastore.preferences)       // Complemento M3: tema guardado con DataStore
    implementation(libs.androidx.compose.material.icons.extended) // íconos (igual que en el Catálogo)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
