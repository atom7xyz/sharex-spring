plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jetbrains.kotlin.plugin.jpa") version "2.2.20"
    // id("org.graalvm.buildtools.native") version "0.11.0"
}

group = "xyz.atom7"
version = "0.0.17"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
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
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.springframework.security:spring-security-test")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.1")
    implementation("commons-codec:commons-codec:1.19.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Testcontainers
    testImplementation("org.testcontainers:testcontainers:1.21.3")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.isFork = true
    options.isIncremental = true
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// graalvmNative {
//     binaries {
//         named("main") {
//             javaLauncher = javaToolchains.launcherFor {
//                 languageVersion = JavaLanguageVersion.of(21)
//             }
//
//             imageName.set("sharex-spring")
//             mainClass.set("xyz.atom7.sharexspring.SharexSpringApplicationKt")
//
//             buildArgs.addAll(
//                 "-O2",
//                 "--gc=G1",
//                 "-H:+UnlockExperimentalVMOptions",
//                 "-H:+ReportExceptionStackTraces",
//                 "-H:+ReportUnsupportedElementsAtRuntime",
//                 "-H:+RemoveSaturatedTypeFlows",
//                 "-H:+AllowIncompleteClasspath",
//                 "--no-fallback", // Force full native build
//                 "--initialize-at-run-time=org.springframework,com.github.benmanes.caffeine,xyz.atom7.sharexspring",
//                 "--initialize-at-build-time=com.github.benmanes.caffeine.cache.NodeFactory",
//             )
//
//             project.findProperty("org.graalvm.buildtools.native.additionalArgs")
//                 ?.toString()
//                 ?.splitToSequence(',')  // Use comma delimiter for safer argument handling
//                 ?.filter { it.isNotBlank() }
//                 ?.forEach { buildArgs.add(it) }
//         }
//     }
// }