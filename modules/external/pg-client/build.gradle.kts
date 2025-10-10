tasks.jar {
    enabled = true
}

tasks.bootJar {
    enabled = false
}

dependencies {
    implementation(projects.modules.application)
    implementation(projects.modules.domain)
    implementation(libs.spring.boot.starter.web)

    //테스트 위해 추가
    testImplementation(libs.spring.boot.starter.test) {
        exclude(module = "mockito-core")
    }
}
