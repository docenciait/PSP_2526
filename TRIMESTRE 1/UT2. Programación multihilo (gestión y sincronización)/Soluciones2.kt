

/**
 * Soluciones — Actividades sobre Corrutinas
 * 
 * Este archivo contiene las soluciones completas y ejecutables para todas las actividades
 * propuestas en Actividades2.md
 * 
 * Para ejecutar cada actividad, descomenta la función correspondiente en el main()
 */

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ============================================
// ACTIVIDAD 1 — Función suspend y delay
// ============================================

suspend fun fetchValue(id: Int): String {
    delay(700)
    return "Value-$id"
}

fun actividad1() = runBlocking {
    println("=== ACTIVIDAD 1 ===")
    val result = fetchValue(1)
    println(result)
}

// ============================================
// ACTIVIDAD 2 — Launch vs async (paralelismo)
// ============================================

suspend fun op(id: Int): String {
    delay(1000)
    return "Result-$id"
}

fun actividad2() = runBlocking {
    println("\n=== ACTIVIDAD 2 ===")
    
    // Versión secuencial
    val startSeq = System.currentTimeMillis()
    val r1 = op(1)
    val r2 = op(2)
    val timeSeq = System.currentTimeMillis() - startSeq
    println("Secuencial: $r1, $r2")
    println("Tiempo secuencial: ${timeSeq}ms")

    // Versión paralela con async
    val startPar = System.currentTimeMillis()
    val d1 = async { op(1) }
    val d2 = async { op(2) }
    val res1 = d1.await()
    val res2 = d2.await()
    val timePar = System.currentTimeMillis() - startPar
    println("Paralelo: $res1, $res2")
    println("Tiempo paralelo: ${timePar}ms")
}

// ============================================
// ACTIVIDAD 3 — Dispatchers y withContext
// ============================================

suspend fun calculateHeavy(): Int = withContext(Dispatchers.Default) {
    println("Calculando en: ${Thread.currentThread().name}")
    var result = 0
    repeat(1_000_000) {
        result += (it % 10)
    }
    result
}

fun actividad3() = runBlocking {
    println("\n=== ACTIVIDAD 3 ===")
    println("Main ejecutándose en: ${Thread.currentThread().name}")
    val result = calculateHeavy()
    println("Resultado: $result")
    println("De vuelta en: ${Thread.currentThread().name}")
}

// ============================================
// ACTIVIDAD 4 — Cancellation cooperativa
// ============================================

fun actividad4() = runBlocking {
    println("\n=== ACTIVIDAD 4 ===")
    val job = launch {
        try {
            var i = 0
            while (isActive) {
                println("Trabajando... $i")
                delay(100)
                i++
            }
        } finally {
            println("Limpieza de recursos")
        }
    }

    delay(1000)
    println("Cancelando...")
    job.cancelAndJoin()
    println("Finalizado")
}

// ============================================
// ACTIVIDAD 5 — Channels (productor-consumidor)
// ============================================

fun actividad5() = runBlocking {
    println("\n=== ACTIVIDAD 5 ===")
    val channel = Channel<Int>()

    // Productor
    launch {
        repeat(10) { i ->
            println("Enviando ${i + 1}")
            channel.send(i + 1)
            delay(50)
        }
        channel.close()
        println("Canal cerrado")
    }

    // Consumidor
    for (value in channel) {
        println("Recibido: $value")
    }

    println("Proceso completado")
}

// ============================================
// ACTIVIDAD 6 — Flow básico y operadores
// ============================================

fun actividad6() = runBlocking {
    println("\n=== ACTIVIDAD 6 ===")
    (1..6).asFlow()
        .filter { it % 2 == 0 }
        .map { it * it }
        .take(2)
        .collect { value ->
            println(value)
        }
}

// ============================================
// ACTIVIDAD 7 — StateFlow y SharedFlow (observadores)
// ============================================

class Counter {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        _count.value++
    }
}

fun actividad7() = runBlocking {
    println("\n=== ACTIVIDAD 7 ===")
    val counter = Counter()

    // Observador
    launch {
        counter.count.collect { value ->
            println("Contador: $value")
        }
    }

    delay(100) // Asegurar que el observador esté listo

    // Incrementador
    launch {
        repeat(3) {
            delay(150)
            counter.increment()
        }
    }

    delay(600) // Esperar a que termine
}

// ============================================
// ACTIVIDAD 8 — SupervisorJob y manejo de fallos
// ============================================

fun actividad8() = runBlocking {
    println("\n=== ACTIVIDAD 8 ===")
    val supervisor = SupervisorJob()

    with(CoroutineScope(coroutineContext + supervisor)) {
        val child1 = launch {
            delay(100)
            println("Hija 1 completada")
        }

        val child2 = launch {
            delay(50)
            println("Hija 2 va a fallar")
            throw RuntimeException("Error en hija 2")
        }

        val child3 = launch {
            delay(150)
            println("Hija 3 completada")
        }

        try {
            child2.join()
        } catch (e: Exception) {
            println("Capturada excepción: ${e.message}")
        }

        child1.join()
        child3.join()
    }

    println("Todas las corrutinas gestionadas")
}

// ============================================
// PRÁCTICA A — Llamadas de red paralelas
// ============================================

data class User(val id: Int, val name: String)
data class Permissions(val items: List<String>)
data class UserWithPermissions(val user: User, val permissions: Permissions)

suspend fun fetchUser(id: Int): User {
    delay(800)
    return User(id, "User$id")
}

suspend fun fetchPermissions(id: Int): Permissions {
    delay(600)
    return Permissions(listOf("READ", "WRITE", "ADMIN"))
}

fun practicaA() = runBlocking {
    println("\n=== PRÁCTICA A ===")
    val startTime = System.currentTimeMillis()

    supervisorScope {
        val userDeferred = async { fetchUser(1) }
        val permissionsDeferred = async { fetchPermissions(1) }

        try {
            val user = userDeferred.await()
            val permissions = permissionsDeferred.await()
            val combined = UserWithPermissions(user, permissions)

            println("Datos combinados: $combined")
            println("Tiempo total: ${System.currentTimeMillis() - startTime}ms")
        } catch (e: Exception) {
            println("Error al obtener datos: ${e.message}")
        }
    }
}

// ============================================
// PRÁCTICA B — Procesamiento paralelo (CPU-bound)
// ============================================

suspend fun processImage(id: Int): Int = withContext(Dispatchers.Default) {
    println("[${Thread.currentThread().name}] Procesando imagen $id")
    var result = 0
    repeat(50_000) {
        result += (it % 3)
    }
    id * 2
}

fun practicaB() = runBlocking {
    println("\n=== PRÁCTICA B ===")
    val images = (1..8).toList()
    val startTime = System.currentTimeMillis()

    val deferred = images.map { id ->
        async { processImage(id) }
    }

    val results = deferred.awaitAll()

    val endTime = System.currentTimeMillis()

    println("Resultados: $results")
    println("Tiempo total: ${endTime - startTime}ms")
}

// ============================================
// PRÁCTICA C — Progreso via Flow
// ============================================

fun progressFlow(): Flow<Int> = flow {
    for (progress in 0..100 step 10) {
        delay(150)
        emit(progress)
    }
}.flowOn(Dispatchers.IO)

fun practicaC() = runBlocking {
    println("\n=== PRÁCTICA C ===")
    println("Iniciando descarga...")

    progressFlow().collect { progress ->
        val bar = "=".repeat(progress / 10) + " ".repeat(10 - progress / 10)
        println("[$bar] $progress%")
    }

    println("Descarga completada!")
}

// ============================================
// PRÁCTICA D — Bridge para APIs callback
// ============================================

class DownloadCall {
    var isCancelled = false

    fun cancel() {
        isCancelled = true
        println("Llamada cancelada")
    }
}

fun startDownload(url: String, callback: (ByteArray?, Exception?) -> Unit): DownloadCall {
    val call = DownloadCall()

    Thread {
        Thread.sleep(1000)
        if (!call.isCancelled) {
            callback("Data from $url".toByteArray(), null)
        }
    }.start()

    return call
}

suspend fun downloadSuspending(url: String): ByteArray = suspendCancellableCoroutine { cont ->
    val call = startDownload(url) { data, error ->
        if (error != null) {
            cont.resumeWithException(error)
        } else if (data != null) {
            cont.resume(data)
        }
    }

    cont.invokeOnCancellation {
        call.cancel()
    }
}

fun practicaD() = runBlocking {
    println("\n=== PRÁCTICA D ===")
    try {
        println("Iniciando descarga...")
        val data = downloadSuspending("http://example.com/file.zip")
        println("Descargado: ${String(data)}")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

// ============================================
// PRÁCTICA E — Servicio con CoroutineScope
// ============================================

class WorkerService {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var tickCount = 0

    fun start() {
        println("Servicio iniciado")
        scope.launch {
            while (isActive) {
                println("Tick ${++tickCount}")
                delay(500)
            }
        }
    }

    fun stop() {
        println("Deteniendo servicio...")
        scope.cancel()
        println("Servicio detenido")
    }

    fun isRunning(): Boolean = scope.isActive
}

fun practicaE() = runBlocking {
    println("\n=== PRÁCTICA E ===")
    val service = WorkerService()

    service.start()
    delay(1200)

    println("Estado activo: ${service.isRunning()}")

    service.stop()
    delay(300)

    println("Estado activo: ${service.isRunning()}")
}

// ============================================
// FUNCIÓN MAIN
// ============================================

fun main() {
    println("========================================")
    println("SOLUCIONES ACTIVIDADES CORRUTINAS")
    println("========================================")
    
    // Descomenta las actividades que quieras ejecutar:
    
    actividad1()
    actividad2()
    actividad3()
    actividad4()
    actividad5()
    actividad6()
    actividad7()
    actividad8()
    
    // Prácticas reales
    practicaA()
    practicaB()
    practicaC()
    practicaD()
    practicaE()
    
    println("\n========================================")
    println("FIN DE LAS SOLUCIONES")
    println("========================================")
}
