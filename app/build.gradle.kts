plugins {
	id("com.android.application")
	id("vip.mystery0.autoversion")
	id("kotlin-android")
	id("kotlin-android-extensions")
	id("kotlin-kapt")
}
autoVersion {
	major = 2
	minor = 2
	patch = 8
	beta = 0
	alpha = 0
}

android {
	compileSdkVersion(28)
	dataBinding {
		isEnabled = true
	}
	defaultConfig {
		applicationId = "com.weilylab.xhuschedule"
		minSdkVersion(21)
		targetSdkVersion(28)
		versionCode = autoVersion.code
		versionName = autoVersion.name
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables.useSupportLibrary = true
		setProperty("archivesBaseName", "XhuSchedule-${autoVersion.name}-${autoVersion.code}-${autoVersion.branch}")
		ndk {
			moduleName = "bspatch"
			// 设置支持的SO库架构
//			abiFilters "armeabi" , "armeabi-v7a", "arm64-v8a"//, "x86_64", "x86"
		}
		// Enabling multi dex support.
		multiDexEnabled = true

		//指定room.schemaLocation生成的文件路径
		javaCompileOptions {
			annotationProcessorOptions {
				argument("room.schemaLocation", "$projectDir/schemas")
			}
		}
	}
	dependencies {
		implementation("com.android.support:multidex:1.0.3")
	}
	buildTypes {
		getByName("debug") {
			resValue("string", "app_version_name", autoVersion.name)
			resValue("string", "app_version_code", defaultConfig.versionCode.toString())
			resValue("string", "app_package_name", defaultConfig.applicationId)
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
		}
		getByName("release") {
			resValue("string", "app_version_name", autoVersion.name)
			resValue("string", "app_version_code", defaultConfig.versionCode.toString())
			resValue("string", "app_package_name", defaultConfig.applicationId)
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
		}
	}
	kapt {
		generateStubs = true
	}
	externalNativeBuild {
		cmake {
			setPath("src/main/jni/CMakeLists.txt")
		}
	}
}

dependencies {
	implementation(kotlin("stdlib-jdk7", "1.3.11"))
	implementation("com.google.android.material:material:1.0.0")
	implementation("androidx.appcompat:appcompat:1.0.2")
	implementation("androidx.cardview:cardview:1.0.0")
	implementation("androidx.legacy:legacy-support-v4:1.0.0")
	implementation("androidx.vectordrawable:vectordrawable:1.0.1")
	implementation("androidx.preference:preference:1.0.0")
	implementation("androidx.recyclerview:recyclerview:1.0.0")
	implementation("androidx.constraintlayout:constraintlayout:1.1.3")
	implementation("androidx.annotation:annotation:1.0.1")
	implementation("androidx.slice:slice-builders:1.0.0")

	//Retrofit
	implementation("com.squareup.retrofit2:retrofit:2.5.0")
	implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")
	implementation("com.squareup.retrofit2:converter-gson:2.5.0")
	implementation("com.squareup.okhttp3:logging-interceptor:3.12.0")

	//Mystery0Tools
	implementation("com.github.Mystery0Tools:Logs:1.3.0")
	implementation("com.github.Mystery0Tools:Tools:1.6.5")
	implementation("com.github.Mystery0Tools:CrashHandler:1.0.2")
	implementation("com.github.Mystery0Tools:BottomTabView:1.0.3")
	implementation("com.github.Mystery0Tools:RxPackageData:1.0.2")

	//Glide
	implementation("com.github.bumptech.glide:glide:4.8.0")
	annotationProcessor("com.github.bumptech.glide:compiler:4.8.0")

	//RxAndroid
	implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
	implementation("io.reactivex.rxjava2:rxjava:2.2.4")

	//ZLoading
	implementation("com.zyao89:zloading:1.2.0")

	//Lifecycle
	implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
	kapt("androidx.lifecycle:lifecycle-compiler:2.0.0")

	//Room
	implementation("androidx.room:room-runtime:2.0.0")
	kapt("androidx.room:room-compiler:2.0.0")

	//Condom
	implementation("com.oasisfeng.condom:library:2.2.0")

	//MultiDex
	implementation("androidx.multidex:multidex:2.0.1")

	//TimetableView
	implementation("com.zhuangfei:TimetableView:2.0.6")

	//Matisse
	implementation("com.zhihu.android:matisse:0.5.2-beta3")

	//UCrop
	implementation("com.github.yalantis:ucrop:2.2.2")

	//SetupWizardLayout
	implementation("com.github.iMeiji:SetupWizardLayout:0.1")

	//About Library
	implementation("com.mikepenz:aboutlibraries:6.2.0")

	//Color Picker
	implementation("com.jrummyapps:colorpicker:2.1.6")

	//CosmoCalendar
	implementation("com.github.applikeysolutions:cosmocalendar:1.0.4")

	//CircleImageView
	implementation("de.hdodenhof:circleimageview:2.2.0")

	//Bugly
	implementation("com.tencent.bugly:crashreport:2.8.6")

	//微信
	implementation("com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.1.4")

	//微博
	implementation("com.sina.weibo.sdk:core:4.3.0:openDefaultRelease@aar")

	//qq
	implementation(files("libs/tencent_sdk.jar"))
}
repositories {
	mavenCentral()
}
