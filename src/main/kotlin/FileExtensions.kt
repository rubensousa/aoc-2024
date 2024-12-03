import java.io.File

fun readText(path: String): List<String> {
    return File(path).readLines()
}

fun readLine(path: String): String {
    return File(path).readText()
}
