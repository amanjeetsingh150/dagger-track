package me.amanjeet.daggertrack.util

import java.io.File

fun toOutputFile(outputDir: File, inputDir: File, inputFile: File) =
    File(outputDir, inputFile.relativeTo(inputDir).path)

fun File.isClassFile() = this.isFile && this.extension == "class"