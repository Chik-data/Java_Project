plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    flatDir{
        dirs("lib")
    }
}

dependencies {
    implementation(name = "zen5", group = "")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13

    sourceSets {
        main {
            java {
                setSrcDirs(listOf("src"))
            }

            resources {
                setSrcDirs(listOf("src"))
            }
        }
    }
}

task<JavaExec>("runAppCli") {
    classpath = sourceSets["main"].runtimeClasspath
    main = "fr.umlv.project.hanabi.cli.SimpleGameController"
    standardInput = System.`in`
}

task<JavaExec>("runAppGui") {
    classpath = sourceSets["main"].runtimeClasspath
    main = "fr.umlv.project.hanabi.gui.SimpleGameController"
    standardInput = System.`in`
}
