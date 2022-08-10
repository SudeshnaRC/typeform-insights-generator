import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("org.jetbrains.kotlin.kapt") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.21"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.6.21"
    id("groovy")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.4.1"
}

version = "0.1"
group = "org.addvert.marketresearch"

val kotlinVersion = project.properties["kotlinVersion"]
val micronautVersion = project.properties["micronautVersion"]
val log4jVersion = project.properties["log4jVersion"]
val log4jKotlinVersion = project.properties["log4jKotlinVersion"]


repositories {
    mavenCentral()
}

fun getCommandOutput(vararg cmd: String): String {
    ByteArrayOutputStream().use { outputStream ->
        project.exec {
            commandLine(*cmd)
            standardOutput = outputStream
        }
        return outputStream.toString().trim()
    }
}

dependencies {
    implementation("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut:micronaut-validation")
    kapt("io.micronaut:micronaut-inject-java")
    implementation("io.micronaut:micronaut-runtime")
    implementation("com.beust:klaxon:5.6")
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.neo4j:micronaut-neo4j-bolt")
    implementation("io.micronaut.sql:micronaut-hibernate-jpa")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")

    // Log4j
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:$log4jKotlinVersion")
    kapt("org.apache.logging.log4j:log4j-core:$log4jVersion")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:spock")
    testImplementation("org.testcontainers:testcontainers")
    implementation("io.micronaut:micronaut-validation")
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
    testImplementation("org.codehaus.groovy:groovy-all:3.0.11")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    testRuntimeOnly("org.neo4j.test:neo4j-harness")

}


application {
    mainClass.set("org.addvert.marketresearch.typeforminsightsgenerator.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

micronaut {
    runtime("netty")
    testRuntime("spock2")
    processing {
        incremental(true)
        annotations("org.addvert.marketresearch.typeforminsightsgenerator.*")
    }
}