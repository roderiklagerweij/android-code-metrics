rootProject.name = "PluginSample"
include(":app")
include(":mylibrary")
//include(":plugin") // Uncomment to publish plugin
includeBuild("android-code-metrics-plugin") // Uncomment to use local plugin

pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}