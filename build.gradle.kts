plugins {
    java
    `maven-publish`
}

allprojects {
    apply {
        plugin("java-library")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:26.0.2")
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

subprojects {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
    }
}