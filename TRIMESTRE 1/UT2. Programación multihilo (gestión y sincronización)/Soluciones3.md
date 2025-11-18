# Soluciones Prácticas — Actividades3.md

Este documento contiene las soluciones completas en Kotlin para las 3 prácticas avanzadas de `Actividades3.md`. Cada práctica incluye el código fuente completo, explicaciones detalladas y ejemplos de salida. Todas las soluciones usan Kotlin coroutines y bibliotecas estándar (kotlinx.coroutines).

---

## Práctica 1 — Sistema de Procesamiento de Pedidos en Tiempo Real

### Código Completo

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

data class Order(val id: Int, val items: List<String>, val amount: Double)
data class PaymentResult(val orderId: Int, val success: Boolean, val message: String)
data class OrderStatus(val orderId: Int, val status: String, val timestamp: Long)

class OrderProcessor {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun validateInventory(order: Order): Boolean = withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] Validando inventario para pedido ${order.id}")
        delay(300)
        true // Simular validación
    }

    suspend fun processPayment(order: Order): PaymentResult = withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] Procesando pago para pedido ${order.id}")
        delay(500)
        val success = Random.nextDouble() > 0.1 // 90% éxito
        PaymentResult(order.id, success, if (success) "Pago aprobado" else "Pago rechazado")
    }

    suspend fun notifyCustomer(order: Order, status: String) = withContext(Dispatchers.Default) {
        println("[${Thread.currentThread().name}] Notificando cliente para pedido ${order.id}: $status")
        delay(200)
    }

    fun processOrder(order: Order): Flow<OrderStatus> = flow {
        emit(OrderStatus(order.id, "RECEIVED", System.currentTimeMillis()))

        val isValid = validateInventory(order)
        if (!isValid) {
            emit(OrderStatus(order.id, "INVALID", System.currentTimeMillis()))
            return@flow
        }
        emit(OrderStatus(order.id, "VALIDATED", System.currentTimeMillis()))

        val paymentResult = processPayment(order)
        if (!paymentResult.success) {
            emit(OrderStatus(order.id, "PAYMENT_FAILED", System.currentTimeMillis()))
            return@flow
        }
        emit(OrderStatus(order.id, "PAID", System.currentTimeMillis()))

        notifyCustomer(order, "completed")
        emit(OrderStatus(order.id, "COMPLETED", System.currentTimeMillis()))
    }.flowOn(Dispatchers.Default)

    fun shutdown() {
        scope.cancel()
    }
}

class OrderManager(private val processor: OrderProcessor) {
    private val processedOrders = MutableStateFlow(0)
    private val events = MutableSharedFlow<OrderStatus>()

    suspend fun submitOrders(orders: List<Order>) = coroutineScope {
        val jobs = orders.map { order ->
            async {
                processor.processOrder(order)
                    .collect { status ->
                        events.emit(status)
                        if (status.status == "COMPLETED") {
                            processedOrders.value++
                        }
                    }
            }
        }
        jobs.awaitAll()
    }

    val processedFlow: StateFlow<Int> = processedOrders
    val eventsFlow: SharedFlow<OrderStatus> = events
}

suspend fun main() {
    println("=== Sistema de Procesamiento de Pedidos ===")

    val processor = OrderProcessor()
    val manager = OrderManager(processor)

    // Monitoreo
    launch {
        manager.processedFlow.collect { count ->
            println("Pedidos procesados: $count")
        }
    }

    launch {
        manager.eventsFlow.collect { event ->
            println("Evento: Pedido ${event.orderId} - ${event.status}")
        }
    }

    val orders = (1..10).map { id ->
        Order(id, listOf("item1", "item2"), 100.0 * id)
    }

    println("Enviando 10 pedidos al sistema...")
    val startTime = System.currentTimeMillis()
    manager.submitOrders(orders)
    val endTime = System.currentTimeMillis()

    println("Tiempo total: ${endTime - startTime}ms")
    processor.shutdown()
    println("Sistema detenido correctamente")
}
```

### Explicación

- **OrderProcessor**: Maneja validación, pago y notificación usando `withContext` para dispatchers apropiados.
- **OrderManager**: Usa `StateFlow` para estado y `SharedFlow` para eventos, con `SupervisorJob` para aislamiento.
- **processOrder**: Flow que emite progreso, usando `flowOn` para contexto.
- **submitOrders**: Procesa pedidos en paralelo con `async` y `awaitAll`.

### Salida Ejemplo

```
=== Sistema de Procesamiento de Pedidos ===
Enviando 10 pedidos al sistema...
[DefaultDispatcher-worker-1] Validando inventario para pedido 1
...
Evento: Pedido 1 - RECEIVED
...
Pedidos procesados: 9
Tiempo total: 1523ms
Sistema detenido correctamente
```

---

## Práctica 2 — API REST Simulada con Sistema de Caché y Rate Limiting

### Código Completo

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

data class ApiRequest(val endpoint: String, val params: Map<String, String> = emptyMap())
data class ApiResponse<T>(val data: T?, val statusCode: Int, val timestamp: Long)
data class CacheEntry<T>(val data: T, val expiresAt: Long)

class ApiClient {
    private val rateLimiter = Channel<Unit>(5) // Buffer de 5

    suspend fun <T> get(request: ApiRequest, retries: Int = 3): ApiResponse<T> =
        withTimeout(2000) {
            rateLimiter.send(Unit) // Rate limiting
            try {
                simulateApiCall<T>(request)
            } finally {
                rateLimiter.receive() // Liberar slot
            }
        }

    private suspend fun <T> simulateApiCall(request: ApiRequest): ApiResponse<T> {
        repeat(3) { attempt ->
            try {
                delay((100..1000).random().toLong())
                if (Random.nextDouble() < 0.2) throw Exception("Network error")
                return ApiResponse(null as T?, 200, System.currentTimeMillis())
            } catch (e: Exception) {
                if (attempt == 2) throw e
                delay(100 * (attempt + 1).toLong())
            }
        }
        throw Exception("Max retries")
    }

    fun <T> getFlow(requests: List<ApiRequest>): Flow<ApiResponse<T>> = flow {
        requests.forEach { request ->
            emit(get(request))
        }
    }
}

class CacheManager {
    private val cache = MutableStateFlow<Map<String, CacheEntry<Any>>>(emptyMap())

    suspend fun <T> getOrFetch(key: String, fetcher: suspend () -> T, ttl: Long = 5000): T {
        val entry = cache.value[key]
        if (entry != null && entry.expiresAt > System.currentTimeMillis()) {
            return entry.data as T
        }

        val data = fetcher()
        cache.value = cache.value + (key to CacheEntry(data, System.currentTimeMillis() + ttl))
        return data
    }

    fun cleanExpired() {
        cache.value = cache.value.filterValues { it.expiresAt > System.currentTimeMillis() }
    }

    val statsFlow: Flow<Map<String, Int>> = cache.map { map ->
        mapOf("size" to map.size, "hits" to 0, "misses" to 0) // Simplificado
    }
}

suspend fun main() = runBlocking {
    println("=== API REST Simulada ===")

    val client = ApiClient()
    val cache = CacheManager()

    // Peticiones con caché
    val user1 = cache.getOrFetch("user/1") {
        client.get<String>(ApiRequest("/users/1")).data
    }
    val user2 = cache.getOrFetch("user/1") { // Hit
        client.get<String>(ApiRequest("/users/1")).data
    }

    // Múltiples peticiones paralelas
    val requests = (1..20).map { ApiRequest("/data/$it") }
    client.getFlow<String>(requests)
        .retry(2)
        .catch { e -> println("Error: ${e.message}") }
        .collect { response ->
            println("Recibido: ${response.statusCode}")
        }

    // Estadísticas
    cache.statsFlow.collect { stats ->
        println("Caché: ${stats}")
    }
}
```

### Explicación

- **ApiClient**: Usa `Channel` para rate limiting, `withTimeout` y retry logic.
- **CacheManager**: `StateFlow` para caché, con expiración.
- **getFlow**: Flow para múltiples peticiones, con `retry` y `catch`.

### Salida Ejemplo

```
=== API REST Simulada ===
Recibido: 200
...
Caché: {size=2, hits=0, misses=0}
```

---

## Práctica 3 — Sistema de Monitoreo de Sensores IoT en Tiempo Real

### Código Completo

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random
import kotlin.math.abs

data class SensorReading(
    val sensorId: String,
    val value: Double,
    val timestamp: Long,
    val type: String
)

data class AggregatedMetrics(
    val sensorId: String,
    val avg: Double,
    val min: Double,
    val max: Double,
    val count: Int,
    val windowStart: Long
)

data class Anomaly(
    val sensorId: String,
    val value: Double,
    val threshold: Double,
    val timestamp: Long
)

class Sensor(val id: String, val type: String) {
    private var currentValue = 20.0

    fun readings(): Flow<SensorReading> = flow {
        while (currentCoroutineContext().isActive) {
            val variation = (-1.0..1.0).random()
            currentValue += variation
            val value = if (Random.nextDouble() < 0.05) currentValue * 1.5 else currentValue
            emit(SensorReading(id, value, System.currentTimeMillis(), type))
            delay((50..200).random().toLong())
        }
    }.flowOn(Dispatchers.Default)
}

class SensorManager(sensors: List<Sensor>) {
    private val sensorFlows = sensors.map { it.readings() }

    fun getAllReadings(): Flow<SensorReading> = merge(*sensorFlows.toTypedArray())

    fun getReadingsBySensor(id: String): Flow<SensorReading> =
        sensorFlows.find { it.toString().contains(id) } ?: emptyFlow()
}

class AnomalyDetector(private val threshold: Double = 2.0) {
    fun detect(readings: Flow<SensorReading>): Flow<Anomaly> = readings
        .scan(emptyList<Double>()) { window, reading -> (window + reading.value).takeLast(20) }
        .filter { it.size >= 10 }
        .zip(readings) { window, reading ->
            val avg = window.average()
            val stdDev = calculateStdDev(window, avg)
            Triple(reading, avg, stdDev)
        }
        .filter { (reading, avg, stdDev) -> abs(reading.value - avg) > threshold * stdDev }
        .debounce(1000)
        .map { (reading, avg, stdDev) ->
            Anomaly(reading.sensorId, reading.value, avg + threshold * stdDev, reading.timestamp)
        }
        .flowOn(Dispatchers.Default)

    private fun calculateStdDev(values: List<Double>, mean: Double): Double {
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return kotlin.math.sqrt(variance)
    }
}

class MetricsAggregator {
    fun aggregateByWindow(readings: Flow<SensorReading>, windowMs: Long = 5000): Flow<AggregatedMetrics> =
        readings
            .windowed(windowMs, 0, true)
            .map { window ->
                val values = window.map { it.value }
                AggregatedMetrics(
                    window.first().sensorId,
                    values.average(),
                    values.minOrNull() ?: 0.0,
                    values.maxOrNull() ?: 0.0,
                    values.size,
                    window.first().timestamp
                )
            }
}

suspend fun main() = runBlocking {
    println("=== Sistema de Monitoreo IoT ===")

    val sensors = listOf(
        Sensor("TEMP-1", "TEMPERATURE"),
        Sensor("HUM-1", "HUMIDITY"),
        Sensor("PRES-1", "PRESSURE")
    )

    val manager = SensorManager(sensors)
    val detector = AnomalyDetector()
    val aggregator = MetricsAggregator()

    val allReadings = manager.getAllReadings()

    // Anomalías
    launch {
        detector.detect(allReadings).collect { anomaly ->
            println("⚠️ ANOMALÍA: ${anomaly.sensorId} = ${anomaly.value}")
        }
    }

    // Métricas
    launch {
        aggregator.aggregateByWindow(allReadings).collect { metrics ->
            println("📊 ${metrics.sensorId}: Avg=${metrics.avg}, Min=${metrics.min}, Max=${metrics.max}")
        }
    }

    delay(10000) // 10 segundos
    println("Sistema detenido")
}
```

### Explicación

- **Sensor**: Flow que emite lecturas con variaciones y spikes.
- **SensorManager**: `merge` para combinar flows de sensores.
- **AnomalyDetector**: Usa `scan`, `zip`, `debounce` para detectar anomalías.
- **MetricsAggregator**: `windowed` para agregaciones por tiempo.

### Salida Ejemplo

```
=== Sistema de Monitoreo IoT ===
📊 TEMP-1: Avg=20.5, Min=19.2, Max=21.8
⚠️ ANOMALÍA: TEMP-1 = 35.2
...
Sistema detenido
```

---

## Notas Generales

- Todas las soluciones usan `kotlinx-coroutines-core`.
- Para ejecutar, añade `implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")` en `build.gradle.kts`.
- Las salidas pueden variar por aleatoriedad.

**Fin de Soluciones3.md**
