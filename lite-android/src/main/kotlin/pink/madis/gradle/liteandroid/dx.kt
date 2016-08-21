package pink.madis.gradle.liteandroid

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import java.io.File

open class DxTask: Exec() {
  @InputFiles
  var input: FileCollection? = null

  @OutputFile
  var output: File? = null

  fun withDx(dx: File) {
    val inp = input
    val out = output

    if (inp == null) {
      throw IllegalArgumentException("Input files are not defined for the dex task")
    }

    if (out == null) {
      throw IllegalArgumentException("Output path is not defined for the dex task")
    }

    val args = listOf(
        "--dex",
        "--output",
        out.absolutePath
    ) + inp.files.map { it.absolutePath }

    setExecutable(dx)
    setArgs(args)
  }
}