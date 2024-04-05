import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
}

group = "cz.cuni.gamedev.nail123"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.apache.commons", "commons-csv", "1.10.0")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "21"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "21"
}

tasks.register<JavaExec>("singleRun") {
    group = "run"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("RunGameKt")
}

tasks.register<JavaExec>("generateCSV") {
    group = "run"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("GatherMetricsKt")
}
