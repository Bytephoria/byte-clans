repositories {
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":api"))

    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
}