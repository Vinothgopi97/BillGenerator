plugins {
    id("java")
}

group = "org.vinothgopi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // iText library for PDF generation
    implementation("com.itextpdf:itext7-core:7.1.15")

    // ZXing library for QR code generation
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.google.zxing:javase:3.4.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}