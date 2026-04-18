plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.ralphdugue.kogent"
version = "unspecified"

dependencies {
    implementation(projects.kogentCore)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
