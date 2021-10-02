import org.gradle.internal.os.OperatingSystem

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "graphics.kiln"
version = "1.2.0-SNAPSHOT"

val lwjglVersion = "3.3.0-SNAPSHOT"
val lwjglNatives = when (OperatingSystem.current()) {
    OperatingSystem.LINUX -> System.getProperty("os.arch").let {
        if (it.startsWith("arm") || it.startsWith("aarch64"))
            "natives-linux-${if (it.contains("64") || it.startsWith("armv8")) "arm64" else "arm32"}"
        else
            "natives-linux"
    }
    OperatingSystem.MAC_OS -> "natives-macos"
    OperatingSystem.WINDOWS -> System.getProperty("os.arch").let {
        if (it.contains("64"))
            "natives-windows${if (it.startsWith("aarch64")) "-arm64" else ""}"
        else
            "natives-windows-x86"
    }
    else -> throw Error("Unrecognized or unsupported Operating system. Please set \"lwjglNatives\" manually")
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

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.1")
}

base {
    archivesName.set("rosella")
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16

    withSourcesJar()
    withJavadocJar()
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(16)
    }

    withType<AbstractArchiveTask> {
        from(file("LICENSE"))
    }

    register<Test>("fastCITest") {
        useJUnitPlatform {
            excludeTags("exclude_frequent_ci", "requires_vulkan")
        }
    }

    register<Test>("slowCITest") {
        useJUnitPlatform {
            // In the future we can add tags to exclude tests that require certain vulkan features which arent available on github
            excludeTags("requires_vulkan")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("Rosella")
                packaging = "jar"

                description.set("A Java Vulkan Rendering Engine")
                url.set("https://github.com/KilnGraphics/Rosella")

                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("hYdos")
                        name.set("Hayden V")
                        email.set("haydenv06@gmail.com")
                        url.set("https://hydos.cf/")
                    }

                    developer {
                        id.set("OroArmor")
                        name.set("Eli Orona")
                        email.set("eliorona@live.com")
                        url.set("https://oroarmor.com/")
                    }

                    developer {
                        id.set("CodingRays")
                        url.set("https://github.com/CodingRays")
                    }

                    developer {
                        id.set("burgerdude")
                        name.set("Ryan G")
                        url.set("https://github.com/burgerguy")
                    }

                    developer {
                        id.set("ramidzkh")
                        email.set("ramidzkh@gmail.com")
                        url.set("https://github.com/ramidzkh")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal()

        maven {
            val releasesRepoUrl = uri("${buildDir}/repos/releases")
            val snapshotsRepoUrl = uri("${buildDir}/repos/snapshots")
            name = "Project"
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }

        maven {
            val releasesRepoUrl = uri("https://maven.hydos.cf/releases")
            val snapshotsRepoUrl = uri("https://maven.hydos.cf/snapshots")
            name = "hydos"
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            val u = System.getenv("MAVEN_USERNAME") ?: return@maven
            val p = System.getenv("MAVEN_PASSWORD") ?: return@maven

            credentials {
                username = u
                password = p
            }
        }
    }
}
