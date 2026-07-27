dependencies {
    implementation(project(":spi"))
    api("com.zaxxer:HikariCP:7.1.0")
}

subprojects {
    dependencies {
        implementation(project(":spi"))
        implementation(project(":providers:storage:sql"))
    }
}