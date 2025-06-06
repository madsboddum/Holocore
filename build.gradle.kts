import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/

plugins {
	application
	idea
	java
	kotlin("jvm") version "2.1.21"
	id("org.beryx.jlink") version "3.1.1"
}

val javaVersion = JavaVersion.current()
val kotlinTargetJdk = JvmTarget.fromTarget(javaVersion.majorVersion)
val junit5Version = "5.12.2"
val holocoreLogLevel: String? by project

subprojects {
	ext {
		set("junit5Version", junit5Version)
	}
}

repositories {
	maven("https://dev.joshlarson.me/maven2")
	mavenCentral()
}

application {
	mainClass.set("com.projectswg.holocore.ProjectSWG")
	mainModule.set("holocore")
}

sourceSets {
	main {
		java {
			output.setResourcesDir(destinationDirectory.get())
		}
	}
	create("utility")
}

tasks.named("processResources").configure { dependsOn("compileJava") }

val utilityImplementation by configurations.getting {
	extendsFrom(configurations.implementation.get())
}

dependencies {
	implementation(project(":pswgcommon"))
	implementation(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	implementation(group="org.jetbrains.kotlinx", name="kotlinx-coroutines-core", version="1.10.2")
	implementation(group="org.mongodb", name="mongodb-driver-sync", version="5.5.0")
	implementation(group="me.joshlarson", name="fast-json", version="3.0.1")
	implementation(group="me.joshlarson", name="jlcommon-network", version="1.1.0")
	implementation(group="me.joshlarson", name="jlcommon-argparse", version="0.9.6")
	implementation(group="me.joshlarson", name="websocket", version="0.9.4")
	val slf4jVersion = "1.7.36"
	runtimeOnly(group="org.slf4j", name="slf4j-jdk14", version=slf4jVersion)

	utilityImplementation(project(":"))
	utilityImplementation(project(":pswgcommon"))

	testImplementation(group="org.junit.jupiter", name="junit-jupiter-api", version=junit5Version)
	testRuntimeOnly(group="org.junit.jupiter", name="junit-jupiter-engine", version=junit5Version)
	testRuntimeOnly(group="org.junit.platform", name="junit-platform-launcher", version="1.12.2")
	testImplementation(group="org.junit.jupiter", name="junit-jupiter-params", version=junit5Version)
	testImplementation(group="org.testcontainers", name="mongodb", version="1.21.0")

	testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}

idea {
    module {
        inheritOutputDirs = true
		excludeDirs.add(project.file("log"))
		excludeDirs.add(project.file("mongo_data"))
		excludeDirs.add(project.file("odb"))
    }
}

jlink {
//	addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
	addOptions("--ignore-signing-information")
	forceMerge("kotlin-stdlib")
	imageDir.set(layout.buildDirectory.dir("holocore"))
	imageZip.set(layout.buildDirectory.file("holocore.zip"))
	launcher {
		name = "holocore"
		jvmArgs = listOf()
		unixScriptTemplate = file("src/main/resources/jlink-unix-launch-template.txt")
	}
}

tasks.withType<Jar> {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).configureEach {
	compilerOptions {
		jvmTarget.set(kotlinTargetJdk)
	}
	destinationDirectory.set(File(destinationDirectory.get().asFile.path.replace("kotlin", "java")))
}

tasks.create<JavaExec>("runDevelopment") {
	dependsOn(tasks.getByName("test"))

	enableAssertions = true
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("com.projectswg.holocore.ProjectSWG")

	if (holocoreLogLevel != null)
		args = listOf("--log-level", holocoreLogLevel!!)
}

tasks.create<JavaExec>("runProduction") {
	classpath = sourceSets.main.get().runtimeClasspath
	mainClass.set("com.projectswg.holocore.ProjectSWG")
	
	if (holocoreLogLevel != null)
		args = listOf("--log-level", holocoreLogLevel!!)
}

tasks.replace("run", JavaExec::class).apply {
	dependsOn(tasks.getByName("runDevelopment"))
}

tasks.create<JavaExec>("runClientdataConversion") {
	enableAssertions = true
	classpath = sourceSets["utility"].runtimeClasspath
	mainClass.set("com.projectswg.utility.ClientdataConvertAll")
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	testLogging {
		events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
		exceptionFormat = TestExceptionFormat.FULL
	}
}

tasks.named("classes") {
	dependsOn("createRunScript")
}

tasks.register("createRunScript") {
	dependsOn("compileJava", "compileKotlin", "processResources")

	doLast {
		// Use the Java executable that Gradle is using
		val javaHome = System.getProperty("java.home")
		val javaExecutable = "$javaHome/bin/java"

		// Collect runtime classpath elements into a single string with path separator
		val runtimeClasspath = configurations["runtimeClasspath"].files.joinToString(File.pathSeparator) {
			it.absolutePath
		}

		// Get the output directory for the main/java source set
		val mainJavaOutputDir = project.sourceSets["main"].java.destinationDirectory.get().asFile.absolutePath

		// Assemble the module-path to include both the runtime classpath and the main/java output directory
		val modulePath = "$runtimeClasspath${File.pathSeparator}$mainJavaOutputDir"

		// Assemble the command
		val command = "clear; JAVA_HOME=$javaHome ./gradlew classes && $javaExecutable -Xms1G -Xmx2G -XX:+UseZGC -XX:+ZGenerational -ea -p $modulePath -m holocore/com.projectswg.holocore.ProjectSWG --print-colors"

		// File to write the run command
		val outputFile = file("${layout.buildDirectory.asFile.get().absolutePath}/run")
		outputFile.writeText(command)
		outputFile.setExecutable(true)
	}
}
