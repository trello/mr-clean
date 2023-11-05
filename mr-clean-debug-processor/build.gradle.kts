plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.vanniktech.publish)
}

dependencies {
    implementation(project(":mr-clean-processor-core"))
    implementation(project(":mr-clean-runtime"))
    ksp(libs.autoservice.ksp)
    implementation(libs.autoservice.annotations)

    implementation(libs.kotlin.stdlib)

    implementation(libs.ksp)

    testImplementation(libs.junit)
}

ksp {
    arg("autoserviceKsp.verify", "true")
    arg("autoserviceKsp.verbose", "true")
}
