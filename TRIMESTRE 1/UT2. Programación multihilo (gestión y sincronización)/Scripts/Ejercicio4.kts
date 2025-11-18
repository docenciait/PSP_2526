println("=== Ejercicio 4 ===")

val hilo1 = object : Thread("Inspector-1") {
    override fun run() {
        println("[${name}] Inicio: priority=${priority}, state=${state}")
        try { sleep(1000) } catch (_: InterruptedException) { }
        println("[${name}] Después de dormir: priority=${priority}, state=${state}")
    }
}

val hilo2 = object : Thread("Inspector-2") {
    override fun run() {
        println("[${name}] Inicio: isDaemon=${isDaemon}, isAlive=${isAlive}")
        try { sleep(500) } catch (_: InterruptedException) { }
        println("[${name}] Después de dormir: isDaemon=${isDaemon}, isAlive=${isAlive}")
    }
}

// (Opcional) Ajustar prioridad
hilo1.priority = Thread.NORM_PRIORITY + 1 // Un punto por encima

// Mostrar estado antes de arrancar
println("Estado previo hilo1=${hilo1.state}, hilo2=${hilo2.state}")

hilo1.start()
hilo2.start()

hilo1.join()
hilo2.join()

println("Estados finales hilo1=${hilo1.state}, hilo2=${hilo2.state}")
println("Ambos hilos han finalizado (Ejercicio 4)")