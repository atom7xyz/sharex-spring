plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "xyz.atom7"
version = "0.0.10"

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
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
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

            buildArgs.addAll(
                // [Optimization and Memory Settings] ----------------------------------------
                "-O2",                      // Optimization level GraalVM should compile the image in
                "--gc=G1",                  // Select G1 garbage collector for balance between throughput/pause times
                "-H:+UnlockExperimentalVMOptions",
                "-H:+UseNUMA",              // Optimize memory allocation for Non-Uniform Memory Access architectures
                "-H:+UseDivisor",           // Improve garbage collection interval calculations
                "-R:MaxGCPauseMillis=100",  // Target maximum GC pause time (milliseconds)

                "-H:G1HeapRegionSize=2m",   // Memory region size for G1 collector (smaller regions
                                            // improve allocation precision but increase overhead)


                // [Build Configuration] ----------------------------------------------------
                "--enable-url-protocols=http",              // Enable HTTP URL handling (required for web apps)
                "--no-fallback",                            // Force full native build
                "-H:+ReportExceptionStackTraces",           // Show full stacktraces for build-time initialization errors

                "-H:+ReportUnsupportedElementsAtRuntime",   // Warn about reflection/JNI/resource usages
                                                            // that might fail at runtime

                "-H:+RemoveSaturatedTypeFlows",             // Aggressive optimization to eliminate redundant type checks


                // [Class Initialization] ----------------------------------------------------
                "--initialize-at-build-time=" +                         // Classes to initialize during image build
                        "org.slf4j.LoggerFactory," +                    // Logging framework initialization
                        "ch.qos.logback," +                             // Logback configuration
                        "org.springframework.boot.SpringApplication," + // Spring Boot startup class
                        "sun.security.provider.NativePRNG",             // Native cryptographic random number generator


                // [Native Image Diagnostics] -----------------------------------------------
                "-H:+PrintClassInitialization", // Log class initialization decisions (debugging)
                "-H:+PrintAnalysisCallTree"     // Show full call tree during static analysis
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
