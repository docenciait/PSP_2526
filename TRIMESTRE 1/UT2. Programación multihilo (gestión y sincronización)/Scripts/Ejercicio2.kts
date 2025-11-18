class WorkerThread(private val id: Int) : Thread("Hilo-$id") {
    override fun run() {
        println("[${name}] Inicio trabajo")
        try {
            sleep(1000) // 1 segundo
        } catch (e: InterruptedException) {
            println("[${name}] Interrumpido")
        }
        println("[${name}] Fin trabajo")
    }
}

println("=== Ejercicio 2 ===")
val hilos = (1..5).map { WorkerThread(it) }
hilos.forEach { it.start() }
hilos.forEach { it.join() }
println("Todos los hilos han finalizado (Ejercicio 2)")