plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.apollographql.apollo3").version("3.1.0")
    id("com.mikepenz.aboutlibraries.plugin").version("10.5.2")
}

fun loadConfig(): HashMap<String, String> {
    val configs: HashMap<String, String> = HashMap()
    configs["GITHUB_CLIENT_ID"] = "0033b88e2d83bece185f"
    configs["GITHUB_SECRET"] = "c9840402bc9234f55ce2e5796e2d345cecddd5d3"
    configs["IMGUR_CLIENT_ID"] = "5fced7f255e1dc9"
    configs["IMGUR_SECRET"] = "03025033403196a4b68b48f0738e67ef136ad64f"
    configs["KEY_ALIAS"] = "alias"
    configs["PASSWORD"] = "xrj45yWGLbsO7W0v"
    try {
        val inputFile = rootProject.file("${rootProject.projectDir}\\app\\secrets.properties")
        println("Secrets found!\nLoading OctoHub credentials...")
        inputFile.forEachLine {
            val data = it.split("=")
            configs[data[0]] = data[1]
        }
    } catch (e: Exception) {
        println("Secrets not found!\nUsing demo credentials...")
    }
    return configs
}

val config = loadConfig()

android {
    namespace = "com.fastaccess"
    compileSdk = 32
    buildToolsVersion = "33.0.1"
    defaultConfig {
        applicationId = "com.fastaccess.github.octohub"
        minSdk = 26
        targetSdk = 32
        versionCode = 480
        versionName = "4.8.0"
        buildConfigField("String", "GITHUB_APP_ID", "\"com.fastaccess.github.octohub\"")
        buildConfigField("String", "GITHUB_CLIENT_ID", "\"${config["GITHUB_CLIENT_ID"]}\"")
        buildConfigField("String", "GITHUB_SECRET", "\"${config["GITHUB_SECRET"]}\"")
        buildConfigField("String", "IMGUR_CLIENT_ID", "\"${config["IMGUR_CLIENT_ID"]}\"")
        buildConfigField("String", "IMGUR_SECRET", "\"${config["IMGUR_SECRET"]}\"")
        buildConfigField("String", "REST_URL", "\"https://api.github.com/\"")
        buildConfigField("String", "IMGUR_URL", "\"https://api.imgur.com/3/\"")
        buildConfigField("String", "GITHUB_STATUS_URL", "\"https://www.githubstatus.com/\"")
        buildConfigField("String", "GITHUB_STATUS_COMPONENTS_PATH", "\"api/v2/components.json\"")
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file("${rootProject.projectDir}\\app\\keys_debug.jks")
        }
        create("release") {
            storeFile = file("${rootProject.projectDir}\\app\\keys_release.jks")
            storePassword = config["PASSWORD"]
            keyPassword = config["PASSWORD"]
            keyAlias = config["KEY_ALIAS"]
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    lint {
        htmlReport = true
        xmlReport = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        getByName("main") {
            res.srcDirs(
                "src/main/res/",
                "src/main/res/layouts/main_layouts",
                "src/main/res/layouts/row_layouts",
                "src/main/res/layouts/other_layouts",
                "src/main/res/translations"
            )
        }
    }
}

//kapt {
//    keepJavacAnnotationProcessors = true
//}

apollo {
    packageName.set("com.fastaccess.github")
//    generateKotlinModels.set(false)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // androidx
    implementation("androidx.core:core:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.fragment:fragment-ktx:1.5.7")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.legacy:legacy-preference-v14:1.0.0")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // thirtyinch
    implementation("com.github.Grandcentrix.ThirtyInch:thirtyinch:v1.0.1")
    implementation("com.github.Grandcentrix.ThirtyInch:thirtyinch-rx2:v1.0.1")
    implementation("com.github.Grandcentrix.ThirtyInch:thirtyinch-kotlin:v1.0.1")
    implementation("com.github.Grandcentrix.ThirtyInch:thirtyinch-kotlin-coroutines:v1.0.1")

    // retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // ShapedImageView
    implementation("cn.gavinliu:ShapedImageView:0.8.7")
//    implementation("io.woong.shapedimageview:shapedimageview:1.4.3")

    // bottom-navigation
    implementation("it.sephiroth.android.library.bottomnavigation:bottom-navigation:3.0.0")

    // rx2
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // autodispose
//    implementation("com.uber.autodispose2:autodispose:2.1.1")
//    implementation("com.uber.autodispose2:autodispose-lifecycle:2.1.1")
//    implementation("com.uber.autodispose2:autodispose-android:2.1.1")
//    implementation("com.uber.autodispose2:autodispose-androidx-lifecycle:2.1.1")

    // okhttp3
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // stream
    implementation("com.annimon:stream:1.2.2")

    // Toasty
    implementation("com.github.GrenderG:Toasty:1.5.2")

    // RetainedDateTimePickers
    implementation("com.github.k0shk0sh:RetainedDateTimePickers:1.0.2")

    // material-about-library
    implementation("com.github.daniel-stoneuk:material-about-library:2.1.0")

    // requery
//    implementation("io.requery:requery:1.6.0")
//    implementation("io.requery:requery-android:1.6.0")
//    kapt("io.requery:requery-processor:1.6.0")

    // about lib
    implementation("com.mikepenz:aboutlibraries-core:10.5.2")
    implementation("com.mikepenz:aboutlibraries:10.0.1")

    // HtmlSpanner
    implementation("com.github.NightWhistler:HtmlSpanner:0.4")
    // htmlcleaner !! 2.2> cause htmlparser to not work properly
    implementation("net.sourceforge.htmlcleaner:htmlcleaner:2.28")


    // commonmark
    implementation("com.atlassian.commonmark:commonmark:0.17.0")
    implementation("com.atlassian.commonmark:commonmark-ext-autolink:0.17.0")
    implementation("com.atlassian.commonmark:commonmark-ext-gfm-strikethrough:0.17.0")
    implementation("com.atlassian.commonmark:commonmark-ext-gfm-tables:0.17.0")
    implementation("com.atlassian.commonmark:commonmark-ext-ins:0.17.0")
    implementation("com.atlassian.commonmark:commonmark-ext-yaml-front-matter:0.17.0")

    // kotlin std
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")

    // jsoup
    implementation("org.jsoup:jsoup:1.16.1")

    // state
    implementation("com.evernote:android-state:1.4.1")
    kapt("com.evernote:android-state-processor:1.4.1")

    // color picker
    implementation("com.github.kristiyanP:colorpicker:v1.1.10")

    // apollo3
    implementation("com.apollographql.apollo3:apollo-runtime:3.1.0")
    implementation("com.apollographql.apollo3:apollo-rx2-support:3.1.0")

    // device name
    implementation("com.jaredrummler:android-device-names:2.1.1")

    // keyboard
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:2.3.0")

    // lottie
    implementation("com.airbnb.android:lottie:6.0.0")

    // mmkv
    implementation("com.tencent:mmkv:1.2.16")

    // androidx javax annotation
    implementation("org.glassfish:javax.annotation:10.0-b28")
    implementation("androidx.annotation:annotation:1.5.0")


    // shortbread
    implementation("com.github.matthiasrobbers:shortbread:1.4.0")
//    kapt("com.github.matthiasrobbers:shortbread-compiler:1.4.0")

    // objectbox
    implementation("io.objectbox:objectbox-kotlin:3.6.0")
    implementation("io.objectbox:objectbox-rxjava:3.6.0")
//    debugImplementation("io.objectbox:objectbox-android-objectbrowser:3.1.2")
    implementation("io.objectbox:objectbox-android:3.6.0")


    // cache
//    implementation("com.github.ben-manes.caffeine:caffeine:3.0.6")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.assertj:assertj-core:3.24.2")
    androidTestImplementation("org.mockito:mockito-core:5.3.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 泄漏检测
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.8.1")
}

apply(plugin = "io.objectbox")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
