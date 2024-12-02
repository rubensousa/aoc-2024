import java.io.File

fun readText(path: String): List<String> {
    return File(path).readLines()
}
