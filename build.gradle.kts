plugins {
    // apply false를 이용해서 사용할 때 적용하겠다고 하는 부분은 일반적으로 다중 모듈 형태로 구성할 때 많이 사용한다.
    id("org.springframework.boot") version "3.3.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    kotlin("jvm") version "2.1.21" apply false

    kotlin("plugin.spring") version "2.0.21" apply false // spring에서 제공하는 프레임워크에서 kotlin 언어에서 인식하고 처리할 수 있도록 해주는 처리해주는 플러그인

    kotlin("plugin.jpa") version "2.0.21" apply false
}

/// 설정된 전체 module이 공통으로 가져갈 부분에 대한 설정 부분
allprojects {
    group = "kr.co.won"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")

    if (name == "bank-api") {
        apply(plugin = "org.springframework.boot")
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
        apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    }

    if (name == "bank-core") {
        apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    }

    if (name == "bank-domain") {
        apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += "-Xjsr305=strict"
        }
    }

}
