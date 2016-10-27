package pink.madis.gradle.liteandroid

import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import java.io.File

open class DxTask: Exec {
  @InputFiles
  var input: FileCollection = project.files()
    internal set

  @OutputFile
  var output: File = project.buildDir.resolve("dexes").resolve(name).resolve("classes.dex")
    internal set

  fun from(task: Task) {
    dependsOn(task)
    from(task.outputs.files)
  }

  fun from(file: File) {
    from(SimpleFileCollection(file))
  }

  fun from(files: FileCollection) {
    input += files
  }

  fun to(file: File) {
    output = file
  }

  constructor() : super() {
    // why is this required?
    project.afterEvaluate {
      doFirst {
        val ext = project.extensions.getByType(LiteAndroidExtension::class.java)
        val buildTools = ext.buildToolsVersion ?: throw IllegalStateException("Dx task used but buildToolsVersion not set in the liteAndroid {} block")
        setExecutable(ext.sdk.dx(buildTools))

        setArgs(listOf(
            "--dex",
            "--output",
            output.absolutePath
        ) + input.files.map { it.absolutePath })
      }
    }
  }
}