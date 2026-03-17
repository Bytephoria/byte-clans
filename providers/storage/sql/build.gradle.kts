dependencies {
    implementation(project(":spi"))
    api("com.zaxxer:HikariCP:7.0.2")
}

subprojects {
    dependencies {
        implementation(project(":spi"))
        implementation(project(":providers:storage:sql"))
    }
}