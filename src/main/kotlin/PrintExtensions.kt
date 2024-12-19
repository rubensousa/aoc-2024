import kotlin.system.measureTimeMillis

fun Any?.printObject() = println(this)

fun printAndReport(action: () -> Any) {
    var output: Any?
    val time = measureTimeMillis {
        output = action()
    }
    println("$output | Generated in $time ms")
}