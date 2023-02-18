plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.intellij") version "1.12.0"
}

group = "com.github.novotnyr"
version = "6-SNAPSHOT"
val intellijPublishToken: String by project

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

intellij {
    version.set("2021.3")
    type.set("IC")

    plugins.set(listOf("git4idea"))
    updateSinceUntilBuild.set(false)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        changeNotes.set("""
            <ul>
                <li>Bugs and improvements/li>
            </ul>
        """.trimIndent())
    }

    publishPlugin {
        token.set(intellijPublishToken)
    }
}