package pink.madis.gradle.liteandroid

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets
import java.util.Properties

fun ensureSdk(project: Project): Sdk {
  val location = findSdk(project)
  // we're looking for the readme and platforms, if those exist then we're already good to go
  if (!location.resolve("tools").exists() || !location.resolve("platforms").exists()) {
    throw FileNotFoundException(
        "lite-android: No valid SDK location found at '$location'.\nPlease ensure a correct " +
        "SDK location is defined through the sdk.dir property in local.properties file or the ANDROID_HOME environment " +
        "variable")
  }

  return Sdk(location)
}

fun findSdk(project: Project): File {
  var sdkDir: String? = null
  // use local.properties first
  val localProps = project.rootProject.file("local.properties")
  if (localProps.exists()) {
    val props = Properties()
    localProps.reader(StandardCharsets.UTF_8).use {
      props.load(it)
    }
    sdkDir = props.getProperty("sdk.dir")
  }

  // if that fails, try to look at ANDROID_HOME
  sdkDir = sdkDir ?: System.getenv("ANDROID_HOME")

  if (sdkDir != null) {
    return File(sdkDir)
  }
  throw IllegalStateException(
      "lite-android: SDK location not defined.\nPlease create a local.properties file with sdk.dir set or set " +
      "the ANDROID_HOME environment variable.")
}

class Sdk(baseDir: File) {
  private val platforms = baseDir.resolve("platforms")
  private val extras = baseDir.resolve("extras")
  private val buildtools = baseDir.resolve("build-tools")

  private val repos = listOf(
      extras.resolve("m2repository"),
      extras.resolve("android").resolve("m2repository"),
      extras.resolve("google").resolve("m2repository")
  )

  fun androidJar(version: String): File {
    return platforms
        .resolve(version)
        .resolve("android.jar")
        .ensure("The android.jar file for platform '$version' was not found. Either it's a typo or the SDK is missing it.")
  }

  fun dxJar(buildToolsVersion: String): File {
    return buildtools
        .resolve(buildToolsVersion)
        .resolve("lib")
        .resolve("dx.jar")
        .ensure("The dx jar for build tools '$buildToolsVersion' could not be found. Please make sure that the SDK is complete.")
  }

  fun extraRepos(): List<File> = repos.filter { it.exists() && it.isDirectory }
}

fun File.ensure(message: String): File {
  if (exists()) {
    return this
  }
  throw FileNotFoundException(message)
}