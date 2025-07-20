dependencies {
    // domain module 의존성 추가
    implementation(project(":bank-domain"))
    implementation(project(":bank-monitoring"))
    implementation(project(":bank-core"))

    /// spring container 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")


    // retryable 사용하기 위한 의존성
    implementation("org.springframework.retry:spring-retry")
}