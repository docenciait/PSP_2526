# Soluciones Ejercicios 2–5 (Multihilo Kotlin)

A continuación se presentan las soluciones en Kotlin para los ejercicios 2, 3, 4 y 5 solicitados. Cada sección incluye explicación y el código fuente propuesto. Todas las soluciones usan únicamente la biblioteca estándar (JVM) y el modelo de hilos clásico.

---
## Ejercicio 2
**Enunciado:** Crear un programa Kotlin que lance 5 hilos utilizando la clase `Thread`. Cada hilo debe imprimir un mensaje de inicio, dormir 1 segundo y luego imprimir un mensaje de fin.

### Explicación
Usamos una clase que extiende `Thread` para encapsular el comportamiento. Al iniciar todos casi simultáneamente, los mensajes pueden intercalarse. Sincronizamos la espera con `join()` para que el `main` no termine antes que los hilos.

### Código
```kotlin
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

fun ejercicio2() {
    println("=== Ejercicio 2 ===")
    val hilos = (1..5).map { WorkerThread(it) }
    hilos.forEach { it.start() }
    hilos.forEach { it.join() }
    println("Todos los hilos han finalizado (Ejercicio 2)\n")
}
```

---
## Ejercicio 3
**Enunciado:** Crear un programa Kotlin que use `kotlin.concurrent.thread` para lanzar 3 hilos que realicen una cuenta regresiva de 3..1 con una espera de 500 ms entre números.

### Explicación
Se usa la función de conveniencia `thread {}` que crea y arranca el hilo. Cada hilo ejecuta la misma lógica de cuenta regresiva. Al final usamos `join()` para asegurar la finalización antes de salir del `main`.

### Código
```kotlin
import kotlin.concurrent.thread

fun ejercicio3() {
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
    println("Finalizó la cuenta regresiva de todos los hilos (Ejercicio 3)\n")
}
```

---
## Ejercicio 4
**Enunciado:** Crear un programa Kotlin con 2 hilos:
- Hilo 1: mostrar nombre, prioridad y estado antes y después de dormir 1 s.
- Hilo 2: mostrar `isDaemon` e `isAlive` antes y después de dormir 500 ms.
El `main` debe esperar a ambos con `join()`.

### Explicación
El estado (`state`) y otros atributos pueden consultarse antes de iniciar (NEW), tras arrancar (RUNNABLE / TIMED_WAITING), etc. Para el segundo hilo activamos o no modo daemon para mostrar la diferencia (en este ejemplo lo dejamos como no daemon para que se aprecie vivo durante la espera). Usamos pequeñas impresiones antes y después del sueño.

### Código
```kotlin
fun ejercicio4() {
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
    println("Ambos hilos han finalizado (Ejercicio 4)\n")
}
```

---
## Ejercicio 5
**Enunciado:** Implementar en Kotlin el ejercicio 1: lanzar 10 hilos (usando una `List`) que calculen el área de un triángulo con `base` y `altura`, y mostrar el resultado. Fórmula: `área = (base * altura) / 2`.

### Explicación
Creamos dos listas de bases y alturas (del mismo tamaño). Cada hilo toma su índice y calcula el área. Guardamos resultados en una estructura sincronizada (`MutableList` protegido con un lock) o simplemente imprimimos. Para conservar orden final recogemos los resultados en una lista de pares.

### Código
```kotlin
fun ejercicio5() {
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
    println("Fin cálculo de áreas (Ejercicio 5)\n")
}
```

---
## Ejecución conjunta (opcional)
Puedes crear un `main()` para invocar todos:
```kotlin
fun main() {
    ejercicio2()
    ejercicio3()
    ejercicio4()
    ejercicio5()
}
```

## Posibles Salidas (Ejemplo Parcial)
```
=== Ejercicio 2 ===
[Hilo-1] Inicio trabajo
[Hilo-3] Inicio trabajo
[Hilo-2] Inicio trabajo
[Hilo-5] Inicio trabajo
[Hilo-4] Inicio trabajo
[Hilo-3] Fin trabajo
[Hilo-1] Fin trabajo
...
=== Ejercicio 3 ===
[Cuenta-1] 3
[Cuenta-2] 3
[Cuenta-3] 3
...
=== Ejercicio 4 ===
Estado previo hilo1=NEW, hilo2=NEW
[Inspector-1] Inicio: priority=6, state=RUNNABLE
[Inspector-2] Inicio: isDaemon=false, isAlive=true
...
=== Ejercicio 5 ===
[Triangulo-1] base=3.0 altura=4.0 área=6.0
...
```

## Notas
- Los estados de hilo pueden variar según el planificador.
- Para experimentar con `isDaemon`, prueba `hilo2.isDaemon = true` antes de `start()`.
- En ejercicio 5 los accesos a la lista de resultados se protegen con `synchronized` para asegurar visibilidad y ausencia de condiciones de carrera.

---
**Fin de Soluciones3.md**
