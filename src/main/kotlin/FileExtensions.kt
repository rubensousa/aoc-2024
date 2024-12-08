import java.io.File

fun readText(path: String): List<String> {
    return File("inputs/$path").readLines()
}

fun readLine(path: String): String {
    return File("inputs/$path").readText()
}
