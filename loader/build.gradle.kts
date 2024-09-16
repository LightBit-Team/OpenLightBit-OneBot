import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Scala application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.7-rc-3/userguide/building_java_projects.html in the Gradle documentation.
 */
plugins {
    // Apply the scala Plugin to add support for Scala.
    scala

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.graalvm.buildtools.native") version "0.10.2"
    id("xyz.wagyourtail.jvmdowngrader") version "1.1.3"
}

project.version = "0.3.0"
val prettyName = "QingZhu"
val javaVersion = JavaVersion.VERSION_17
jvmdg.downgradeTo = JavaVersion.VERSION_1_8

repositories {
    // Use Maven Central for resolving dependencies.
    //mavenCentral()
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
}

dependencies {
    implementation("org.scala-lang:scala3-library_3:3.6.0-RC1-bin-20240822-d490d13-NIGHTLY")
    implementation("cn.hutool:hutool-bom:5.8.31") {
        exclude(group = "cn.hutool", module = "hutool-log")
        exclude(group = "cn.hutool", module = "hutool-socket")
        exclude(group = "cn.hutool", module = "hutool-poi")
        exclude(group = "cn.hutool", module = "hutool-jwt")
        exclude(group = "cn.hutool", module = "hutool-script")
        exclude(group = "cn.hutool", module = "hutool-aop")
        exclude(group = "cn.hutool", module = "hutool-db")
        exclude(group = "cn.hutool", module = "hutool-bloomFilter")
        exclude(group = "cn.hutool", module = "hutool-dfa")
        exclude(group = "cn.hutool", module = "hutool-crypto")
        exclude(group = "cn.hutool", module = "hutool-cache")
        exclude(group = "cn.hutool", module = "hutool-json")
        exclude(group = "cn.hutool", module = "hutool-cron")
        exclude(group = "cn.hutool", module = "hutool-extra")
    }
    implementation("org.java-websocket:Java-WebSocket:1.5.7")
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")
    implementation("com.github.oshi:oshi-core-java11:6.6.3")
    compileOnly("xyz.wagyourtail.jvmdowngrader:jvmdowngrader:1.1.3")
    runtimeOnly("xyz.wagyourtail.jvmdowngrader:jvmdowngrader-java-api:1.1.3:downgraded-8")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

application {
    // Define the main class for the application.
    mainClass = "am9.olbcore.onebot.Main"
}

tasks.register<WriteProperties>("generateFile") {
    outputFile = file("$buildDir/resources/main/META-INF/b-info")
    property("version", project.version)
    property("pretty-name", prettyName)
    property("build-time", System.currentTimeMillis())
}

tasks.register<Copy>("copyFile") {
    mustRunAfter("generateFile")
    dependsOn("processResources")
    from("$buildDir/resources/main/META-INF")
    into("$buildDir/classes/java/main/META-INF")
}

tasks.named<ScalaCompile>("compileScala") {
    finalizedBy("generateFile")
    finalizedBy("copyFile")
    sourceCompatibility = javaVersion.toString()
    targetCompatibility = javaVersion.toString()
}

tasks.named<Jar>("jar") {
    from("$buildDir/classes/java/main/META-INF")
}

tasks.named<ShadowJar>("shadowJar") {

    dependsOn("copyFile")
}

project.extra["scalaMajorVersion"] = "3"
project.extra["scalaVersion"] = "${project.extra["scalaMajorVersion"]}.6.0-RC1-bin-20240822-d490d13-NIGHTLY-nonbootstrapped-git-d490d13"

