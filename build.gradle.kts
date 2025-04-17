import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	java
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "gov.epa"
version = "1.0"

java { sourceCompatibility = JavaVersion.VERSION_21 }

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
		resolutionStrategy.failOnVersionConflict()
	}
	implementation {
		resolutionStrategy.failOnVersionConflict()
	}
}

repositories {
	mavenLocal()
	maven {
		url = uri("https://uat.ccdsupport.com/archiva/repository/internal")
		metadataSources {
			mavenPom()
			artifact()
		}
	}
	mavenCentral()
}
// configurations.all {
//     resolutionStrategy {
//         // Force specific version of the dependency
//         force("com.sun.xml.messaging.saaj:saaj-impl:1.5.3")
//     }
// }
dependencies {
	annotationProcessor("org.projectlombok:lombok")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	/* the below package includes all packages needed for security 
	   so we don't need implementation for spring-boot-starter-security separately
	 */
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-web")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.apache.poi:poi:5.2.0")
	implementation("org.apache.poi:poi-ooxml:5.2.0")
	implementation ("org.apache.commons:commons-lang3")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	/* Begin - Packages to include for authenticationService */
	// implementation("gov.epa:ghg-cdx:23.0"){
    //     exclude(group = "com.sun.xml.ws", module = "jaxws-rt")
	// }
	// // Need below implementations to avoid version conflicts with packages from Java 21
	// implementation("javax.xml.ws:jaxws-api:2.3.1")
	// implementation("com.sun.xml.ws:jaxws-rt:2.3.6"){
	// 	 // Exclude below and get packages with specific version to server ghg-cdx
    //     exclude(group = "com.sun.xml.messaging.saaj", module = "saaj-impl")
	// 	exclude(group = "com.sun.xml.bind", module="jaxb-impl")
	// }
	// implementation("com.sun.xml.messaging.saaj:saaj-impl:1.5.3")
	// implementation("com.sun.xml.bind:jaxb-impl:2.3.8")
	/* End - Packages to include for authenticationService */
	/* Begin - Packages for json */
	implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
	implementation("org.json:json:20230227")
	/* End - Packages for json */
	/* Begin - Packages for AWS */
	implementation(platform("software.amazon.awssdk:bom:2.29.18"))
	implementation("software.amazon.awssdk:s3")
	implementation("software.amazon.awssdk:athena")
	implementation("software.amazon.awssdk:sso")
	/* End - Packages for AWS */
	/* Begin - Packages for object-to-object mapping to simplify converting DTOs to models */
	implementation("org.modelmapper:modelmapper:2.3.8")
	/* End */
}

tasks {
	withType<BootBuildImage> {
		bindings = listOf("${projectDir}/bindings/ca-certificates:/platform/bindings/ca-certificates")
		environment = (mapOf(
			"BP_EMBED_CERTS" to "true"
		))
	}
	withType<Test> {
		useJUnitPlatform()
	}
}

description = "invdb-ms"
