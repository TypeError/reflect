import org.zaproxy.gradle.addon.AddOnStatus

plugins {
    kotlin("jvm") version "1.4.30"
    id("org.zaproxy.add-on") version "0.4.0"
}

version = "0.0.10"
description = "Finds reflected parameters"

zapAddOn {
    addOnName.set("Reflect")
    addOnStatus.set(AddOnStatus.ALPHA)
    zapVersion.set("2.9.0")

    manifest {
        author.set("Caleb Kinney")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}