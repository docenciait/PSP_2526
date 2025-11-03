# Actividades sobre Corrutinas (Actividades2)

Este fichero propone actividades prácticas sobre corrutinas basadas en el documento "3_CorrutinasKotlin.md". Cada actividad incluye el enunciado, objetivos, pistas y, cuando procede, casos de prueba o salida esperada.

---

## Actividad 1 — Función suspend y delay

Enunciado
: Escribe una función `suspend fun fetchValue(id: Int): String` que simule una operación de red usando `delay(700)` y devuelva "Value-<id>". Luego crea una función `main` con `runBlocking` que llame a `fetchValue(1)` y muestre el resultado.

Objetivos

- Usar `suspend` y `delay`.
- Llamar a una función suspend desde `runBlocking`.

Pistas

- `suspend fun fetchValue(id: Int): String { delay(700); return "Value-$id" }`
- En `main` usa `val v = fetchValue(1)` dentro de `runBlocking`.

Salida esperada (aprox.):

```
Value-1
```

---

## Actividad 2 — Launch vs async (paralelismo)

Enunciado
: Implementa dos versiones para obtener dos resultados:

1. Secuencial: llamar a `suspend fun op(id: Int): String` dos veces y sumar el tiempo.
2. Paralelo: lanzar dos `async` y `await` sus resultados.

Medir con `System.currentTimeMillis()` el tiempo de ejecución para comparar ambos enfoques usando `delay(1000)` dentro de `op`.

Objetivos

- Entender la diferencia entre `launch`/`async` y ejecución secuencial.
- Usar `Deferred` y `await()`.

Pistas

- `op` debe usar `delay(1000)` y devolver un string.
- En paralelo usa `val d1 = async { op(1) }`, `val d2 = async { op(2) }`, luego `d1.await()` y `d2.await()`.

Salida esperada (aprox.):

- Secuencial: ~2000 ms
- Paralelo: ~1000 ms

---

## Actividad 3 — Dispatchers y withContext

Enunciado
: Escribe una función `suspend fun calculateHeavy(): Int` que simule trabajo intensivo (usa `withContext(Dispatchers.Default)` y dentro `repeat(1_000_000) { /* pequeña operación */ }`) y devuelva un entero. En `main` (runBlocking) llama a `calculateHeavy()` y muestra el resultado y el nombre del hilo desde donde se ejecuta.

Objetivos

- Aprender a cambiar de contexto con `withContext`.
- Ver la diferencia de hilos usados por dispatchers.

Pistas

- Dentro de `withContext` puedes usar `Thread.currentThread().name`.

---

## Actividad 4 — Cancellation cooperativa

Enunciado
: Crea una corrutina que realice trabajo en bucle (por ejemplo, `repeat(1000)`) y que verifique regularmente `isActive`. Desde el `main`, lanza la corrutina, espera 1 segundo y solicita cancelación con `job.cancelAndJoin()`. Asegura que la corrutina realiza una limpieza en `finally`.

Objetivos

- Practicar cancelación cooperativa y limpieza en `finally`.

Pistas

- Dentro del bloque `launch { try { while (isActive) { ...; delay(100) } } finally { println("limpieza") } }`.

Salida esperada (parcial):

```
Trabajando... (varias líneas)
Cancelando...
Limpieza de recursos
```

---

## Actividad 5 — Channels (productor-consumidor)

Enunciado
: Implementa un productor que envíe los números del 1 al 10 a un `Channel<Int>` y un consumidor que los reciba e imprima. Cierra el canal al terminar.

Objetivos

- Usar `Channel`, `send`, `receive` y `for (v in channel)`.

Pistas

- `val channel = Channel<Int>()`
- En el productor `repeat(10) { channel.send(it + 1); delay(50) } channel.close()`
- En el consumidor `for (v in channel) println(v)`.

---

## Actividad 6 — Flow básico y operadores

Enunciado
: Crea un `flow` que emita los números 1..6 con `delay(100)` entre emisiones. En `main`, aplica `filter { it % 2 == 0 }`, `map { it * it }` y `collect` los primeros 2 resultados usando `take(2)`.

Objetivos

- Usar `flow`, operadores `filter`, `map` y `take`.

Pistas

- `(1..6).asFlow().filter { ... }.map { ... }.take(2).collect { println(it) }`

Salida esperada:

```
4
16
```

---

## Actividad 7 — StateFlow y SharedFlow (observadores)

Enunciado
: Implementa un `Counter` que use `MutableStateFlow<Int>` para exponer el contador como `StateFlow`. En `main`, lanza una corrutina que recoja `counter.count` y otra que incremente el contador cada 150 ms tres veces.

Objetivos

- Practicar `StateFlow` y observar cambios de estado.

Pistas

- `private val _count = MutableStateFlow(0); val count: StateFlow<Int> = _count.asStateFlow()`
- `launch { counter.count.collect { println(it) } }`
- `launch { repeat(3) { delay(150); counter.increment() } }`

---

## Actividad 8 — SupervisorJob y manejo de fallos

Enunciado
: Crea un `SupervisorJob` dentro de un `CoroutineScope`. Lanza tres corrutinas hijas: una lanza después de 50 ms una excepción, las otras dos se retrasan y completan. Observa que las demás no se cancelan al fallar una.

Objetivos

- Entender `SupervisorJob` y aislamiento de fallos.

Pistas

- Usa `with(CoroutineScope(coroutineContext + SupervisorJob())) { ... }` o `CoroutineScope(Dispatchers.Default + SupervisorJob())`.

---

## Actividades prácticas — casos reales

Estas prácticas muestran escenarios reales donde las corrutinas facilitan la implementación de tareas asíncronas y concurrentes. Incluyen enunciado, objetivos, pistas y ejemplos mínimos.

### Práctica A — Llamadas de red paralelas y composición

Enunciado: Simula dos llamadas de red independientes (`fetchUser()` y `fetchPermissions()`), ejecútalas en paralelo con `async`, combina los resultados en un único objeto y muestra el resultado. Maneja errores de forma que una llamada fallida no cancele completamente la combinación.

Objetivos:

- Usar `async`/`await` para paralelizar I/O.
- Aprender a componer resultados y tratar errores básicos.

Pistas:

- Usa `supervisorScope` o captura excepciones en cada `async`.
- Simula latencia con `delay`.

Ejemplo mínimo:

```kotlin
import kotlinx.coroutines.*

data class User(val id: Int, val name: String)
data class Permissions(val items: List<String>)

suspend fun fetchUser(id: Int): User { delay(800); return User(id, "User$id") }
suspend fun fetchPermissions(id: Int): Permissions { delay(600); return Permissions(listOf("READ","WRITE")) }

fun main() = runBlocking {
	supervisorScope {
		val u = async { fetchUser(1) }
		val p = async { fetchPermissions(1) }
		println("Combined: ${'$'}{u.await()} + ${'$'}{p.await()}")
	}
}
```

---

### Práctica B — Procesamiento paralelo (CPU-bound)

Enunciado: Dada una lista de "imágenes" (IDs), procesa cada una en paralelo usando `Dispatchers.Default`. Reúne los resultados con `awaitAll()`.

Objetivos:

- Elegir el dispatcher adecuado para operaciones CPU-bound.
- Usar `async` y `awaitAll` para paralelizar y recolectar resultados.

Pistas:

- Implementa la función `suspend fun processImage(id: Int): Int` con `withContext(Dispatchers.Default)`.

Ejemplo mínimo:

```kotlin
import kotlinx.coroutines.*

suspend fun processImage(id: Int): Int = withContext(Dispatchers.Default) {
	// trabajo simulado
	var x = 0
	repeat(50_000) { x += it % 3 }
	id * 2
}

fun main() = runBlocking {
	val images = (1..8).toList()
	val deferred = images.map { id -> async { processImage(id) } }
	val results = deferred.awaitAll()
	println(results)
}
```

---

### Práctica C — Progreso via Flow (UI/terminal)

Enunciado: Implementa un `flow` que emita el progreso (0..100) de una descarga simulada. En `main`, consume el flow y actualiza la salida (por ejemplo, imprime la barra de progreso).

Objetivos:

- Usar `flow` para emitir valores periódicos.
- Separar generación y consumo (producer/consumer) en corrutinas.

Pistas:

- `flow { for (i in 0..100 step 10) { delay(150); emit(i) } }` y usar `flowOn(Dispatchers.IO)`.

Ejemplo mínimo:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun progressFlow() = flow {
	for (p in 0..100 step 10) { delay(150); emit(p) }
}.flowOn(Dispatchers.IO)

fun main() = runBlocking {
	progressFlow().collect { p -> println("Progreso: $p%") }
}
```

---

### Práctica D — Bridge para APIs callback (suspending)

Enunciado: Dada una API basada en callbacks (por ejemplo, `startDownload(url, callback)`), crea un adaptador `suspend fun downloadSuspending(url: String): ByteArray` usando `suspendCancellableCoroutine` para integrar la API con corrutinas y soportar cancelación.

Objetivos:

- Convertir APIs callback a suspending functions.
- Manejar cancelación invocando `invokeOnCancellation`.

Pistas:

- Usa `suspendCancellableCoroutine` y `cont.invokeOnCancellation { /* cancelar llamada */ }`.

Esqueleto:

```kotlin
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun downloadSuspending(url: String): ByteArray = suspendCancellableCoroutine { cont ->
	val call = startDownload(url) { data, err ->
		if (err != null) cont.resumeWithException(err) else cont.resume(data)
	}
	cont.invokeOnCancellation { call.cancel() }
}
```

---

### Práctica E — Servicio con CoroutineScope y cancelación (lifecycle)

Enunciado: Implementa `WorkerService` que lanza tareas periódicas en su propio `CoroutineScope` y que pueda detenerse limpiamente (cancelando su scope).

Objetivos:

- Aprender a encapsular `CoroutineScope` en clases y a cancelar correctamente.

Pistas:

- `private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())`
- `fun stop() = scope.cancel()`

Ejemplo mínimo:

```kotlin
class WorkerService {
	private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
	fun start() { scope.launch { while (isActive) { println("tick"); delay(500) } } }
	fun stop() { scope.cancel() }
}

fun main() = runBlocking {
	val s = WorkerService(); s.start(); delay(1200); s.stop(); delay(300)
}
```

---

Si quieres, puedo generar los archivos `.kt` con soluciones completas en la carpeta `UT2` y ejecutar alguno para verificar la salida. ¿Los creo ahora?
