# 2.3 Corrutinas en Kotlin

Las corrutinas son una característica poderosa de Kotlin que proporciona una forma más sencilla y eficiente de escribir código asíncrono y concurrente. A diferencia de los hilos tradicionales, las corrutinas son extremadamente ligeras y pueden suspenderse sin bloquear el hilo subyacente.

## ¿Qué son las corrutinas?

Las **corrutinas** son componentes de código que pueden suspender su ejecución sin bloquear el hilo y reanudarse más tarde. Esto permite escribir código asíncrono de manera secuencial, haciéndolo más legible y mantenible.

:::tip Corrutinas vs Hilos
| Característica | Hilos | Corrutinas |
|----------------|-------|------------|
| Peso | Pesados (~1MB stack) | Ligeras (~KB) |
| Creación | Costosa | Muy barata |
| Cantidad | Miles | Millones |
| Cambio de contexto | Costoso | Muy barato |
| Bloqueo | Bloquea el hilo | Suspende sin bloquear |
| API | Compleja | Simple y legible |
:::

### Ventajas de las corrutinas

1. **Ligereza**: Puedes crear millones de corrutinas sin agotar los recursos del sistema
2. **Menos fugas de memoria**: Soporte integrado para cancelación estructurada
3. **Integración nativa**: Soporte de primera clase en Kotlin
4. **Código legible**: El código asíncrono se ve como código secuencial
5. **Menos callbacks**: Evita el "callback hell"

## Configuración inicial

Para usar corrutinas, añade la dependencia en tu proyecto:

```kotlin title="build.gradle.kts"
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

## Conceptos básicos

### 1. Función suspend

Una función `suspend` puede suspender su ejecución sin bloquear el hilo:

```kotlin
import kotlinx.coroutines.*

suspend fun fetchUser(userId: Int): User {
    delay(1000) // Suspende por 1 segundo sin bloquear
    return User(userId, "Usuario $userId")
}

suspend fun fetchOrders(userId: Int): List<Order> {
    delay(500)
    return listOf(Order(1, "Pedido 1"), Order(2, "Pedido 2"))
}

data class User(val id: Int, val name: String)
data class Order(val id: Int, val description: String)
```

:::info Palabra clave suspend
La palabra clave `suspend` marca una función que puede suspenderse. Solo puede ser llamada desde otra función suspend o desde una corrutina.
:::

### 2. Builders de corrutinas

Los builders crean y lanzan corrutinas:

#### runBlocking

Bloquea el hilo actual hasta que la corrutina termine (útil principalmente en funciones main y tests):

```kotlin
fun main() = runBlocking {
    println("Inicio")
    delay(1000)
    println("Fin después de 1 segundo")
}
```

#### launch

Lanza una corrutina que no devuelve resultado (fire-and-forget):

```kotlin
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
    delay(2000) // Esperar a que terminen
}

// Salida:
// Hilo principal
// Corrutina 2 (después de 500ms)
// Corrutina 1 (después de 1000ms)
```

#### async

Lanza una corrutina que devuelve un resultado (Deferred):

```kotlin
fun main() = runBlocking {
    val deferred1 = async {
        delay(1000)
        "Resultado 1"
    }

    val deferred2 = async {
        delay(500)
        "Resultado 2"
    }

    println("Esperando resultados...")
    println(deferred1.await()) // Espera y obtiene el resultado
    println(deferred2.await())
}
```

### 3. Ejemplo completo básico

```kotlin
import kotlinx.coroutines.*

suspend fun fetchDataFromServer(id: Int): String {
    println("[${Thread.currentThread().name}] Fetching data $id...")
    delay(1000) // Simula operación de red
    return "Data $id"
}

fun main() = runBlocking {
    println("Programa iniciado")

    // Lanzar múltiples corrutinas
    val job1 = launch {
        val data = fetchDataFromServer(1)
        println("Recibido: $data")
    }

    val job2 = launch {
        val data = fetchDataFromServer(2)
        println("Recibido: $data")
    }

    // Esperar a que terminen
    job1.join()
    job2.join()

    println("Programa finalizado")
}
```

## Contextos y Dispatchers

Los **dispatchers** determinan en qué hilo(s) se ejecutará la corrutina:

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    // Dispatchers.Default: Pool de hilos para CPU intensivo
    launch(Dispatchers.Default) {
        println("Default: ${Thread.currentThread().name}")
        // Cálculos intensivos
    }

    // Dispatchers.IO: Pool de hilos para operaciones I/O
    launch(Dispatchers.IO) {
        println("IO: ${Thread.currentThread().name}")
        // Operaciones de red, archivos, BD
    }

    // Dispatchers.Main: Hilo principal (Android/UI)
    // launch(Dispatchers.Main) { ... }

    // Dispatchers.Unconfined: No confinado a ningún hilo
    launch(Dispatchers.Unconfined) {
        println("Unconfined: ${Thread.currentThread().name}")
    }

    delay(100)
}
```

### Cambiar de contexto con withContext

```kotlin
suspend fun loadData(): String = withContext(Dispatchers.IO) {
    // Ejecuta en hilo de I/O
    println("Cargando en: ${Thread.currentThread().name}")
    delay(1000)
    "Datos cargados"
}

fun main() = runBlocking {
    println("Main: ${Thread.currentThread().name}")
    val data = loadData()
    println("Datos recibidos: $data en ${Thread.currentThread().name}")
}
```

## Structured Concurrency

Las corrutinas siguen el principio de **concurrencia estructurada**: las corrutinas hijas no sobreviven a su padre.

```kotlin
fun main() = runBlocking {
    println("Inicio del scope padre")

    launch {
        println("Corrutina hija 1 iniciada")
        delay(1000)
        println("Corrutina hija 1 completada")
    }

    launch {
        println("Corrutina hija 2 iniciada")
        delay(500)
        println("Corrutina hija 2 completada")
    }

    println("Esperando a las hijas...")
    // runBlocking espera automáticamente a todas las corrutinas hijas
}
```

### CoroutineScope

Define el ciclo de vida de las corrutinas:

```kotlin
class MyService {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    fun doWork() {
        scope.launch {
            // Trabajo asíncrono
            println("Trabajando...")
            delay(1000)
            println("Trabajo completado")
        }
    }

    fun cleanup() {
        scope.cancel() // Cancela todas las corrutinas
    }
}

fun main() = runBlocking {
    val service = MyService()
    service.doWork()
    delay(500)
    service.cleanup() // Cancela la corrutina antes de que termine
    delay(1000)
}
```

## Cancelación de corrutinas

Las corrutinas pueden ser canceladas cooperativamente:

```kotlin
fun main() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("Trabajando $i...")
            delay(500)
        }
    }

    delay(2000)
    println("Cancelando...")
    job.cancel() // Solicita cancelación
    job.join()   // Espera a que termine
    println("Cancelado")
}
```

### Hacer el código cancelable

```kotlin
fun main() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 0
        while (isActive) { // Verifica si está activa
            // Trabajo intensivo
            i++
            if (i % 1_000_000 == 0) {
                println("Trabajando... $i")
            }
        }
    }

    delay(100)
    println("Cancelando...")
    job.cancelAndJoin()
    println("Cancelado después de procesar algunos millones")
}
```

### Finally para limpieza

```kotlin
fun main() = runBlocking {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("Paso $i")
                delay(500)
            }
        } finally {
            println("Limpieza de recursos")
        }
    }

    delay(2000)
    job.cancelAndJoin()
}
```

## Manejo de excepciones

### Try-catch en corrutinas

```kotlin
fun main() = runBlocking {
    val job = launch {
        try {
            delay(1000)
            throw RuntimeException("Error en corrutina")
        } catch (e: Exception) {
            println("Excepción capturada: ${e.message}")
        }
    }

    job.join()
}
```

### CoroutineExceptionHandler

```kotlin
fun main() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Excepción global: ${exception.message}")
    }

    val scope = CoroutineScope(Job() + handler)

    scope.launch {
        throw RuntimeException("Error!")
    }

    delay(100)
}
```

### SupervisorJob

Permite que las corrutinas hijas fallen sin afectar a sus hermanas:

```kotlin
fun main() = runBlocking {
    val supervisor = SupervisorJob()

    with(CoroutineScope(coroutineContext + supervisor)) {
        val child1 = launch {
            delay(100)
            println("Hija 1 completada")
        }

        val child2 = launch {
            delay(50)
            throw RuntimeException("Hija 2 falla")
        }

        val child3 = launch {
            delay(150)
            println("Hija 3 completada") // Se ejecuta aunque child2 falle
        }

        try {
            child2.join()
        } catch (e: Exception) {
            println("Excepción: ${e.message}")
        }

        child1.join()
        child3.join()
    }
}
```

## Operaciones paralelas

### async para paralelismo

```kotlin
suspend fun fetchUser(): User {
    delay(1000)
    return User(1, "Juan")
}

suspend fun fetchPosts(): List<Post> {
    delay(1500)
    return listOf(Post(1, "Post 1"), Post(2, "Post 2"))
}

data class Post(val id: Int, val title: String)

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()

    // Secuencial (lento)
    val user = fetchUser()
    val posts = fetchPosts()
    println("Secuencial: ${System.currentTimeMillis() - startTime}ms") // ~2500ms

    // Paralelo (rápido)
    val startTime2 = System.currentTimeMillis()
    val userDeferred = async { fetchUser() }
    val postsDeferred = async { fetchPosts() }
    val user2 = userDeferred.await()
    val posts2 = postsDeferred.await()
    println("Paralelo: ${System.currentTimeMillis() - startTime2}ms") // ~1500ms
}
```

### awaitAll para múltiples operaciones

```kotlin
fun main() = runBlocking {
    val deferreds = (1..10).map { id ->
        async {
            delay((100..1000).random().toLong())
            "Resultado $id"
        }
    }

    val results = deferreds.awaitAll()
    results.forEach { println(it) }
}
```

## Channels: Comunicación entre corrutinas

Los **channels** permiten que las corrutinas se comuniquen enviando y recibiendo valores:

```kotlin
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    val channel = Channel<Int>()

    // Productor
    launch {
        repeat(5) { i ->
            println("Enviando $i")
            channel.send(i)
            delay(100)
        }
        channel.close() // Importante cerrar el channel
    }

    // Consumidor
    for (value in channel) {
        println("Recibido $value")
    }

    println("Finalizado")
}
```

### Productor-Consumidor con produce

```kotlin
fun CoroutineScope.produceNumbers() = produce {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>) = produce {
    for (x in numbers) {
        send(x * x)
    }
}

fun main() = runBlocking {
    val numbers = produceNumbers()
    val squares = square(numbers)

    repeat(5) {
        println(squares.receive())
    }

    println("Cancelando...")
    coroutineContext.cancelChildren()
}
```

### Buffered Channels

```kotlin
fun main() = runBlocking {
    val channel = Channel<Int>(4) // Buffer de 4 elementos

    launch {
        repeat(10) { i ->
            println("Enviando $i")
            channel.send(i)
        }
        channel.close()
    }

    delay(1000) // El productor puede enviar 4 antes de bloquearse

    for (value in channel) {
        println("Recibido $value")
        delay(200)
    }
}
```

## Flow: Streams asíncronos

**Flow** es un tipo que puede emitir múltiples valores secuencialmente, a diferencia de las funciones suspend que devuelven un solo valor.

```kotlin
import kotlinx.coroutines.flow.*

fun simpleFlow(): Flow<Int> = flow {
    println("Flow iniciado")
    for (i in 1..3) {
        delay(100)
        emit(i) // Emite un valor
    }
}

fun main() = runBlocking {
    println("Llamando a flow...")
    val flow = simpleFlow()

    println("Colectando...")
    flow.collect { value ->
        println("Recibido $value")
    }

    println("Finalizado")
}
```

### Operadores de Flow

```kotlin
fun main() = runBlocking {
    (1..10).asFlow() // Convertir a Flow
        .filter { it % 2 == 0 } // Filtrar
        .map { it * it } // Transformar
        .take(3) // Tomar solo 3
        .collect { value ->
            println(value)
        }
}

// Salida: 4, 16, 36
```

### Flow builders

```kotlin
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    // flowOf
    flowOf(1, 2, 3, 4, 5)
        .collect { println(it) }

    // asFlow
    listOf("a", "b", "c").asFlow()
        .collect { println(it) }

    // flow builder
    flow {
        emit("Inicio")
        delay(100)
        emit("Fin")
    }.collect { println(it) }
}
```

## Ejemplo práctico completo: Sistema de descargas

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

data class DownloadTask(val id: Int, val url: String, val size: Long)
data class DownloadProgress(val taskId: Int, val downloaded: Long, val total: Long) {
    val percentage get() = (downloaded * 100 / total).toInt()
}

class DownloadManager {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun downloadFile(task: DownloadTask): Flow<DownloadProgress> = flow {
        var downloaded = 0L
        val chunkSize = task.size / 10

        while (downloaded < task.size) {
            delay(200) // Simula descarga
            downloaded = minOf(downloaded + chunkSize, task.size)
            emit(DownloadProgress(task.id, downloaded, task.size))
        }
    }.flowOn(Dispatchers.IO)

    fun downloadMultiple(tasks: List<DownloadTask>): Flow<DownloadProgress> = flow {
        tasks.forEach { task ->
            downloadFile(task).collect { progress ->
                emit(progress)
            }
        }
    }

    fun downloadParallel(tasks: List<DownloadTask>): Flow<DownloadProgress> =
        tasks.map { task ->
            downloadFile(task)
        }.merge() // Combina múltiples flows en uno

    fun shutdown() {
        scope.cancel()
    }
}

fun main() = runBlocking {
    val manager = DownloadManager()

    val tasks = listOf(
        DownloadTask(1, "http://example.com/file1.zip", 1000),
        DownloadTask(2, "http://example.com/file2.zip", 2000),
        DownloadTask(3, "http://example.com/file3.zip", 1500)
    )

    println("=== Descarga paralela ===")
    manager.downloadParallel(tasks)
        .collect { progress ->
            println("Tarea ${progress.taskId}: ${progress.percentage}% " +
                    "(${progress.downloaded}/${progress.total})")
        }

    println("\n¡Todas las descargas completadas!")
    manager.shutdown()
}
```

## StateFlow y SharedFlow

### StateFlow: Estado observable

```kotlin
import kotlinx.coroutines.flow.*

class Counter {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        _count.value++
    }

    fun decrement() {
        _count.value--
    }
}

fun main() = runBlocking {
    val counter = Counter()

    // Observar cambios
    launch {
        counter.count.collect { value ->
            println("Contador: $value")
        }
    }

    delay(100)
    counter.increment()
    delay(100)
    counter.increment()
    delay(100)
    counter.decrement()

    delay(100)
}
```

### SharedFlow: Broadcast

```kotlin
class EventBus {
    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    suspend fun postEvent(event: String) {
        _events.emit(event)
    }
}

fun main() = runBlocking {
    val eventBus = EventBus()

    // Múltiples observadores
    launch {
        eventBus.events.collect { event ->
            println("Observador 1: $event")
        }
    }

    launch {
        eventBus.events.collect { event ->
            println("Observador 2: $event")
        }
    }

    delay(100)
    eventBus.postEvent("Evento A")
    delay(100)
    eventBus.postEvent("Evento B")
    delay(100)
}
```

## Mejores prácticas con corrutinas

1. **Usa structured concurrency**: Siempre lanza corrutinas dentro de un scope
2. **Maneja la cancelación**: Verifica `isActive` en loops largos
3. **Usa el dispatcher apropiado**: Default para CPU, IO para I/O
4. **No bloquees en corrutinas**: Usa funciones suspend
5. **Limpia recursos**: Usa `try-finally` o `use`
6. **Evita GlobalScope**: Usa scopes específicos
7. **Testea con TestCoroutineScheduler**: Para tests deterministas

## Conclusión

Las corrutinas de Kotlin proporcionan una forma elegante y eficiente de manejar operaciones asíncronas y concurrentes. Sus principales ventajas son:

- **Simplicidad**: Código asíncrono que parece síncrono
- **Eficiencia**: Miles de corrutinas en pocos hilos
- **Seguridad**: Structured concurrency y manejo de excepciones integrado
- **Flexibilidad**: Channels, Flows, y múltiples dispatchers

Las corrutinas son especialmente útiles para:

- Operaciones de red y I/O
- Procesamiento paralelo de datos
- Aplicaciones con UI (Android)
- Sistemas reactivos

Dominar las corrutinas es esencial para la programación moderna en Kotlin.
