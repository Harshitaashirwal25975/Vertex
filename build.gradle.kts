import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.1.2"
  id("io.ebean") version "14.1.0"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.5.8"
val junitJupiterVersion = "5.9.1"
val vertexWebVersion = "4.3.8"
val ebeanVersion = "12.12.1"
val queryBeanVersion = "12.12.1"
val mysqlConnectorVersion = "8.0.28"
val hikariCPVersion = "4.0.3"
val vertexauthjwt = "4.3.4"
val vertexauthcommon = "4.3.4"
val ebeanagent = "12.11.1"
val jjwt_Api ="0.11.5"
val jjwt_IMPL = "0.11.5"
val jackson = "0.11.5"
val jbcrypt ="0.4"
val jwtauth="4.3.3"
val mainVerticleName = "com.example.starter.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-core")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")

  implementation("io.vertx:vertx-web:$vertexWebVersion")
  implementation("io.ebean:ebean:$ebeanVersion")
  implementation("io.ebean:ebean-querybean:$queryBeanVersion")
  implementation("mysql:mysql-connector-java:$mysqlConnectorVersion")
  implementation ("com.zaxxer:HikariCP:$hikariCPVersion")
  implementation ("io.vertx:vertx-auth-jwt:$vertxVersion")
  implementation ("org.mindrot:jbcrypt:$jbcrypt")
  implementation ("io.vertx:vertx-auth-jwt:$jwtauth")
  implementation ("io.vertx:vertx-auth-common:$vertexauthcommon")
  runtimeOnly ("io.ebean:ebean-agent:$ebeanagent")
  implementation ("io.jsonwebtoken:jjwt-api:$jjwt_Api")
  runtimeOnly ("io.jsonwebtoken:jjwt-impl:$jjwt_IMPL")
  runtimeOnly ("io.jsonwebtoken:jjwt-jackson:$jackson")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}


tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}



tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}

