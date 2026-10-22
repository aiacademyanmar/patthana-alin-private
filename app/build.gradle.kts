plugins { id("com.android.application") }
android {
  namespace = "asia.aaii.patthana"
  compileSdk = 35
  defaultConfig {
    applicationId = "asia.aaii.patthana"
    minSdk = 26
    targetSdk = 35
    versionCode = 23
    versionName = "2.3.0-public-preview"
  }
  buildTypes { release { isMinifyEnabled = false } }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}
