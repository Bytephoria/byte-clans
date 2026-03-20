import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
    id("com.gradleup.shadow") version("9.3.0")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    api(project(":api"))
    api(project(":core"))

    api(project(":infrastructure:adventure"))
    api(project(":infrastructure:configuration"))
    api(project(":infrastructure:bootstrap"))

    api(project(":providers:storage:sql"))
    api(project(":providers:storage:sql:h2"))
    api(project(":providers:storage:sql:mysql"))

    api(project(":platform:bukkit-api"))
    api(project(":platform:common-bukkit"))

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")
    implementation("org.bstats:bstats-bukkit:3.2.1")

    paperLibrary("com.github.ben-manes.caffeine:caffeine:3.2.3")
    paperLibrary("com.zaxxer:HikariCP:7.0.2")
    paperLibrary("org.incendo:cloud-paper:2.0.0-beta.10")
    paperLibrary("org.incendo:cloud-annotations:2.0.0")
    paperLibrary("com.h2database:h2:2.4.240")

}

paper {
    name = getProjectName(rootProject.name)
    main = "${rootProject.group}.${rootProject.name.replace("-", "")}.platform.paper.PaperPlugin"
    description = rootProject.description
    version = rootProject.version.toString()
    loader = "${rootProject.group}.${rootProject.name.replace("-", "")}.platform.paper.PaperPluginLoader"
    apiVersion = "1.19"

    authors = listOf("Bytephoria", "iAmForyy_")
    website = "https://bytephoria.team"
    generateLibrariesJson = true
    foliaSupported = true

    serverDependencies {
        register("PlaceholderAPI") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

}

tasks {
    generatePaperPluginDescription {
        useGoogleMavenCentralProxy()
    }

    shadowJar {
        exclude(
            "org/h2/**",
            "org/slf4j/**",
            "org/spongepowered/**",
            "com/zaxxer/**",
            "com/github/benmanes/caffeine/**",
            "net/kyori/**",
            "com/google/errorprone/**",
            "io/leangen/geantyref/**",
            "org/jspecify/annotations/**"
        )

        relocate("org.bstats", "${rootProject.group}.${rootProject.name.replace("-", "")}.platform.paper.bstats")

        archiveBaseName.set("${getProjectName(rootProject.name)}-${project.name}")
        archiveVersion.set(rootProject.version.toString())
        archiveClassifier.set("")

    }

}

/**
 * Converts a hyphen-separated project name into PascalCase.
 */
fun getProjectName(baseName: String): String {
    return baseName.split("-")
        .joinToString("") {
                part -> part.replaceFirstChar {
            it.uppercase()
        }
        }
}