buildscript {
	var kotlinVersion: String by extra
	kotlinVersion = "1.3.11"
	repositories {
		google()
		jcenter()
		maven("https://jitpack.io")
	}
	dependencies {
		classpath("com.android.tools.build:gradle:3.2.1")
		classpath(kotlin("gradle-plugin", kotlinVersion))
		classpath("com.github.Mystery0Tools:AutoVersion:1.0.4")
	}
}

allprojects {
	repositories {
		google()
		jcenter()
		maven("https://jitpack.io")
		maven("https://dl.bintray.com/thelasterstar/maven/")
	}
}

task("clean", Delete::class) {
	delete = setOf(rootProject.buildDir)
}
