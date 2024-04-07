plugins {
    alias(libs.plugins.library.common)
    alias(libs.plugins.library.hilt)
    alias(libs.plugins.library.compose)
}

android {
    namespace = "com.xayah.feature.main.packages"
}

dependencies {
    // Core
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:util"))
    implementation(project(":core:rootservice"))
    implementation(project(":core:hiddenapi"))
    implementation(project(":core:service"))

    // Hilt navigation
    implementation(libs.androidx.hilt.navigation.compose)

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coil
    implementation(libs.coil.compose)

    // dotLottie
    implementation(libs.dotlottie.android)
}
