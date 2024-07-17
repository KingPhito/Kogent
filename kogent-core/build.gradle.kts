import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

group = "com.ralphdugue.kogent"
version = "0.1-PRE-ALPHA"

kotlin {
    jvmToolchain(17)
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        publishLibraryVariants("release")
    }
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                api(project.dependencies.platform(libs.koin.bom))
                api(project.dependencies.platform(libs.koin.annotations.bom))
                api("io.insert-koin:koin-core")
                api("io.insert-koin:koin-core-coroutines")
                api("io.insert-koin:koin-annotations")
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.bundles.ktor)
                implementation(libs.kermit)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.test.junit.bom))
                implementation("org.junit.jupiter:junit-jupiter")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
                implementation(libs.bundles.test)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.hikaricp)
                implementation(libs.h2)
                implementation(libs.postgresql)
                implementation(libs.milvus.sdk.java)
                implementation(libs.slf4j.api)
                implementation(libs.sqlDelight.jvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.test.junit.bom))
                implementation("org.junit.jupiter:junit-jupiter")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
                implementation(libs.bundles.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.hikaricp)
                implementation(libs.h2)
                implementation(libs.postgresql)
                implementation(libs.milvus.sdk.java)
                implementation(libs.slf4j.api)
                implementation(libs.sqlDelight.android)
                implementation("io.insert-koin:koin-android")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(project.dependencies.platform(libs.test.junit.bom))
                implementation("org.junit.jupiter:junit-jupiter")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
                implementation(libs.bundles.test)
                implementation(libs.sqlDelight.jvm)
            }
        }

    }
}

sqldelight {
    databases {
        create("DataSourceRegistryDB") {
            packageName.set("com.ralphdugue.kogent.cache")
        }
    }
}

android {
    namespace = "com.ralphdugue.kogent.core"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }

    // task("testClasses")
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp)
    //add("kspAndroid", libs.koin.ksp)
}

afterEvaluate {  // WORKAROUND: both register() and named() fail – https://github.com/gradle/gradle/issues/9331
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>> {
            if (name != "kspCommonMainKotlinMetadata")
                dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}

// java {
//    withSourcesJar()
// }

apply(from = file("../gradle/publish.gradle.kts"))
