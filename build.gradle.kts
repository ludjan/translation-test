import java.io.File

plugins {
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "no.ludjan"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.crac:crac:1.5.0") // annotation magic screams without this

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


fun recreateFile(fileName: String) = File(fileName).apply {
		delete()
		parentFile.mkdirs()
	}

val fileToTranslate: String = "src/main/resources/generated/strings_to_translate.properties"

fun String.nestedStrings(): List<String> {

	var temp = this
		.dropWhile { it != '"' }
		.dropLastWhile { it != '"' }

	val nestedStrings = mutableListOf<String>()

	while (temp.isNotEmpty()) {
		temp = temp.substringAfter("\"")
		val nestedString = temp.substringBefore("\"")
		nestedStrings.add(nestedString)
		println("----- Added nested string $nestedString")
		temp = temp.substringAfter("\"")
	}
	return nestedStrings
}

// Ensure this task runs before the build process
tasks.named("compileKotlin") {
	dependsOn("extractTranslations")
}

tasks.register("mockTranslate") {
	dependsOn("extractTranslations")

	println("Mocking translation")
	val lines = File("src/main/resources/generated/strings_to_translate.properties").readLines()

	val outputFile = File("src/main/resources/translated/no.properties")
	outputFile.delete()
	outputFile.parentFile.mkdirs()

	var i = 0
	while(i < lines.size) {
		val startI = i
		val wordLines = mutableListOf<String>()
		while(i < lines.size && lines[i].isNotBlank()) {
			wordLines.add(lines[i])
			i++
		}
		val key = lines[startI].split("=").last().trim()
		wordLines.add("translation=$key-translated\n\n")
		i++

		outputFile.appendText("${wordLines.joinToString("\n")}")

	}
}

tasks.register("extractTranslations") {

	println("- Extracting translations")
	val outputFile = recreateFile(fileToTranslate)

	val kotlinFiles = fileTree("src/main/kotlin") { include("**/*.kt") }
	println("-- Found ${kotlinFiles.files.size} files")

	kotlinFiles.forEach { file ->
		val lines = file.readLines()
		lines.forEachIndexed { index, line ->
			if (line.contains("@ExtractForTranslation")) {

				println("--- Found @ExtractForTranslation annotation in file ${file.name} on line ${index + 1}")
				val annotationLines = mutableListOf<String>()
				var i = index
				while (i < lines.size && !lines[i].contains("val")) {
					annotationLines.add(lines[i].trim())
					i++
				}
				val annotationLinesString = annotationLines.joinToString("")
				println("---- $annotationLinesString")

				val keys = listOf("key", "defaultValue","description", "sourceLanguage")
				val values: List<String> = annotationLinesString.nestedStrings()

				val valueMap = keys.zip(values)

				val lineList = valueMap.map { (k,v) -> "$k=$v" }

				outputFile.appendText(lineList.joinToString("\n") + "\n\n")
			}
		}
	}
}
