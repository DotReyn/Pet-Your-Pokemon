plugins {
    kotlin("jvm")
    `maven-publish`
    java
    id("architectury-plugin") version("3.4-SNAPSHOT")
    id("dev.architectury.loom") version("0.12.0-SNAPSHOT")
}

group = property("maven_group")!!
version = property("mod_version")!!

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    mavenCentral()
    maven("https://maven.impactdev.net/repository/development/")
    flatDir { dirs("libs") }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    modImplementation("curse.maven:cobblemon-687131:${property("cobblemon_curse_file_id")}")
    modImplementation("dev.architectury", "architectury-fabric", "6.3.49")
}

tasks {

    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // select the repositories you want to publish to
        repositories {
            // uncomment to publish to the local maven
            // mavenLocal()
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}



// configure the maven publication