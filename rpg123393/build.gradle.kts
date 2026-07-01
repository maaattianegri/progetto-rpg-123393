import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
}

group = "it.unicam.cs.mpgc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"
val javafxVersion = "21.0.6"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Rileva la piattaforma corrente per scaricare i jar JavaFX giusti
val currentOS: OperatingSystem = OperatingSystem.current()
val platform = when {
    currentOS.isWindows -> "win"
    currentOS.isMacOsX  -> "mac"
    else                -> "linux"
}

dependencies {
    // JavaFX — jar nativi per la piattaforma corrente
    implementation("org.openjfx:javafx-base:${javafxVersion}:${platform}")
    implementation("org.openjfx:javafx-graphics:${javafxVersion}:${platform}")
    implementation("org.openjfx:javafx-controls:${javafxVersion}:${platform}")
    implementation("org.openjfx:javafx-fxml:${javafxVersion}:${platform}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

application {
    mainClass.set("it.unicam.cs.mpgc.rpg123393.HelloApplication")

    // Passa i moduli JavaFX alla JVM a runtime
    applicationDefaultJvmArgs = listOf(
        "--module-path", configurations.runtimeClasspath.get().asPath,
        "--add-modules", "javafx.controls,javafx.fxml"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}
