plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "xyz.atom7"
version = "0.0.5-SNAPSHOT"

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
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.flywaydb:flyway-core")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
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
            
            verbose.set(false)
            buildArgs.addAll(
                "-O2",
                "-H:+ReportExceptionStackTraces",
                "-R:MaxGCPauseMillis=100",
                "--gc=G1",
                "-H:G1HeapRegionSize=2m",
                "--initialize-at-build-time=org.slf4j.LoggerFactory,ch.qos.logback",
                "-H:+RemoveSaturatedTypeFlows",
                "--no-fallback",
                "-march=native"
            )

            if (project.hasProperty("org.graalvm.buildtools.native.additionalArgs")) {
                buildArgs.addAll(project.property("org.graalvm.buildtools.native.additionalArgs").toString().split(" "))
            }
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
