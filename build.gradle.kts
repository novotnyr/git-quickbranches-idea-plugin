plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
    id("org.jetbrains.intellij") version "1.13.0"
}

group = "com.github.novotnyr"
version = "6"
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
                <li>Require at least IntelliJ 2023.1</li>
                <li>Fix issues with branch checkout</li>
                <li>Improve performance by offloading tasks to background threads</li>
            </ul>
        """.trimIndent())
    }

    publishPlugin {
        token.set(intellijPublishToken)
    }
}