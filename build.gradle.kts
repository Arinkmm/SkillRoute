plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.10.0"
}

group = "com.skillroute"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val openApiSpec = "$projectDir/src/main/resources/api.yaml"
val openApiGeneratedDir: String = layout.buildDirectory.dir("generated").get().asFile.absolutePath

openApiGenerate {
    inputSpec.set(openApiSpec)
    outputDir.set(openApiGeneratedDir)
    generatorName.set("spring")
    modelPackage.set("com.skillroute.api.generated.dto")
    apiPackage.set("com.skillroute.api.generated.api")

    configOptions.set(
        mapOf(
            "useJakartaEe" to "true",
            "useSpringBoot3" to "true",
            "library" to "spring-boot",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "useBeanValidation" to "true",
            "useTags" to "true",
            "dateLibrary" to "java8",
            "openApiNullable" to "false",
            "documentationProvider" to "none",
            "useResponseEntity" to "true"
        )
    )
    additionalProperties.set(
        mapOf(
            "generateApiTests" to "false",
            "generateModelTests" to "false",
            "generateApiDocumentation" to "false",
            "generateModelDocumentation" to "false"
        )
    )
}

sourceSets {
    getByName("main") {
        java {
            srcDir(layout.buildDirectory.dir("generated/src/main/java"))
        }
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}