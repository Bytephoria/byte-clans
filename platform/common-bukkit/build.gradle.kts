repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.groupez.dev/releases")
}

dependencies {
    api(project(":api"))
    api(project(":core"))
    api(project(":platform:bukkit-api"))
    api(project(":infrastructure:configuration"))
    api(project(":infrastructure:bootstrap"))

    api(project(":providers:storage:sql"))
    api(project(":providers:storage:sql:h2"))
    api(project(":providers:storage:sql:mysql"))
    api(project(":providers:storage:sql:mariadb"))

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("fr.maxlego08.menu:zmenu-api:1.1.1.4")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.zaxxer:HikariCP:7.0.2")

    compileOnly("org.incendo:cloud-paper:2.0.0-beta.10")
    compileOnly("org.incendo:cloud-annotations:2.0.0")

}