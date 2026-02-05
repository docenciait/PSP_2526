# UT3. Programación de Comunicaciones en Red con Sockets en Kotlin

---

# 1. Introducción a la Comunicación en Red y Sockets

## 1.1. Qué es la comunicación en red

La comunicación en red implica que dos procesos —cliente y servidor— intercambian datos a través de un canal lógico llamado **socket**.
En Kotlin, el soporte de sockets es accesible desde la librería estándar de la JVM, pero se utiliza **con sintaxis, estructuras, objetos y corrutinas Kotlin**.

---

## 1.2. Escenarios típicos

- Chats y mensajería instantánea
- Juegos multijugador
- Sensores e IoT
- Comunicación entre dos Android (LAN, WiFi Direct, hotspot)
- Sincronización de datos
- Evitar dependencias HTTP cuando se necesita velocidad o persistencia de conexiones

---

## 1.3. Cliente y Servidor

**Servidor:**

- Abre un puerto
- Espera conexiones
- Atiende a uno o varios clientes
- Mantiene lógica de negocio

**Cliente:**

- Se conecta al servidor
- Envía datos
- Recibe respuestas

---

## 1.4. Qué es un socket

Un socket es un objeto bidireccional que permite enviar y recibir bytes.
Se compone de:

- Dirección IP
- Puerto
- Canal de entrada (InputStream)
- Canal de salida (OutputStream)

---

## 1.5. Tipos

| Tipo    | Características                                                                                            |
| ------- | ---------------------------------------------------------------------------------------------------------- |
| **TCP** | Orientado a conexión. Garantiza entrega y orden. Útil para chat, transacciones, juegos con lógica estable. |
| **UDP** | No orientado a conexión. Rápido. Adecuado para streaming, telemetría, juegos a alta velocidad.             |

---

# 2. Programación Cliente-Servidor con Sockets (Kotlin puro)

Toda esta sección está escrita **solo con Kotlin**.
Nada de Java.
Las clases del JDK se acceden con sintaxis Kotlin, métodos Kotlin y wrappers Kotlin.

---

# 2.1. Diagrama general

```mermaid
sequenceDiagram
    participant C as Cliente Kotlin
    participant S as Servidor Kotlin

    C->>S: Solicitud de conexión (TCP)
    S->>C: Aceptación
    C->>S: Envío de datos
    S->>C: Respuesta
    C->>S: Cierre de conexión
```

---

# 2.2. Servidor TCP básico (Kotlin idiomático)

```kotlin
import java.net.ServerSocket

fun main() {
    val server = ServerSocket(9000)
    println("Servidor iniciado en puerto 9000")

    while (true) {
        val client = server.accept()
        println("Nuevo cliente conectado: ${client.inetAddress.hostAddress}")

        val input = client.getInputStream().bufferedReader()
        val output = client.getOutputStream().bufferedWriter()

        val recibido = input.readLine()
        println("Cliente envió: $recibido")

        output.write("Eco: $recibido\n")
        output.flush()

        client.close()
    }
}
```

Características Kotlin:

- Uso de `bufferedReader()` y `bufferedWriter()` directamente desde Kotlin
- Interpolación de strings (`${...}`)
- Flujos con `readLine()` y `write()`

---

# 2.3. Cliente TCP (Kotlin puro)

```kotlin
import java.net.Socket

fun main() {
    val socket = Socket("127.0.0.1", 9000)

    val input = socket.getInputStream().bufferedReader()
    val output = socket.getOutputStream().bufferedWriter()

    output.write("Hola servidor desde Kotlin\n")
    output.flush()

    val respuesta = input.readLine()
    println("Respuesta del servidor: $respuesta")

    socket.close()
}
```

---

# 2.4. Concurrencia moderna con corrutinas (Kotlin 2025)

La forma profesional de hacer un servidor con múltiples clientes en Kotlin no es con threads, sino **con corrutinas**.

```kotlin
import kotlinx.coroutines.*
import java.net.ServerSocket

fun main() = runBlocking {
    val server = ServerSocket(9000)
    println("Servidor concurrente con corrutinas")

    while (true) {
        val client = server.accept()

        launch(Dispatchers.IO) {
            val input = client.getInputStream().bufferedReader()
            val output = client.getOutputStream().bufferedWriter()

            val msg = input.readLine()
            println("(${currentCoroutineContext()}) Cliente: $msg")

            output.write("Procesado: $msg\n")
            output.flush()

            client.close()
        }
    }
}
```

Ventajas:

- No se bloquea el hilo principal
- Cada cliente se maneja en una corrutina
- Rendimiento muy alto
- Solución idiomática Kotlin

---

# 3. Sockets UDP con Kotlin

UDP se maneja con objetos `DatagramSocket` y `DatagramPacket`.

---

# 3.1. Servidor UDP con Kotlin

```kotlin
import java.net.DatagramPacket
import java.net.DatagramSocket

fun main() {
    val socket = DatagramSocket(5000)
    val buffer = ByteArray(1024)

    println("Servidor UDP escuchando en puerto 5000")

    while (true) {
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)

        val texto = packet.data.decodeToString(0, packet.length)
        println("Recibido: $texto")
    }
}
```

---

# 3.2. Cliente UDP con Kotlin

```kotlin
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun main() {
    val socket = DatagramSocket()
    val mensaje = "Hola UDP Kotlin".encodeToByteArray()

    val packet = DatagramPacket(
        mensaje,
        mensaje.size,
        InetAddress.getByName("127.0.0.1"),
        5000
    )

    socket.send(packet)
    socket.close()
}
```

---

# 4. Estructuras concurrentes en Kotlin

## 4.1. Estrategias

| Estrategia         | Recomendación en Kotlin      |
| ------------------ | ---------------------------- |
| Thread por cliente | Desaconsejado                |
| Pool de threads    | Válido en sistemas heredados |
| Corrutinas         | La opción moderna en Kotlin  |

---

## 4.2. Implementación profesional con corrutinas y SupervisorJob

```kotlin
val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

while (true) {
    val cliente = server.accept()

    scope.launch {
        procesarCliente(cliente)
    }
}
```

---

# 5. Monitorización y Depuración en Kotlin

Aunque las herramientas son externas, el enfoque es 100 % aplicable a proyectos Kotlin.

## 5.1. Herramientas

- Wireshark: inspección de paquetes
- netstat o ss: ver puertos abiertos
- tcpdump: análisis en terminal
- Logs estructurados con Kotlin Logging

---

## 5.2. Problemas típicos

| Problema               | Explicación                                 |
| ---------------------- | ------------------------------------------- |
| Bloqueo                | La lectura `readLine()` no finaliza         |
| Pérdida de mensajes    | UDP sin garantías                           |
| Desconexión inesperada | El cliente cierra antes de enviar           |
| Sin flush              | El servidor no envía porque falta `flush()` |

---

# 6. Miniproyectos 100 % Kotlin

Pensados para FP, reutilizables en Android Jetpack Compose.

---

## Miniproyecto 1: Eco concurrente con corrutinas

Servidor que recibe un mensaje y devuelve el mismo.

Objetivos:

- Corrutinas
- Conversión de bytes a texto
- Lógica paralela

---

## Miniproyecto 2: Chat simple con broadcast

Servidor que:

1. Mantiene una lista mutable de clientes
2. Cada mensaje que recibe lo reenvía a todos

Conceptos Kotlin:

- Listas sincronizadas
- Corrutinas para cada cliente
- Eliminación de clientes desconectados

---

## Miniproyecto 3: Monitor de latencia UDP

- Cliente envía timestamp
- Servidor devuelve el mismo timestamp
- Cliente calcula RTT

Uso de:

- `System.nanoTime()`
- `decodeToString()`
- `encodeToByteArray()`

---

## Miniproyecto 4: Servidor para apps Android

Pensado para integrarlo luego en Jetpack Compose.

- Servidor TCP en Kotlin
- Cliente Android en otra práctica
- Comunicación binaria o textual
- Corrutinas IO para llamadas de red

---

# 7. WebSockets en Android (Compose + Ktor + FastAPI)

## 7.1. Backend FastAPI (Python 3.11+)

```python
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

connected = set()


@app.websocket("/ws")
async def websocket_endpoint(ws: WebSocket):
    await ws.accept()
    connected.add(ws)
    try:
        while True:
            data = await ws.receive_text()
            for peer in connected:
                await peer.send_text(f"eco: {data}")
    except WebSocketDisconnect:
        connected.remove(ws)
```

Arrancar con Uvicorn: `uvicorn main:app --reload --port 8000`.

---

## 7.2. Dependencias en Android (Gradle Kotlin DSL)

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-android:3.0.0")
    implementation("io.ktor:ktor-client-websockets:3.0.0")
    implementation("io.ktor:ktor-client-logging:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
}
```

---

## 7.3. Cliente Ktor con corrutinas

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.ws
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val client = HttpClient(Android) {
        install(Logging) { level = LogLevel.INFO }
        install(ContentNegotiation) { json() }
    }

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    private val outgoing = Channel<String>(capacity = Channel.UNLIMITED)

    fun connect(url: String = "ws://10.0.2.2:8000/ws") {
        viewModelScope.launch {
            client.ws(urlString = url) {
                launch {
                    for (msg in outgoing) send(Frame.Text(msg))
                }
                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            _messages.value = _messages.value + frame.readText()
                        }
                    }
                } finally {
                    close(CloseReason(CloseReason.Codes.NORMAL, "bye"))
                }
            }
        }
    }

    fun send(text: String) {
        outgoing.trySend(text)
    }
}
```

---

## 7.4. UI con Jetpack Compose

```kotlin
@Composable
fun ChatScreen(vm: ChatViewModel = viewModel()) {
    val mensajes by vm.messages.collectAsState()
    var texto by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.connect() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(Modifier.weight(1f)) {
            items(mensajes) { msg -> Text(msg) }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                modifier = Modifier.weight(1f),
                label = { Text("Mensaje") }
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.send(texto); texto = "" }) {
                Text("Enviar")
            }
        }
    }
}
```

---

## 7.5. Buenas prácticas

- Usar `10.0.2.2` desde emulador para llegar al host.
- Mantener el `HttpClient` como singleton o inyectado (Hilt/Koin).
- Cerrar la sesión WebSocket en `onCleared()` si no se usa dentro del scope del `ViewModel`.
- Validar reconexiones y backoff exponencial en producción.
