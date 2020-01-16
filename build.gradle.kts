group = "com.nlab.coroutine"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("kapt") version "1.3.61"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // coroutine
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-test", "1.3.3")
    testImplementation("org.jetbrains.kotlinx", "kotlinx-coroutines-test","1.3.3")

    // junit 5
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.5.2")
    testCompile("org.junit.jupiter", "junit-jupiter-params", "5.5.2")
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", "5.5.2")

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

    test {
        useJUnitPlatform()
    }
}