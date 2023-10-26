import gg.essential.gradle.util.*

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
//    id("gg.essential.defaults.maven-publish")
}

group = "gg.essential"

java.withSourcesJar()
tasks.compileKotlin.setJvmDefault(if (platform.mcVersion >= 11400) "all" else "all-compatibility")
loom.noServerRunConfigs()

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
}

tasks.jar {
    manifest {
        attributes(mapOf("FMLModType" to "LIBRARY"))
    }
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])

            pluginManager.withPlugin("gg.essential.multi-version") {
                val baseArtifactId = (if (parent == rootProject) rootProject.name.toLowerCase() else null)
                    ?: project.findProperty("baseArtifactId")?.toString()
                    ?: throw GradleException("No default base maven artifact id found. Set `baseArtifactId` in the `gradle.properties` file of the multi-version-root project.")
                artifactId = "$baseArtifactId-$platform"
            }
        }
    }

    repositories {
        maven("https://repo.plasmoverse.com/snapshots") {
            name = "PlasmoVerseSnapshots"

            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
