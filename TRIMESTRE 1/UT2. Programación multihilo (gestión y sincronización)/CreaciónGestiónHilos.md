---
sidebar_position: 3
title: Creación y Gestión de Hilos
---

# Creación y Gestión de Hilos

En esta sección aprenderemos cómo crear, iniciar, gestionar y finalizar hilos en Kotlin. Exploraremos las diferentes formas de trabajar con hilos, desde el uso de la clase `Thread` tradicional hasta enfoques más modernos como las corrutinas de Kotlin.

## Creación de hilos en Kotlin

Kotlin, al ejecutarse sobre la JVM, hereda todas las capacidades de threading de Java y añade sus propias mejoras sintácticas. Existen varias formas de crear y ejecutar hilos:

### 1. Usando la clase Thread directamente

La forma más básica es crear una instancia de la clase `Thread` y pasarle una función lambda:

```kotlin
fun main() {
    val thread = Thread {
        println("Hilo ejecutándose: ${Thread.currentThread().name}")
        Thread.sleep(1000)
        println("Hilo finalizado")
    }

    thread.start() // Inicia la ejecución del hilo
    println("Hilo principal continúa")
    thread.join() // Espera a que el hilo termine
    println("Programa finalizado")
}
```

**Salida esperada:**

```
Hilo principal continúa
Hilo ejecutándose: Thread-0
Hilo finalizado
Programa finalizado
```

:::warning No confundir start() con run()
Es importante usar `start()` y no `run()`. El método `start()` crea un nuevo hilo de ejecución, mientras que `run()` ejecuta el código en el hilo actual.

```kotlin
thread.run()  // ❌ Se ejecuta en el hilo principal
thread.start() // ✅ Se ejecuta en un nuevo hilo
```

:::

### 2. Usando la función thread() de Kotlin

Kotlin proporciona una función de extensión más concisa para crear hilos:

```kotlin
import kotlin.concurrent.thread

fun main() {
    val myThread = thread(start = true, name = "MiHilo") {
        println("Ejecutando en: ${Thread.currentThread().name}")
        Thread.sleep(1000)
        println("Tarea completada")
    }

    myThread.join()
}
```

**Parámetros de la función thread():**

- `start`: Boolean que indica si el hilo debe iniciarse inmediatamente (default: true)
- `isDaemon`: Boolean que indica si es un hilo demonio (default: false)
- `contextClassLoader`: ClassLoader para el hilo
- `name`: Nombre del hilo
- `priority`: Prioridad del hilo (1-10)
- `block`: Función lambda que contiene el código a ejecutar

### 3. Extendiendo la clase Thread

Aunque menos común en Kotlin, también es posible extender la clase `Thread`:

```kotlin
class MiHilo : Thread() {
    override fun run() {
        println("Ejecutando hilo personalizado: $name")
        sleep(1000)
        println("Hilo personalizado finalizado")
    }
}

fun main() {
    val hilo = MiHilo()
    hilo.name = "HiloPersonalizado"
    hilo.start()
    hilo.join()
}
```

## Propiedades y métodos importantes de Thread

### Propiedades principales

| Propiedad  | Descripción                                   |
| ---------- | --------------------------------------------- |
| `name`     | Nombre del hilo                               |
| `priority` | Prioridad del hilo (1-10, donde 10 es máxima) |
| `isDaemon` | Indica si es un hilo demonio                  |
| `state`    | Estado actual del hilo                        |
| `isAlive`  | Indica si el hilo está vivo                   |

### Métodos principales

| Método            | Descripción                                    |
| ----------------- | ---------------------------------------------- |
| `start()`         | Inicia la ejecución del hilo                   |
| `join()`          | Espera a que el hilo termine                   |
| `join(millis)`    | Espera un tiempo máximo a que el hilo termine  |
| `interrupt()`     | Interrumpe el hilo                             |
| `isInterrupted()` | Verifica si el hilo ha sido interrumpido       |
| `sleep(millis)`   | Pausa el hilo actual (método estático)         |
| `yield()`         | Sugiere al planificador que cambie a otro hilo |

### Ejemplo completo de gestión de hilos

```kotlin
import kotlin.concurrent.thread

fun main() {
    // Obtener información sobre el hilo actual
    val mainThread = Thread.currentThread()
    println("Hilo principal: ${mainThread.name}")
    println("Prioridad: ${mainThread.priority}")
    println("Estado: ${mainThread.state}")

    // Crear múltiples hilos con diferentes configuraciones
    val hilos = List(3) { i ->
        thread(
            start = false,
            name = "Trabajador-$i",
            priority = Thread.NORM_PRIORITY,
            isDaemon = false
        ) {
            println("[${Thread.currentThread().name}] Iniciando tarea")
            repeat(3) { j ->
                println("[${Thread.currentThread().name}] Paso $j")
                Thread.sleep(500)
            }
            println("[${Thread.currentThread().name}] Tarea completada")
        }
    }

    // Iniciar todos los hilos
    println("\nIniciando hilos...")
    hilos.forEach { it.start() }

    // Esperar a que todos terminen
    println("Esperando a que los hilos terminen...")
    hilos.forEach { it.join() }

    println("\nTodos los hilos han terminado")
}
```

## Hilos daemon vs hilos normales

Los hilos en Java/Kotlin pueden ser de dos tipos:

### Hilos normales (User threads)

- La JVM espera a que todos los hilos normales terminen antes de finalizar el programa
- Son los hilos predeterminados
- Útiles para tareas que deben completarse

### Hilos daemon

- La JVM no espera a que los hilos daemon terminen
- El programa finaliza cuando todos los hilos normales han terminado, incluso si hay hilos daemon ejecutándose
- Útiles para tareas de soporte o mantenimiento

```kotlin
import kotlin.concurrent.thread

fun main() {
    // Hilo daemon
    thread(isDaemon = true, name = "Daemon") {
        repeat(10) {
            println("Hilo daemon ejecutándose... $it")
            Thread.sleep(500)
        }
    }

    // Hilo normal
    thread(isDaemon = false, name = "Normal") {
        repeat(3) {
            println("Hilo normal ejecutándose... $it")
            Thread.sleep(500)
        }
    }

    println("Hilo principal finaliza")
    // El programa termina cuando el hilo normal termina,
    // sin esperar al hilo daemon
}
```

## Gestión del ciclo de vida de los hilos

### Método join()

El método `join()` hace que el hilo actual espere a que otro hilo termine:

```kotlin
fun main() {
    val hilo = thread {
        println("Tarea larga iniciada")
        Thread.sleep(2000)
        println("Tarea larga completada")
    }

    println("Esperando a que el hilo termine...")
    hilo.join() // Bloquea hasta que hilo termine
    println("Continuando después de join()")
}
```

También podemos especificar un tiempo máximo de espera:

```kotlin
fun main() {
    val hilo = thread {
        Thread.sleep(5000)
        println("Hilo terminado")
    }

    hilo.join(2000) // Espera máximo 2 segundos

    if (hilo.isAlive) {
        println("El hilo aún está ejecutándose después de 2 segundos")
    }
}
```

### Interrupción de hilos

Podemos solicitar la interrupción de un hilo usando `interrupt()`:

```kotlin
import kotlin.concurrent.thread

fun main() {
    val hilo = thread {
        try {
            repeat(10) { i ->
                println("Trabajando... $i")
                Thread.sleep(1000)
            }
        } catch (e: InterruptedException) {
            println("Hilo interrumpido!")
            return@thread
        }
    }

    Thread.sleep(3000)
    println("Solicitando interrupción...")
    hilo.interrupt()
    hilo.join()
    println("Programa finalizado")
}
```

:::tip Verificar interrupciones
Un hilo debe verificar regularmente si ha sido interrumpido y responder apropiadamente:

```kotlin
thread {
    while (!Thread.currentThread().isInterrupted) {
        // Realizar trabajo
        println("Trabajando...")
        Thread.sleep(500)
    }
    println("Hilo interrumpido, finalizando limpiamente")
}
```

:::

## Prioridades de hilos

Los hilos pueden tener prioridades de 1 (MIN_PRIORITY) a 10 (MAX_PRIORITY), siendo 5 (NORM_PRIORITY) el valor por defecto:

```kotlin
fun main() {
    val hiloPrioridadAlta = thread(
        start = false,
        name = "Alta",
        priority = Thread.MAX_PRIORITY
    ) {
        repeat(5) {
            println("[ALTA] Iteración $it")
        }
    }

    val hiloPrioridadBaja = thread(
        start = false,
        name = "Baja",
        priority = Thread.MIN_PRIORITY
    ) {
        repeat(5) {
            println("[BAJA] Iteración $it")
        }
    }

    hiloPrioridadBaja.start()
    hiloPrioridadAlta.start()

    hiloPrioridadAlta.join()
    hiloPrioridadBaja.join()
}
```

:::warning Las prioridades son sugerencias
Las prioridades son solo sugerencias al planificador del sistema operativo. No garantizan el orden de ejecución y su comportamiento puede variar según el sistema operativo y la carga del sistema.
:::

## Pools de hilos con ExecutorService

Para aplicaciones que crean muchos hilos, es más eficiente usar pools de hilos:

```kotlin
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    // Crear un pool con 4 hilos
    val executor = Executors.newFixedThreadPool(4)

    // Enviar tareas al pool
    repeat(10) { i ->
        executor.submit {
            println("[${Thread.currentThread().name}] Tarea $i iniciada")
            Thread.sleep(1000)
            println("[${Thread.currentThread().name}] Tarea $i completada")
        }
    }

    // Cerrar el executor
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)
    println("Todas las tareas completadas")
}
```

### Tipos de ExecutorService

```kotlin
import java.util.concurrent.Executors

// Pool de tamaño fijo
val fixedPool = Executors.newFixedThreadPool(4)

// Pool que crea hilos según necesidad
val cachedPool = Executors.newCachedThreadPool()

// Pool con un solo hilo
val singleThread = Executors.newSingleThreadExecutor()

// Pool para tareas programadas
val scheduledPool = Executors.newScheduledThreadPool(2)
```

### Ejemplo con ScheduledExecutorService

```kotlin
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val scheduler = Executors.newScheduledThreadPool(1)

    // Ejecutar después de un retraso
    scheduler.schedule({
        println("Tarea ejecutada después de 2 segundos")
    }, 2, TimeUnit.SECONDS)

    // Ejecutar periódicamente
    scheduler.scheduleAtFixedRate({
        println("Tarea periódica: ${System.currentTimeMillis()}")
    }, 0, 1, TimeUnit.SECONDS)

    // Dejar ejecutar durante 5 segundos
    Thread.sleep(5000)
    scheduler.shutdown()
}
```

## Introducción a las Corrutinas de Kotlin

Las corrutinas son una forma más moderna y eficiente de manejar concurrencia en Kotlin. Aunque profundizaremos más en ellas en una sección posterior, aquí hay una introducción básica:

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(1000)
        println("Corrutina 1")
    }

    launch {
        delay(500)
        println("Corrutina 2")
    }

    println("Hilo principal")
}
```

:::info Ventajas de las corrutinas

- Más ligeras que los hilos (miles de corrutinas pueden ejecutarse en pocos hilos)
- Sintaxis más simple y legible
- Mejor manejo de la concurrencia estructurada
- Cancelación cooperativa integrada
- Excelente integración con funciones suspend
  :::

## Buenas prácticas

1. **Nombra tus hilos**: Facilita la depuración y el seguimiento
2. **Usa pools de hilos**: Para aplicaciones que crean muchos hilos
3. **Maneja excepciones**: Los hilos deben manejar sus propias excepciones
4. **Limpia recursos**: Asegúrate de cerrar recursos en bloques `finally`
5. **Evita crear demasiados hilos**: Cada hilo consume recursos del sistema
6. **Considera usar corrutinas**: Para casos de uso modernos en Kotlin

## Ejemplo práctico: Descarga paralela de archivos

```kotlin
import kotlin.concurrent.thread

data class DownloadTask(val url: String, val id: Int)

fun downloadFile(task: DownloadTask) {
    println("[Hilo ${Thread.currentThread().name}] Descargando: ${task.url}")
    // Simular descarga
    Thread.sleep((1000..3000).random().toLong())
    println("[Hilo ${Thread.currentThread().name}] Completado: ${task.url}")
}

fun main() {
    val tasks = listOf(
        DownloadTask("http://example.com/file1.zip", 1),
        DownloadTask("http://example.com/file2.zip", 2),
        DownloadTask("http://example.com/file3.zip", 3),
        DownloadTask("http://example.com/file4.zip", 4)
    )

    val startTime = System.currentTimeMillis()

    val threads = tasks.map { task ->
        thread(name = "Downloader-${task.id}") {
            downloadFile(task)
        }
    }

    threads.forEach { it.join() }

    val endTime = System.currentTimeMillis()
    println("\nTodas las descargas completadas en ${endTime - startTime}ms")
}
```

## Conclusión

La gestión adecuada de hilos es fundamental para crear aplicaciones concurrentes eficientes. Aunque los hilos tradicionales son poderosos, es importante conocer también alternativas modernas como los pools de hilos y las corrutinas de Kotlin, que ofrecen abstracciones de más alto nivel y mejor rendimiento en muchos casos.
