dependencies {
    api(project(":spi"))
    api(project(":providers:storage:sql"))
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.9")
}