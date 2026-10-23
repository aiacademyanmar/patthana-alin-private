plugins { id("com.android.application") }
android {
  namespace = "asia.aaii.patthanareader"
  compileSdk = 35
  defaultConfig {
    applicationId = "asia.aaii.patthanareader"
    minSdk = 26
    targetSdk = 35
    versionCode = 41
    versionName = "4.1.0"
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  androidResources { noCompress += listOf("mp3", "webp") }
}