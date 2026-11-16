plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.xiaozao"
version = "1.0.9"

kotlin {
    jvmToolchain(17)
}

repositories {
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Monorepo Git Status"
        version = "1.0.9"
        ideaVersion {
            sinceBuild = "241"
            untilBuild = "263.*"
        }
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1")
        bundledPlugin("Git4Idea")
        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
}
