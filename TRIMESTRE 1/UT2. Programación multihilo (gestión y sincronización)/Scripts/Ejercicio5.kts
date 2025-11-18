println("=== Ejercicio 5 ===")
val bases = listOf(3.0, 5.5, 10.0, 7.2, 6.0, 9.1, 4.4, 8.0, 2.5, 12.0)
val alturas = listOf(4.0, 2.0, 5.0, 3.3, 10.0, 6.2, 9.5, 1.5, 7.2, 4.0)

require(bases.size == alturas.size) { "Listas deben tener mismo tamaño" }

val results = MutableList<Double?>(bases.size) { null }
val lock = Any()

val hilos = bases.indices.map { i ->
    Thread("Triangulo-${i + 1}") {
        val base = bases[i]
        val altura = alturas[i]
        val area = (base * altura) / 2.0
        synchronized(lock) {
            results[i] = area
        }
        println("[${Thread.currentThread().name}] base=$base altura=$altura área=$area")
    }
}

hilos.forEach { it.start() }
hilos.forEach { it.join() }

println("Resultados finales ordenados:")
results.forEachIndexed { i, area ->
    println("Triángulo ${i + 1}: área=$area")
}
println("Fin cálculo de áreas (Ejercicio 5)")