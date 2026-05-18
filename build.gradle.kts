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
        languageVersion = JavaLanguageVersion.of(23)
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

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/src/main/resources/static/openapi/skillroute-api.yaml")
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath)
    modelPackage.set("com.skillroute.openapi.model")
    modelNameSuffix.set("Api")
    globalProperties.set(
        mapOf(
            "models" to "",
            "modelDocs" to "false",
            "modelTests" to "false",
            "supportingFiles" to "false"
        )
    )
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "openApiNullable" to "false",
            "annotationLibrary" to "none",
            "documentationProvider" to "none",
            "useBeanValidation" to "true",
            "useJakartaEe" to "true",
            "useSpringBoot3" to "true"
        )
    )
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/openapi/src/main/java"))
        }
    }
}

tasks.named("compileJava") {
    dependsOn(tasks.named("openApiGenerate"))
}

tasks.named("openApiGenerate") {
    doFirst {
        delete(layout.buildDirectory.dir("generated/openapi"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
