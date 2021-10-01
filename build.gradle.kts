plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"

}

group = "kiln.graphics"
version = "1.1.1"

val lwjglVersion = "3.3.0-SNAPSHOT"
val lwjglNatives = when (org.gradle.internal.os.OperatingSystem.current()) {
    org.gradle.internal.os.OperatingSystem.LINUX -> System.getProperty("os.arch").let {
        if (it.startsWith("arm") || it.startsWith("aarch64")) {
            val arch = if (it.contains("64") || it.startsWith("armv8")) {
                "arm64"
            } else {
                "arm32"
            }

            "natives-linux-$arch"
        } else {
            "natives-linux"
        }
    }
    org.gradle.internal.os.OperatingSystem.MAC_OS -> if (System.getProperty("os.arch")
            .startsWith("aarch64")
    ) "natives-macos-arm64" else "natives-macos"
    org.gradle.internal.os.OperatingSystem.WINDOWS -> "natives-windows"
    else -> error("Unrecognized or unsupported Operating system. Please set \"lwjglNatives\" manually")
}

repositories {
    mavenCentral()

    maven {
        name = "Sonatype Snapshots"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    api(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    api("org.lwjgl", "lwjgl")
    api("org.lwjgl", "lwjgl-assimp")
    api("org.lwjgl", "lwjgl-glfw")
    api("org.lwjgl", "lwjgl-shaderc")
    api("org.lwjgl", "lwjgl-stb")
    api("org.lwjgl", "lwjgl-vma")
    api("org.lwjgl", "lwjgl-vulkan")
    api("org.lwjgl", "lwjgl-xxhash")

    api("org.joml", "joml", "1.10.1")
    api("it.unimi.dsi", "fastutil", "8.2.1")
    api("org.apache.logging.log4j", "log4j-core", "2.14.1")

    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-vma", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-xxhash", classifier = lwjglNatives)

    if (lwjglNatives == "natives-macos" || lwjglNatives == "natives-macos-arm64") {
        runtimeOnly("org.lwjgl", "lwjgl-vulkan", classifier = lwjglNatives)
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
}

tasks.test {
    useJUnitPlatform {
    }
}

tasks.register<Test>("fastCITest") {
    useJUnitPlatform {
        excludeTags("exclude_frequent_ci", "requires_vulkan")
    }
}

tasks.register<Test>("slowCITest") {
    useJUnitPlatform {
        excludeTags("requires_vulkan")
    } // In the future we can add tags to exclude tests that require certain vulkan features which arent available on github
}

var sourcesJar = tasks.create("sourcesJar", Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

var javadocJar = tasks.create("javadocJar", Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"].outputs)
}

publishing {
    publications {
        create("mavenJava", MavenPublication::class) {
            artifacts {
                artifact(tasks["jar"])
                artifact(sourcesJar)
                artifact(javadocJar)
            }

            pom {
                name.set("Rosella")
                packaging = "jar"

                description.set("A Java Vulkan Rendering Engine")
                url.set("https://github.com/Blaze4D-MC/Rosella")

                licenses {
                    license {
                        name.set("Lesser GPL v3.0")
                        url.set("https://mit-license.org/")
                    }
                }

                developers {
                    developer {
                        id.set("hYdos")
                        name.set("Hayden V")
                        email.set("haydenv06@gmail.com")
                    }

                    developer {
                        id.set("OroArmor")
                        name.set("Eli Orona")
                        email.set("eliorona@live.com")
                        url.set("oroarmor.com")
                    }

                    developer {
                        id.set("CodingRays")
                    }

                    developer {
                        id.set("burgerguy")
                    }

                    developer {
                        id.set("ramidzkh")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            setUrl(System.getenv("MAVEN_URL"))
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
            name = "hydosMaven"
            isAllowInsecureProtocol = true
        }
    }
}
