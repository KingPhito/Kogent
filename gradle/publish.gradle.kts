apply(plugin = "maven-publish")

val javadocJar = tasks.getByName("javadocJar")

configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("Kogent")
                description.set("Kogent - Kotlin Multiplatform RAG Pipeline Framework")
                url.set("https://ralphdugue.com")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    url.set("https://github.com/KingPhito/Kogent")
                    connection.set("https://github.com/KingPhito/Kogent.git")
                }
                developers {
                    developer {
                        name.set("Ralph Dugue")
                        email.set("rdugue@ralphdugue.com")
                    }
                }
            }
        }
    }
}

apply(from = file("../gradle/signing.gradle.kts"))
