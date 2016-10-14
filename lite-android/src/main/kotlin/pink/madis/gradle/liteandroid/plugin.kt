package pink.madis.gradle.liteandroid

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile

class LiteAndroidPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    // try to find the SDK
    val sdk = ensureSdk(project)

    val extension = LiteAndroidExtension(sdk)
    project.extensions.add("liteAndroid", extension)

    project.afterEvaluate {
      extension.compileSdkVersion?.let { compileSdkVersion ->
        project.plugins.withType(JavaPlugin::class.java) {
          // put the android.jar on the boot cp of both the test task and the java task
          // TODO(madis) will this work with Kotlin/Scala/Groovy/whatevs?
          configureAndroidJar(project, sdk, compileSdkVersion)
        }
      }

      // register all found m2 repositories
      for (repoLocation in sdk.extraRepos()) {
        project.repositories.maven {
          it.setUrl(repoLocation)
        }
      }
    }
  }

  private fun configureAndroidJar(project: Project, sdk: Sdk, compileSdkVersion: String) {
    val androidJar = sdk.androidJar(compileSdkVersion)

    val compileJava = project.tasks.getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME)
    if (compileJava is JavaCompile) {
      compileJava.options.bootClasspath = androidJar.absolutePath
    }
    // add it to the compileOnly to keep IDEA happy, is there a better way to this?
    // this leaves both the rt.jar and the android.jar on the classpath for IDEA
    project.dependencies.add("compileOnly", project.files(androidJar))
    // add to testCompileOnly to make unit testing possible
    project.dependencies.add("testCompileOnly", project.files(androidJar))
  }
}

@Suppress("RedundantVisibilityModifier", "unused") // should be visible and can be unused
public class LiteAndroidExtension(internal val sdk: Sdk) {
  var compileSdkVersion: String? = null
  var buildToolsVersion: String? = null

  public fun compileSdkVersion(version: String) {
    compileSdkVersion = version
  }

  public fun compileSdkVersion(version: Int) {
    compileSdkVersion = "android-" + version
  }

  public fun buildToolsVersion(version: String) {
    buildToolsVersion = version
  }
}