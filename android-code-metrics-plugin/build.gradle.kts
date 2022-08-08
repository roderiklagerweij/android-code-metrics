//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
//    kotlin("jvm") version "1.7.10"
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.15.0"
}

buildscript {
//    extra.apply{
//        set("kotlin_version", "1.7.10")
//        set("gradle_version", "7.2.1")
//    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath ("com.android.tools.build:gradle:7.2.2")
    }
}

//group = "com.github.bendezu"
//version = "0.0.1"

pluginBundle {
    website = "https://http://mobilemetrics.herokuapp.com/"
//    vcsUrl = "https://github.com/bendezu/android-resource-override-finder.git"
    tags = listOf("android")
}

gradlePlugin {
    plugins.create("android-code-metrics") {
        id = "android-code-metrics"
        displayName = "Android Resource Override Finder"
        description = "A plugin that finds resource overrides in android project"
        implementationClass = "AnalysePlugin"
    }
}
group = "com.rl"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}
dependencies {
    implementation ("org.apache.httpcomponents.client5:httpclient5:5.1.3")
    implementation ("com.google.guava:guava:24.1-jre")
    implementation ("com.google.code.gson:gson:2.8.9")

    // Android gradle plugin will allow us to access Android specific features
    implementation ("com.android.tools.build:gradle:7.2.2")
}



//publishing {
//    repositories {
//        mavenLocal()
//    }
//}

// Publish to Maven Local
//   ./gradlew plugin:publishToMavenLocal

// Publish to Gradle Plugin Portal
//   ./gradlew plugin:publishPlugins

//apply plugin: 'java-gradle-plugin' // Allows us to create and configure plugin
//apply plugin: 'kotlin' //We'll write our plugin in Kotlin
//apply plugin: 'maven-publish'
//
//repositories {
//    google()
//    mavenCentral()
//    mavenLocal()
//}
//
//buildscript {
//    ext.kotlin_version = '1.7.10'
//    ext.gradle_version = '7.2.1'
//    repositories {
//        google()
//        mavenCentral()
//        mavenLocal()
//    }
//    dependencies {
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//        classpath "com.android.tools.build:gradle:$gradle_version"
//    }
//}
//
//version = "0.1.0"
//group = "com.autonomousapps"
//
//gradlePlugin {
//    plugins {
//        create("android-code-metrics") {
//            id = "android-code-metrics"
//            implementationClass = "AnalysePlugin"
//        }
//    }
//}
//
//publishing {
//    repositories {
//        mavenLocal()
//    }
//}
//
//dependencies {
//    implementation 'org.apache.httpcomponents.client5:httpclient5:5.1.3'
//    implementation 'com.google.guava:guava:24.1-jre'
//    implementation "com.google.code.gson:gson:2.8.9"
//
//    // Android gradle plugin will allow us to access Android specific features
//    implementation "com.android.tools.build:gradle:$gradle_version"
//}
