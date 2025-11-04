import kotlin.concurrent.thread

/**
 * Ejercicio 2:
 * Crea 5 hilos usando la clase Thread.
 * Cada hilo imprime su nombre, indica que ha iniciado, duerme 1 segundo y luego indica que ha finalizado.
 */
fun ejercicio2() {
    val hilos = List(5) { i ->
        val hilo = Thread {
            val nombre = Thread.currentThread().name
            println("[$nombre] Iniciado (índice=$i)")
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                println("[$nombre] Interrumpido")
            }
            println("[$nombre] Finalizado (índice=$i)")
        }
        hilo.name = "Hilo-$i"
        hilo   // Devuelve el hilo configurado
    }

    hilos.forEach { it.start() }
    hilos.forEach { it.join() }
    println("Main: Todos los hilos han terminado (Ejercicio 2)")
}

/**
 * Ejercicio 3:
 * Usa la función thread() de Kotlin para crear 3 hilos.
 * Cada hilo realiza una cuenta regresiva desde 3 hasta 1 con pausas de 0.5 segundos.
 */
fun ejercicio3() {
    val tareas = List(3) { i ->
        thread(name = "Contador-$i") {
            val nombre = Thread.currentThread().name
            for (j in 3 downTo 1) {
                println("[$nombre] Cuenta: $j")
                Thread.sleep(500)
            }
            println("[$nombre] Terminado")
        }
    }

    tareas.forEach { it.join() }
    println("Main: Todos los contadores han terminado (Ejercicio 3)")
}

/**
 * Ejercicio 4:
 * Crea dos hilos con distintas propiedades.
 * - Hilo1: muestra su nombre, prioridad y estado antes y después de dormir 1 segundo.
 * - Hilo2: muestra si es daemon y si está vivo antes y después de dormir 0.5 segundos.
 */
fun ejercicio4() {
    val hilo1 = Thread {
        val t = Thread.currentThread()
        println("[${t.name}] Antes -> Nombre: ${t.name}, Prioridad: ${t.priority}, Estado: ${t.state}")
        Thread.sleep(1000)
        println("[${t.name}] Después -> Estado: ${t.state}, ¿isAlive?: ${t.isAlive}")
    }
    hilo1.name = "Hilo-Propiedades-1"
    hilo1.priority = Thread.MAX_PRIORITY

    val hilo2 = Thread {
        val t = Thread.currentThread()
        println("[${t.name}] Antes -> isDaemon: ${t.isDaemon}, isAlive: ${t.isAlive}")
        Thread.sleep(500)
        println("[${t.name}] Después -> isDaemon: ${t.isDaemon}, isAlive: ${t.isAlive}")
    }
    hilo2.name = "Hilo-Propiedades-2"
    hilo2.isDaemon = false

    hilo1.start()
    hilo2.start()
    hilo1.join()
    hilo2.join()
    println("Main: Ambos hilos han terminado (Ejercicio 4)")
}

/**
 * Ejercicio 5:
 * Calcula el área de 10 triángulos en paralelo usando hilos.
 * Cada hilo calcula el área (base * altura / 2) y la muestra por pantalla.
 */
fun ejercicio5() {
    val bases = listOf(3.0, 5.0, 7.0, 2.0, 6.0, 4.0, 8.0, 9.0, 1.0, 10.0)
    val alturas = listOf(4.0, 2.0, 5.0, 6.0, 3.0, 7.0, 2.0, 8.0, 9.0, 1.0)

    val hilos = List(bases.size) { i ->
        thread(name = "Triangulo-$i") {
            val base = bases[i]
            val altura = alturas[i]
            val area = (base * altura) / 2.0
            println("[${Thread.currentThread().name}] Base: $base, Altura: $altura => Área: $area")
        }
    }

    hilos.forEach { it.join() }
    println("Main: Cálculos de áreas completados (Ejercicio 5)")
}

/**
 * Función principal para ejecutar los ejercicios.
 * Puedes comentar/descomentar las llamadas según el que quieras probar.
 */
fun main() {
    println("=== Ejercicio 2 ===")
    ejercicio2()
    println()

    println("=== Ejercicio 3 ===")
    ejercicio3()
    println()

    println("=== Ejercicio 4 ===")
    ejercicio4()
    println()

    println("=== Ejercicio 5 ===")
    ejercicio5()
}
