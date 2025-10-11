tasks.jar {
    enabled = true
}

tasks.bootJar {
    enabled = false
}

dependencies {
    implementation(projects.modules.domain)
    // Only need Spring annotations (@Service) for this module
    implementation("org.springframework:spring-context")
    // logging
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
}
