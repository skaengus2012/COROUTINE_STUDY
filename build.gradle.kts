plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("kapt") version "1.3.61"
}

group = "com.nlab.coroutine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // coroutine
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-test", "1.3.3")
    testImplementation("org.jetbrains.kotlinx", "kotlinx-coroutines-test","1.3.3")

    // junit
    testImplementation("junit", "junit", "4.12")

    // mockito
    testImplementation("org.mockito", "mockito-core", "3.2.4")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}