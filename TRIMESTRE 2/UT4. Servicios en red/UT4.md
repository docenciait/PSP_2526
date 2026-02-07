### UT4: Servicios en Red

---

#### 1. Servicios en Red

Los **servicios en red** son aplicaciones o programas que se ejecutan en un servidor y ofrecen funcionalidades accesibles a través de una red, como Internet o una red local (LAN). Estos servicios permiten que diferentes dispositivos o usuarios se comuniquen, compartan recursos o realicen tareas específicas.

**Ejemplos de servicios en red:**

- Servidores web (páginas web, aplicaciones web)
- Servidores de correo electrónico
- Servidores FTP para transferencia de archivos
- Servicios de mensajería instantánea
- Servicios de bases de datos accesibles remotamente

**Modelo Cliente-Servidor:**

La mayoría de los servicios en red se basan en el modelo cliente-servidor, donde:

- **Servidor:** Equipo o programa que ofrece un servicio y espera solicitudes.
- **Cliente:** Equipo o programa que solicita un servicio al servidor.

El servidor está siempre activo y escucha peticiones en un puerto específico. Cuando un cliente realiza una solicitud, el servidor la procesa y envía una respuesta.

**Características importantes:**

- **Concurrencia:** Capacidad para atender múltiples clientes al mismo tiempo.
- **Disponibilidad:** El servicio debe estar accesible y operativo la mayor parte del tiempo.
- **Seguridad:** Protección de datos y control de acceso.
- **Escalabilidad:** Capacidad para crecer y atender más usuarios o datos.

---

#### 2. Protocolos

Un **protocolo** es un conjunto de reglas y formatos que definen cómo se comunican dos o más dispositivos en una red. Los protocolos garantizan que la información se transmita correctamente y que ambos extremos entiendan los datos intercambiados.

**Protocolos estándar en servicios en red:**

- **HTTP (HyperText Transfer Protocol):** Protocolo base para la comunicación web. Permite la transferencia de páginas web y recursos asociados (imágenes, scripts, etc.). Funciona sobre TCP y es sin estado (cada petición es independiente).

- **FTP (File Transfer Protocol):** Protocolo para la transferencia de archivos entre cliente y servidor. Usa dos conexiones TCP: una para control (comandos) y otra para datos (archivos). Permite subir, descargar, renombrar y borrar archivos.

- **SMTP (Simple Mail Transfer Protocol):** Protocolo para el envío de correos electrónicos. Se usa para enviar mensajes desde el cliente al servidor de correo o entre servidores.

- **POP3 (Post Office Protocol 3):** Protocolo para la recepción de correos electrónicos. Permite descargar los mensajes desde el servidor al cliente.

- **Telnet:** Protocolo para acceso remoto a un equipo mediante línea de comandos. No cifra la información, por lo que no es seguro para redes públicas.

**Importancia de los protocolos:**

- Garantizan interoperabilidad entre sistemas diferentes.
- Definen cómo se estructuran los mensajes.
- Establecen cómo se inicia, mantiene y termina la comunicación.

---

#### 3. Comunicaciones TCP

El **Protocolo de Control de Transmisión (TCP)** es uno de los protocolos fundamentales de Internet. Proporciona una comunicación fiable y orientada a conexión entre dos dispositivos.

**Características principales de TCP:**

- **Orientado a conexión:** Antes de enviar datos, se establece una conexión mediante un proceso llamado "handshake" de tres vías (SYN, SYN-ACK, ACK).
- **Fiabilidad:** TCP garantiza que los datos lleguen completos y en orden. Si un paquete se pierde, se retransmite.
- **Control de flujo:** Ajusta la velocidad de transmisión para evitar saturar al receptor.
- **Multiplexación:** Permite que múltiples aplicaciones usen la red simultáneamente mediante el uso de puertos.

**Funcionamiento básico:**

1. **Establecimiento de conexión:** Cliente y servidor realizan el handshake.
2. **Transferencia de datos:** Se envían los datos en segmentos, con confirmación de recepción.
3. **Terminación de conexión:** Se cierra la conexión de forma ordenada.

**Ventajas de TCP:**

- Comunicación fiable y segura.
- Control de errores y congestión.
- Uso en protocolos como HTTP, FTP, SMTP.

---

#### 4. Aplicaciones: HTTP

**HTTP** es el protocolo más utilizado para servicios web. Permite que los clientes (navegadores, aplicaciones móviles) soliciten recursos y que los servidores respondan con esos recursos.

**Estructura de una comunicación HTTP:**

- **Solicitud (Request):** El cliente envía una petición al servidor con:

  - Método HTTP (GET, POST, PUT, DELETE, etc.)
  - URL del recurso
  - Cabeceras (headers) con información adicional (tipo de contenido, autenticación, etc.)
  - Cuerpo (body) opcional, usado en métodos como POST para enviar datos.

- **Respuesta (Response):** El servidor responde con:
  - Código de estado (200 OK, 404 Not Found, 500 Internal Server Error, etc.)
  - Cabeceras con información sobre la respuesta
  - Cuerpo con el recurso solicitado (HTML, JSON, imagen, etc.)

**Características de HTTP:**

- **Stateless:** Cada petición es independiente; el servidor no guarda estado entre peticiones.
- **Extensible:** Se pueden añadir cabeceras para controlar caché, autenticación, compresión, etc.
- **Seguro:** Se puede usar sobre TLS/SSL (HTTPS) para cifrar la comunicación y proteger los datos.

**Ejemplo de uso:**

Cuando escribes una URL en el navegador, este envía una petición HTTP GET al servidor web para obtener la página solicitada.

---

#### 5. REST API

**REST (Representational State Transfer)** es un estilo arquitectónico para diseñar servicios web que utilizan HTTP para la comunicación.

**Principios básicos de REST:**

- **Recursos:** Todo se representa como recursos identificados por URLs (por ejemplo, `/usuarios`, `/productos/123`).
- **Operaciones estándar:** Se usan los métodos HTTP para operar sobre recursos:
  - **GET:** Obtener un recurso o lista de recursos.
  - **POST:** Crear un nuevo recurso.
  - **PUT:** Actualizar un recurso existente.
  - **DELETE:** Eliminar un recurso.
- **Stateless:** Cada petición contiene toda la información necesaria para ser procesada.
- **Representaciones:** Los recursos pueden representarse en diferentes formatos, siendo JSON el más común.
- **Interfaz uniforme:** Simplifica la interacción entre cliente y servidor.

**Ventajas de REST:**

- Simplicidad y facilidad de uso.
- Escalabilidad y flexibilidad.
- Independencia entre cliente y servidor.
- Amplio soporte en tecnologías y lenguajes.

**Ejemplo de una REST API para gestión de usuarios:**

| Método | URL           | Acción                     |
| ------ | ------------- | -------------------------- |
| GET    | /usuarios     | Obtener lista de usuarios  |
| GET    | /usuarios/123 | Obtener usuario con ID 123 |
| POST   | /usuarios     | Crear un nuevo usuario     |
| PUT    | /usuarios/123 | Actualizar usuario 123     |
| DELETE | /usuarios/123 | Eliminar usuario 123       |

---

#### 6. Retrofit: CRUD contra API pública

Objetivo: consumir una API REST pública (por ejemplo, https://jsonplaceholder.typicode.com) con Retrofit en Android para operaciones CRUD.

**Dependencias (Gradle Kotlin DSL):**

```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
```

**Modelo de datos (DTO):**

```kotlin
data class Post(
    val userId: Int,
    val id: Int? = null,
    val title: String,
    val body: String
)
```

**Interfaz Retrofit:**

```kotlin
import retrofit2.http.*

interface PostApi {
    @GET("posts")
    suspend fun list(): List<Post>

    @GET("posts/{id}")
    suspend fun get(@Path("id") id: Int): Post

    @POST("posts")
    suspend fun create(@Body post: Post): Post

    @PUT("posts/{id}")
    suspend fun update(@Path("id") id: Int, @Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun delete(@Path("id") id: Int): Unit
}
```

**Instancia Retrofit:**

```kotlin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun providePostApi(): PostApi {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()

    return retrofit.create(PostApi::class.java)
}
```

**Uso desde ViewModel (corrutinas):**

```kotlin
class PostViewModel : ViewModel() {
    private val api = providePostApi()
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    fun load() = viewModelScope.launch { _posts.value = api.list() }

    fun add(title: String, body: String) = viewModelScope.launch {
        api.create(Post(userId = 1, title = title, body = body))
        load()
    }

    fun update(id: Int, title: String, body: String) = viewModelScope.launch {
        api.update(id, Post(userId = 1, id = id, title = title, body = body))
        load()
    }

    fun remove(id: Int) = viewModelScope.launch {
        api.delete(id)
        load()
    }
}
```

**Notas rápidas:**

- `jsonplaceholder` no persiste cambios; devuelve respuestas simuladas.
- Añade manejo de errores (try/catch, Result, Either) y mapeo a modelos de dominio en apps reales.
- Inyecta `PostApi` con Hilt/Koin y comparte el `OkHttpClient`.
- Usa `Dispatchers.IO` si trabajas fuera de `viewModelScope`.

---

### Resumen y Conexión de Conceptos

Los servicios en red permiten que aplicaciones y dispositivos se comuniquen y compartan información. Para ello, utilizan protocolos estándar como HTTP y FTP, que funcionan sobre protocolos de transporte fiables como TCP. HTTP es la base de la web y, junto con REST, permite crear APIs que facilitan la interacción entre sistemas de forma sencilla y escalable.

---
