# Actividades Avanzadas — Prácticas Completas de Corrutinas

Este documento contiene 3 prácticas completas y realistas que cubren todos los conceptos del documento `3_CorrutinasKotlin.md`. Cada práctica incluye enunciado detallado, requisitos técnicos, guía de implementación y criterios de evaluación.

---

## Práctica 1 — Sistema de Procesamiento de Pedidos en Tiempo Real

### Descripción

Implementa un sistema completo de procesamiento de pedidos que simule una aplicación de e-commerce. El sistema debe gestionar múltiples pedidos concurrentemente, validar inventario, procesar pagos y notificar a los clientes, todo usando corrutinas de Kotlin.

### Objetivos de aprendizaje

Esta práctica cubre:

- ✅ Funciones `suspend` y `delay`
- ✅ Builders: `runBlocking`, `launch`, `async`
- ✅ Dispatchers (Default, IO, Unconfined)
- ✅ `withContext` para cambio de contexto
- ✅ Structured Concurrency y CoroutineScope
- ✅ Cancelación cooperativa
- ✅ Manejo de excepciones con `CoroutineExceptionHandler`
- ✅ `SupervisorJob` para aislamiento de fallos
- ✅ Channels para comunicación
- ✅ Flow para streams de datos
- ✅ StateFlow para estado observable

### Requisitos técnicos

El sistema debe incluir:

1. **Modelo de datos:**

   ```kotlin
   data class Order(val id: Int, val items: List<String>, val amount: Double)
   data class PaymentResult(val orderId: Int, val success: Boolean, val message: String)
   data class OrderStatus(val orderId: Int, val status: String, val timestamp: Long)
   ```

2. **OrderProcessor class:**

   - Debe tener su propio `CoroutineScope`
   - Método `suspend fun validateInventory(order: Order): Boolean` (usa `Dispatchers.IO`, delay 300ms)
   - Método `suspend fun processPayment(order: Order): PaymentResult` (usa `Dispatchers.IO`, delay 500ms, 10% probabilidad fallo)
   - Método `suspend fun notifyCustomer(order: Order, status: String)` (usa `Dispatchers.Default`, delay 200ms)
   - Método `fun processOrder(order: Order): Flow<OrderStatus>` que emita el progreso del pedido
   - Método `fun shutdown()` para limpiar recursos

3. **OrderManager class:**

   - Usa `StateFlow<Int>` para rastrear pedidos procesados
   - Usa `SharedFlow<OrderStatus>` para broadcast de eventos
   - Método `suspend fun submitOrders(orders: List<Order>)` que procese todos en paralelo
   - Usa `SupervisorJob` para que un fallo no cancele otros pedidos

4. **Sistema de monitoreo:**
   - Channel para cola de pedidos pendientes
   - Flow que emita estadísticas cada segundo (total procesados, exitosos, fallidos)

### Guía de implementación

**Paso 1:** Crear las clases de datos y `OrderProcessor`

```kotlin
class OrderProcessor {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun validateInventory(order: Order): Boolean = withContext(Dispatchers.IO) {
        println("[${Thread.currentThread().name}] Validando inventario para pedido ${order.id}")
        delay(300)
        true // Simular validación
    }

    // Implementar resto de métodos...

    fun shutdown() {
        scope.cancel()
    }
}
```

**Paso 2:** Implementar `processOrder` con Flow

```kotlin
fun processOrder(order: Order): Flow<OrderStatus> = flow {
    emit(OrderStatus(order.id, "RECEIVED", System.currentTimeMillis()))

    // Validación
    val isValid = validateInventory(order)
    if (!isValid) {
        emit(OrderStatus(order.id, "INVALID", System.currentTimeMillis()))
        return@flow
    }
    emit(OrderStatus(order.id, "VALIDATED", System.currentTimeMillis()))

    // Pago
    val paymentResult = processPayment(order)
    if (!paymentResult.success) {
        emit(OrderStatus(order.id, "PAYMENT_FAILED", System.currentTimeMillis()))
        return@flow
    }
    emit(OrderStatus(order.id, "PAID", System.currentTimeMillis()))

    // Notificación
    notifyCustomer(order, "completed")
    emit(OrderStatus(order.id, "COMPLETED", System.currentTimeMillis()))
}.flowOn(Dispatchers.Default)
```

**Paso 3:** Crear `OrderManager` con StateFlow/SharedFlow

**Paso 4:** Implementar main con procesamiento paralelo de múltiples pedidos

**Paso 5:** Añadir sistema de monitoreo con Channel y estadísticas

### Ejemplo de salida esperada

```
=== Sistema de Procesamiento de Pedidos ===
Enviando 10 pedidos al sistema...

[DefaultDispatcher-worker-1] Validando inventario para pedido 1
[DefaultDispatcher-worker-2] Validando inventario para pedido 2
...
Pedido 1: RECEIVED
Pedido 1: VALIDATED
Pedido 1: PAID
Pedido 1: COMPLETED
...
Estadísticas: Total=10, Exitosos=9, Fallidos=1
Tiempo total: 1523ms

Sistema detenido correctamente
```

### Criterios de evaluación

- ✅ Uso correcto de dispatchers según tipo de operación
- ✅ Implementación de structured concurrency
- ✅ Manejo apropiado de excepciones con SupervisorJob
- ✅ Uso de Flow para progreso y Channel para comunicación
- ✅ StateFlow/SharedFlow para estado observable
- ✅ Cancelación limpia de recursos
- ✅ Procesamiento paralelo eficiente

---

## Práctica 2 — API REST Simulada con Sistema de Caché y Rate Limiting

### Descripción

Desarrolla un cliente HTTP simulado que gestione peticiones a múltiples endpoints, implemente un sistema de caché con expiración, rate limiting, retry logic y manejo robusto de errores, todo usando corrutinas y Flow.

### Objetivos de aprendizaje

Esta práctica cubre:

- ✅ `async`/`await` para operaciones paralelas
- ✅ `withContext` y dispatchers IO
- ✅ Flow avanzado (operadores, transformaciones)
- ✅ StateFlow para caché
- ✅ `flowOn` para cambio de contexto en flows
- ✅ Cancelación con timeout
- ✅ `retry` y manejo de errores
- ✅ `merge`, `combine`, `zip` de flows
- ✅ Buffered channels
- ✅ `suspendCancellableCoroutine` para integración con callbacks

### Requisitos técnicos

1. **Modelo de datos:**

   ```kotlin
   data class ApiRequest(val endpoint: String, val params: Map<String, String> = emptyMap())
   data class ApiResponse<T>(val data: T?, val statusCode: Int, val timestamp: Long)
   data class CacheEntry<T>(val data: T, val expiresAt: Long)
   ```

2. **ApiClient class:**

   - Método `suspend fun <T> get(request: ApiRequest): ApiResponse<T>` con retry logic (max 3 intentos)
   - Método `fun <T> getFlow(requests: List<ApiRequest>): Flow<ApiResponse<T>>` para múltiples peticiones
   - Rate limiting: máximo 5 peticiones concurrentes
   - Timeout de 2 segundos por petición
   - Simulación de latencia variable (100-1000ms)
   - 20% probabilidad de fallo simulado

3. **CacheManager class:**

   - Usa `MutableStateFlow<Map<String, CacheEntry<Any>>>` para almacenar caché
   - Método `suspend fun <T> getOrFetch(key: String, fetcher: suspend () -> T, ttl: Long): T`
   - Método `fun cleanExpired()` para limpiar entradas expiradas
   - Flow que emita estadísticas de caché (hits, misses, tamaño)

4. **RateLimiter class:**

   - Usa Channel con buffer para controlar concurrencia
   - Método `suspend fun <T> execute(block: suspend () -> T): T`
   - Estadísticas: peticiones aceptadas, rechazadas, tiempo de espera promedio

5. **Sistema de agregación:**
   - Combinar datos de múltiples endpoints usando `combine` o `zip`
   - Flow que procese respuestas y emita resultados agregados
   - Manejo de fallos parciales (algún endpoint falla pero otros continúan)

### Guía de implementación

**Paso 1:** Implementar `ApiClient` básico con retry

```kotlin
class ApiClient {
    private val rateLimiter = RateLimiter(maxConcurrent = 5)

    suspend fun <T> get(request: ApiRequest, retries: Int = 3): ApiResponse<T> =
        rateLimiter.execute {
            withContext(Dispatchers.IO) {
                repeat(retries) { attempt ->
                    try {
                        return@withContext withTimeout(2000) {
                            simulateApiCall(request)
                        }
                    } catch (e: Exception) {
                        if (attempt == retries - 1) throw e
                        delay(100 * (attempt + 1))
                    }
                }
                throw Exception("Max retries exceeded")
            }
        }

    private suspend fun <T> simulateApiCall(request: ApiRequest): ApiResponse<T> {
        delay((100..1000).random().toLong())
        if (Random.nextDouble() < 0.2) throw Exception("Network error")
        // Simular respuesta...
    }
}
```

**Paso 2:** Implementar `CacheManager` con StateFlow

**Paso 3:** Crear `RateLimiter` con Channel

**Paso 4:** Implementar flows para múltiples peticiones paralelas

**Paso 5:** Agregar sistema de monitoreo y estadísticas

### Ejemplo de uso

```kotlin
fun main() = runBlocking {
    val client = ApiClient()
    val cache = CacheManager()

    // Peticiones individuales con caché
    val user = cache.getOrFetch("user/1") {
        client.get<User>(ApiRequest("/users/1")).data!!
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
        println("Caché - Hits: ${stats.hits}, Misses: ${stats.misses}")
    }
}
```

### Criterios de evaluación

- ✅ Rate limiting funcional con Channel
- ✅ Sistema de caché con expiración usando StateFlow
- ✅ Retry logic con exponential backoff
- ✅ Timeout handling correcto
- ✅ Procesamiento paralelo eficiente con Flow
- ✅ Manejo robusto de errores (parciales y totales)
- ✅ Estadísticas en tiempo real con Flow

---

## Práctica 3 — Sistema de Monitoreo de Sensores IoT en Tiempo Real

### Descripción

Implementa un sistema completo de monitoreo que simule la recolección de datos de múltiples sensores IoT, procese streams de datos en tiempo real, detecte anomalías, agregue métricas y persista datos, utilizando todas las capacidades avanzadas de corrutinas y Flow.

### Objetivos de aprendizaje

Esta práctica cubre TODOS los conceptos:

- ✅ Todas las características de Flow (operadores, transformaciones, buffering)
- ✅ StateFlow y SharedFlow avanzado
- ✅ Hot y Cold flows
- ✅ `produce` para channels
- ✅ `callbackFlow` para integración con callbacks
- ✅ Operadores avanzados: `debounce`, `conflate`, `buffer`, `scan`
- ✅ `combine`, `zip`, `merge` de múltiples flows
- ✅ Backpressure handling
- ✅ Scope personalizado con lifecycle
- ✅ Structured concurrency compleja

### Requisitos técnicos

1. **Modelo de datos:**

   ```kotlin
   data class SensorReading(
       val sensorId: String,
       val value: Double,
       val timestamp: Long,
       val type: SensorType
   )

   enum class SensorType { TEMPERATURE, HUMIDITY, PRESSURE, MOTION }

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
   ```

2. **Sensor class:**

   - Método `fun readings(): Flow<SensorReading>` que emita lecturas cada 50-200ms
   - Simulación realista con valores que varían gradualmente
   - Ocasionales "spikes" para simular anomalías (5% probabilidad)
   - Usa `callbackFlow` o `flow` builder

3. **SensorManager class:**

   - Gestiona múltiples sensores (mínimo 5)
   - Método `fun getAllReadings(): Flow<SensorReading>` que combine todos los sensores
   - Método `fun getReadingsBySensor(id: String): Flow<SensorReading>`
   - Usa `merge` o `combine` para unificar streams

4. **AnomalyDetector class:**

   - Procesa flow de lecturas y detecta anomalías
   - Usa `scan` para mantener ventana móvil de valores
   - Calcula desviación estándar y detecta valores fuera de 2σ
   - Emite flow de anomalías detectadas
   - Implementa debounce para evitar alertas duplicadas

5. **MetricsAggregator class:**

   - Agrega métricas en ventanas de tiempo (por ejemplo, cada 5 segundos)
   - Usa operadores de Flow para calcular estadísticas
   - `StateFlow` para métricas actuales
   - Método `fun aggregateByWindow(windowMs: Long): Flow<AggregatedMetrics>`

6. **DataPersistence class:**

   - Channel buffer para persistencia asíncrona
   - Método `suspend fun save(reading: SensorReading)` que use `Dispatchers.IO`
   - Batch writes (agrupa escrituras cada 1 segundo o cada 100 lecturas)
   - Manejo de backpressure con buffer strategy

7. **Dashboard (UI simulado):**

   - Usa `SharedFlow` para broadcast de eventos a múltiples "clientes"
   - `StateFlow` para estado actual de cada sensor
   - Actualizaciones en tiempo real
   - Manejo de conflation para UI que no puede seguir el ritmo

8. **Sistema completo:**
   - Lifecycle management con scope personalizado
   - Graceful shutdown con cancelación limpia
   - Manejo de excepciones con recovery
   - Logging detallado con contexto de corrutina

### Guía de implementación

**Paso 1:** Crear Sensor con Flow

```kotlin
class Sensor(val id: String, val type: SensorType) {
    private var currentValue = when (type) {
        SensorType.TEMPERATURE -> 20.0
        SensorType.HUMIDITY -> 50.0
        SensorType.PRESSURE -> 1013.0
        SensorType.MOTION -> 0.0
    }

    fun readings(): Flow<SensorReading> = flow {
        while (currentCoroutineContext().isActive) {
            // Variación gradual
            val variation = (-1.0..1.0).random()
            currentValue += variation

            // Ocasional spike (anomalía)
            val value = if (Random.nextDouble() < 0.05) {
                currentValue * (1.5 + Random.nextDouble())
            } else {
                currentValue
            }

            emit(SensorReading(id, value, System.currentTimeMillis(), type))
            delay((50..200).random().toLong())
        }
    }.flowOn(Dispatchers.Default)
}
```

**Paso 2:** Implementar SensorManager con merge de flows

**Paso 3:** Crear AnomalyDetector con scan y operadores avanzados

```kotlin
class AnomalyDetector(private val threshold: Double = 2.0) {
    fun detect(readings: Flow<SensorReading>): Flow<Anomaly> = readings
        .scan(emptyList<Double>()) { window, reading ->
            (window + reading.value).takeLast(20) // Ventana móvil de 20 valores
        }
        .filter { window -> window.size >= 10 } // Esperar ventana mínima
        .zip(readings) { window, reading ->
            val avg = window.average()
            val stdDev = calculateStdDev(window, avg)
            Triple(reading, avg, stdDev)
        }
        .filter { (reading, avg, stdDev) ->
            abs(reading.value - avg) > threshold * stdDev
        }
        .debounce(1000) // Evitar alertas duplicadas
        .map { (reading, avg, stdDev) ->
            Anomaly(reading.sensorId, reading.value, avg + threshold * stdDev, reading.timestamp)
        }
        .flowOn(Dispatchers.Default)
}
```

**Paso 4:** Implementar MetricsAggregator con ventanas de tiempo

**Paso 5:** Crear DataPersistence con buffered channel

**Paso 6:** Implementar Dashboard con SharedFlow/StateFlow

**Paso 7:** Integrar todo en sistema completo con lifecycle

### Ejemplo de uso completo

```kotlin
fun main() = runBlocking {
    val system = IoTMonitoringSystem()

    system.start()

    // Suscribirse a eventos
    launch {
        system.anomalies.collect { anomaly ->
            println("⚠️ ANOMALÍA: Sensor ${anomaly.sensorId} = ${anomaly.value}")
        }
    }

    launch {
        system.metrics.collect { metrics ->
            println("📊 Métricas: ${metrics.sensorId} - Avg: ${metrics.avg}")
        }
    }

    launch {
        system.dashboard.collect { state ->
            println("🖥️ Dashboard actualizado: $state")
        }
    }

    delay(30000) // Ejecutar 30 segundos

    println("Deteniendo sistema...")
    system.shutdown()
    println("Sistema detenido")
}
```

### Ejemplo de salida esperada

```
=== Sistema de Monitoreo IoT ===
Iniciando 5 sensores...
Sensor TEMP-1 iniciado (TEMPERATURE)
Sensor HUM-1 iniciado (HUMIDITY)
...

📊 Métricas: TEMP-1 - Avg: 20.5, Min: 19.2, Max: 21.8 [ventana: 5s]
⚠️ ANOMALÍA: Sensor TEMP-1 = 35.2 (threshold: 25.0)
🖥️ Dashboard: [TEMP-1: 20.5°C] [HUM-1: 52%] [PRES-1: 1013 hPa]
💾 Persistencia: Guardadas 120 lecturas en batch

Estadísticas finales:
- Total lecturas: 2,450
- Anomalías detectadas: 12
- Tiempo activo: 30s
- Throughput: 81.6 lecturas/s

Deteniendo sistema...
Sistema detenido correctamente
```

### Criterios de evaluación

- ✅ Implementación correcta de hot/cold flows
- ✅ Uso avanzado de operadores (scan, debounce, conflate, buffer)
- ✅ Combine/merge de múltiples flows
- ✅ StateFlow y SharedFlow correctamente implementados
- ✅ Manejo de backpressure
- ✅ Lifecycle management robusto
- ✅ Graceful shutdown sin pérdida de datos
- ✅ Performance: manejo eficiente de alto throughput
- ✅ Código limpio, modular y bien documentado

---

## Entrega

Para cada práctica, debes entregar:

1. **Código fuente completo** en archivos `.kt` separados
2. **README.md** con:
   - Instrucciones de ejecución
   - Decisiones de diseño
   - Diagramas (opcional pero recomendado)
3. **Tests** (opcional pero muy valorado)
4. **Captura de salida** mostrando el sistema funcionando
5. **build.gradle.kts** con dependencias necesarias

### Dependencias necesarias

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
```

---

## Recursos adicionales

- Documentación oficial: https://kotlinlang.org/docs/coroutines-guide.html
- Kotlinx Coroutines: https://github.com/Kotlin/kotlinx.coroutines
- Flow documentation: https://kotlinlang.org/docs/flow.html

---

**Nota:** Estas prácticas están diseñadas para ser progresivamente más complejas. Se recomienda completarlas en orden y dedicar tiempo suficiente a cada una para entender profundamente los conceptos.
