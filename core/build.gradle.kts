repositories {
    maven("https://jitpack.io")
}

dependencies {
    api(project(":api"))
    api(project(":spi"))

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    implementation("com.github.bytephoria.data-container:api:v1.0.0")
    implementation("com.github.bytephoria.data-container:binary:v1.0.0")

}