pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // ← Aynan shu qatorni qo'shing
        maven { url = java.net.URI("https://jitpack.io") }
    }
}
rootProject.name = "Uzbgraph"
include(":app")
