import kotlin.concurrent.thread

println("=== Ejercicio 3 ===")
val hilos = (1..3).map { id ->
    thread(name = "Cuenta-$id") {
        for (n in 3 downTo 1) {
            println("[${Thread.currentThread().name}] $n")
            try { Thread.sleep(500) } catch (_: InterruptedException) { }
        }
        println("[${Thread.currentThread().name}] ¡Despegue!")
    }
}
hilos.forEach { it.join() }
println("Finalizó la cuenta regresiva de todos los hilos (Ejercicio 3)")