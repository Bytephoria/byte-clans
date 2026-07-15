repositories {
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    api(project(":api"))
    api("com.github.Bytephoria.data-container:bukkit-serializers:v1.2.0")

    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")

    // Dependencies
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.25.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.25.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.25.0")

    //

    compileOnly("org.incendo:cloud-paper:2.0.0-beta.10")
    compileOnly("org.incendo:cloud-annotations:2.0.0")

}