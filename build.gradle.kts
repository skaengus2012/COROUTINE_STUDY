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

plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("kapt") version "1.5.0"
}

group = "com.nlab.coroutine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    // coroutine
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.0-RC")

    // junit 5
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.1")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.7.1")
    testImplementation("org.junit.jupiter", "junit-jupiter-engine", "5.7.1")
}

tasks {
    test {
        useJUnitPlatform()
    }
}