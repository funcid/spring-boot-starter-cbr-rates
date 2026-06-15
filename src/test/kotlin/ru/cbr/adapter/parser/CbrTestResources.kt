package ru.cbr.adapter.parser

object CbrTestResources {
  fun read(path: String): String =
    javaClass.classLoader
      .getResourceAsStream(path)!!
      .bufferedReader()
      .use { it.readText() }
}
