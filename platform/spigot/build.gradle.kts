plugins {
    id("de.eldoria.plugin-yml.bukkit") version ("0.8.0")
    id("com.gradleup.shadow") version ("9.4.0")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
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

    //compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")

    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
    implementation("org.incendo:cloud-annotations:2.0.0")

    implementation("net.kyori:adventure-api:4.25.0")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("net.kyori:adventure-text-minimessage:4.25.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.25.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.25.0")

    implementation("com.h2database:h2:2.4.240")
    implementation("com.mysql:mysql-connector-j:9.6.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }

}

val rootPackage = "${rootProject.group}.${rootProject.name.replace("-", "")}.platform.spigot"

bukkit {
    name = getProjectName(rootProject.name)
    main = "${rootPackage}.SpigotPlugin"
    description = rootProject.description
    version = rootProject.version.toString()

    authors = listOf("Bytephoria", "iAmForyy_")
    website = "https://bytephoria.team"
    generateLibrariesJson = false
    foliaSupported = true

    softDepend = listOf("PlaceholderAPI")

}

tasks {

    generateBukkitPluginDescription {
        useGoogleMavenCentralProxy()
    }

    shadowJar {
        exclude("org/jspecify/annotations/**")

        val librariesPackage = "${rootPackage}.libraries"

        relocate("org.bstats", "${librariesPackage}.bstats")
        relocate("com.github.benmanes.caffeine", "${librariesPackage}.caffeine")
        relocate("org.incendo", "${librariesPackage}.cloud")
        relocate("org.h2", "${librariesPackage}.h2")
        relocate("com.mysql", "${librariesPackage}.mysql")
        relocate("com.google.errorprone", "${librariesPackage}.google.errorprone")
        relocate("net.kyori", "${librariesPackage}.kyori")
        relocate("com.zaxxer.hikari", "${librariesPackage}.hikari")
        relocate("org.slf4j", "${librariesPackage}.slf4j")
        relocate("org.specify.annotations", "${librariesPackage}.annotations")
        relocate("org.spongepowered.configurate", "${librariesPackage}.configurate")
        relocate("io.leangen.geantyref", "${librariesPackage}.geantyref")

        archiveBaseName.set(getProjectName(rootProject.name))
        archiveVersion.set(rootProject.version.toString())
        archiveClassifier.set(project.name)

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