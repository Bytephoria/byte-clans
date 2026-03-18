plugins {
    id("de.eldoria.plugin-yml.bukkit") version "0.8.0"
    id("com.gradleup.shadow") version "9.4.0"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://repo.extendedclip.com/releases/")

    maven {
        name = "alessiodpRepoSnapshots"
        url = uri("https://repo.alessiodp.com/snapshots")
    }

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

    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")

    implementation("com.alessiodp.libby:libby-bukkit:2.0.0-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.2.1")

    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
    implementation("org.incendo:cloud-annotations:2.0.0")

    implementation("com.google.code.gson:gson:2.13.2")

    bukkitLibrary("com.github.ben-manes.caffeine:caffeine:3.2.3")

    //bukkitLibrary("net.kyori:adventure-api:4.25.0")
    bukkitLibrary("net.kyori:adventure-platform-bukkit:4.4.1")

    bukkitLibrary("net.kyori:adventure-text-minimessage:4.25.0")
    bukkitLibrary("net.kyori:adventure-text-serializer-legacy:4.25.0")
    bukkitLibrary("net.kyori:adventure-text-serializer-plain:4.25.0")

    bukkitLibrary("com.zaxxer:HikariCP:7.0.2")
    bukkitLibrary("com.h2database:h2:2.4.240")
    bukkitLibrary("com.mysql:mysql-connector-j:9.6.0")
}

val rootPackage = "${rootProject.group}.${rootProject.name.replace("-", "")}.platform.spigot"

bukkit {
    name = getProjectName(rootProject.name)
    main = "$rootPackage.SpigotPlugin"
    description = rootProject.description
    version = rootProject.version.toString()

    authors = listOf("Bytephoria", "iAmForyy_")
    website = "https://bytephoria.team"

    generateLibrariesJson = true
    foliaSupported = true

    softDepend = listOf("PlaceholderAPI")
}

tasks {
    generateBukkitPluginDescription {
        useGoogleMavenCentralProxy()
    }

    shadowJar {
        exclude(
            "org/h2/**",
            "org/slf4j/**",
            "com/zaxxer/**",
            "com/github/benmanes/caffeine/**",
            "net/kyori/**",
            "com/google/errorprone/**",
            "org/jspecify/annotations/**"
        )

        val librariesPackage = "$rootPackage.libraries"

        relocate("org.bstats", "$librariesPackage.bstats")
        relocate("com.alessiodp.libby", "$librariesPackage.libby")

        relocate("org.incendo.cloud", "$librariesPackage.cloud")
        relocate("com.google.gson", "$librariesPackage.gson")

        relocate("org.spongepowered.configurate", "$librariesPackage.configurate")
        relocate("io.leangen.geantyref", "$librariesPackage.geantyref")

        archiveBaseName.set(getProjectName(rootProject.name))
        archiveVersion.set(rootProject.version.toString())
        archiveClassifier.set(project.name)

        minimize()
    }
}

/**
 * Converts a hyphen-separated project name into PascalCase.
 */
fun getProjectName(baseName: String): String {
    return baseName
        .split("-")
        .joinToString("") { part ->
            part.replaceFirstChar { it.uppercase() }
        }
}