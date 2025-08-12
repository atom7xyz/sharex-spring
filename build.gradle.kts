plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.2.20-Beta2"
}

group = "xyz.atom7"
version = "0.0.12"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(
            findProperty("java.version")?.toString()?.toInt() ?: 21
        )
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.1")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("sharex-spring")
            mainClass.set("xyz.atom7.sharexspring.SharexSpringApplicationKt")

            buildArgs.addAll(
                "-O2",
                "--gc=G1",
                "-H:+UnlockExperimentalVMOptions",
                "-H:+ReportExceptionStackTraces",
                "-H:+ReportUnsupportedElementsAtRuntime",
                "-H:+RemoveSaturatedTypeFlows",
                "-H:+PrintClassInitialization",
                "-H:+PrintAnalysisCallTree",
                "--enable-url-protocols=http",
                "--no-fallback", // Force full native build
                "--initialize-at-run-time=org.springframework"
            )

            // Handle additional arguments from properties more safely
            project.findProperty("org.graalvm.buildtools.native.additionalArgs")
                ?.toString()
                ?.splitToSequence(',')  // Use comma delimiter for safer argument handling
                ?.filter { it.isNotBlank() }
                ?.forEach { buildArgs.add(it) }
        }
    }
}

tasks.withType<JavaCompile> {
    options.isFork = true
    options.isIncremental = true
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
