/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    // dagger
    api("com.google.dagger:dagger:2.25.4")
    kapt("com.google.dagger:dagger-compiler:2.25.4")
    kaptTest("com.google.dagger:dagger-compiler:2.25.4")

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