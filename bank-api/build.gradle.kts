/** 사용할 의존성 추가 */
dependencies {
    // bank core 를 사용하기 위한 module 추가
    implementation(project(":bank-core"))
    implementation(project(":bank-event"))
    implementation(project(":bank-domain"))
    implementation(project(":bank-monitoring"))

    // circuit breaker 의존성
    implementation("io.github.resilience4j:resilience4j-spring-boot2:2.0.2")
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.0.2")
    implementation("io.github.resilience4j:resilience4j-retry:2.0.2")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // spring web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // slf4j
    implementation("ch.qos.logback:logback-classic:1.4.14")

    //
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
}