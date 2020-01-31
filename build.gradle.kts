import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    id("org.springframework.boot") version "2.1.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

group = "ru.cleverclover.huncale"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    jcenter()
    mavenCentral()
    gradlePluginPortal()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("ru.clever-clover.meta-calendar:meta-calendar:2.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.1")
    testImplementation("org.junit.platform:junit-platform-engine:1.5.2")
    testImplementation("org.junit.platform:junit-platform-commons:1.5.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
}

group = "ru.clever-clover.calendar"
version = "1.0-SNAPSHOT"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}