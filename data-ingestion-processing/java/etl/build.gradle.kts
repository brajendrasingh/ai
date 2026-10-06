plugins {
	java
	id("org.springframework.boot") version "4.2.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.bksoft"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(27)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:2.1.0-M1")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//H2 database
	implementation("com.h2database:h2:2.5.252")
	//For h2 db console only
	implementation("org.springframework.boot:spring-boot-h2console")

    // Spring AI - Ollama
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
    // Spring AI - Milvus Vector Store
    implementation("org.springframework.ai:spring-ai-starter-vector-store-milvus")
    // Spring AI - Tika Document Reader
    implementation("org.springframework.ai:spring-ai-tika-document-reader")
    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
