plugins {
    java
    id("org.springframework.boot") version "4.2.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.bksoft"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencyManagement {
    imports {
        mavenBom(
            "org.springframework.ai:spring-ai-bom:2.0.1}"
        )
    }
}

dependencies {
    // Spring AI - Ollama
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
    // Spring AI - Milvus Vector Store
    implementation("org.springframework.ai:spring-ai-starter-vector-store-milvus")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
