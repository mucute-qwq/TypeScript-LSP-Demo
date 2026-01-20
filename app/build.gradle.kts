plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.mucute.qwq.lsp.demo"

    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = namespace

        minSdk {
            version = release(26)
        }

        targetSdk {
            version = release(28)
        }

        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("shared") {
            storeFile = file("../buildKey.jks")
            storePassword = "123456"
            keyAlias = "TypeScript-LSP-Demo"
            keyPassword = "123456"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs["shared"]
            proguardFiles("proguard-rules.pro")
        }

        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs["shared"]
            proguardFiles("proguard-rules.pro")
        }

    }

    packaging {
        resources {
            excludes += setOf("DebugProbesKt.bin")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

}

kotlin {
    jvmToolchain(21)
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.commons.compress)
    implementation(platform(libs.editor.bom))
    implementation(libs.editor)
    implementation(libs.editor.lsp)
    implementation(libs.oniguruma.native)
    implementation(libs.language.monarch)
    implementation(libs.monarch.code)
    implementation(libs.monarch.language.pack)
    implementation(libs.lsp4j)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}