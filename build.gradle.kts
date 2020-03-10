import org.zaproxy.gradle.addon.AddOnStatus

plugins {
    kotlin("jvm") version "1.3.61"
    id("org.zaproxy.add-on") version "0.3.0"
}

version = "0.0.3"
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