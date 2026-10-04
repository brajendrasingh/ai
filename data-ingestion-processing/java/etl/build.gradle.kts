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
	implementation("org.springframework.ai:spring-ai-starter-vector-store-milvus")
	implementation("org.springframework.ai:spring-ai-starter-model-ollama")

	implementation("org.apache.tika:tika-core:3.2.2")
	implementation("org.apache.tika:tika-parsers-standard-package:3.2.2")

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
