dependencies {
    // domain module 의존성 추가
    implementation(project(":bank-domain"))

    /// spring container 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter")

    // retryable 사용하기 위한 의존성
    implementation("org.springframework.retry:spring-retry")
}