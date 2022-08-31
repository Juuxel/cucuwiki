plugins {
    java
    id("org.cadixdev.licenser")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

license {
    header(rootProject.file("HEADER.txt"))
}
