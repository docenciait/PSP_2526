### UT5. Seguridad en aplicaciones y comunicaciones

### Tema desarrollado: Seguridad en aplicaciones y comunicaciones

---

#### 1. Programación segura y principios de seguridad

La programación segura es un conjunto de prácticas y técnicas que buscan desarrollar software resistente a ataques y vulnerabilidades. Su objetivo es proteger la confidencialidad, integridad y disponibilidad de la información y los sistemas.

**Principios básicos de seguridad en programación:**

- **Principio de mínimo privilegio:** Cada componente o usuario debe tener solo los permisos estrictamente necesarios para realizar su función.
- **Validación y saneamiento de entradas:** Nunca confiar en datos externos; siempre validar y limpiar para evitar inyecciones (SQL, comandos, scripts).
- **Gestión segura de errores:** No mostrar mensajes de error detallados al usuario final para evitar revelar información sensible.
- **Autenticación y autorización robustas:** Verificar la identidad y controlar el acceso a recursos.
- **Cifrado de datos sensibles:** Proteger datos en reposo y en tránsito.
- **Actualización y parcheo:** Mantener el software actualizado para corregir vulnerabilidades conocidas.

---

#### 2. Criptografía: clave pública y privada, hash, firma digital

La criptografía es la ciencia que estudia técnicas para proteger la información mediante transformaciones matemáticas.

- **Clave pública y privada (criptografía asimétrica):** Utiliza un par de claves, una pública para cifrar y otra privada para descifrar. Ejemplo: RSA. Permite comunicaciones seguras sin compartir previamente una clave secreta.
- **Hash:** Función que transforma datos de cualquier tamaño en una cadena fija de caracteres (resumen). Es unidireccional y sirve para verificar integridad. Ejemplo: SHA-256.

- **Firma digital:** Es un hash cifrado con la clave privada del emisor, que garantiza autenticidad, integridad y no repudio del mensaje. El receptor usa la clave pública para verificarla.

---

#### 3. Certificados digitales y autoridades de certificación

Un **certificado digital** es un documento electrónico que vincula una clave pública con la identidad de una entidad (persona, empresa, servidor). Está firmado por una **Autoridad de Certificación (CA)** confiable, que valida la identidad.

Los certificados permiten establecer conexiones seguras y confiables, por ejemplo, en HTTPS, garantizando que el servidor es quien dice ser.

---

#### 4. Políticas de control de acceso

Las políticas de control de acceso definen quién puede acceder a qué recursos y bajo qué condiciones. Se basan en modelos como:

- **DAC (Discretionary Access Control):** El propietario decide quién accede.
- **MAC (Mandatory Access Control):** Acceso basado en reglas estrictas definidas por la organización.
- **RBAC (Role-Based Access Control):** Acceso basado en roles asignados a usuarios.

Estas políticas se implementan para proteger recursos y datos sensibles.

---

#### 5. Encriptación de datos almacenados y transmitidos

- **Datos almacenados:** Se cifran para protegerlos en caso de acceso no autorizado a discos o bases de datos. Ejemplo: cifrado AES.
- **Datos transmitidos:** Se cifran para evitar que sean interceptados y leídos durante la comunicación. Ejemplo: uso de protocolos seguros como TLS.

---

#### 6. Protocolos seguros (SSL/TLS, HTTPS)

- **SSL (Secure Sockets Layer) y TLS (Transport Layer Security):** Protocolos que proporcionan cifrado y autenticación en las comunicaciones por internet. TLS es la versión moderna y segura de SSL.
- **HTTPS:** Es HTTP sobre TLS, que garantiza que la comunicación entre navegador y servidor es segura y privada.

Estos protocolos evitan ataques como la interceptación (man-in-the-middle) y garantizan la integridad de los datos.

---

#### 7. Sockets seguros y autenticación

Los **sockets seguros** (por ejemplo, usando TLS) permiten establecer canales de comunicación cifrados entre aplicaciones. La autenticación puede ser mutua, donde ambas partes verifican su identidad mediante certificados digitales.

Esto es fundamental en aplicaciones distribuidas para proteger la información y evitar accesos no autorizados.

---

#### 8. Programación de aplicaciones seguras

Para desarrollar aplicaciones seguras se deben seguir buenas prácticas como:

- Uso de frameworks y librerías que implementen seguridad.
- Gestión segura de sesiones y tokens.
- Prevención de vulnerabilidades comunes (inyección SQL, XSS, CSRF).
- Implementación de autenticación multifactor.
- Registro y monitoreo de eventos de seguridad.
- Pruebas de seguridad y auditorías periódicas.

---

#### 9. Ejemplo práctico: Autenticación con Auth0 en Jetpack Compose

**Auth0** es una plataforma de autenticación y autorización que permite añadir fácilmente funcionalidades de inicio de sesión seguro a aplicaciones.

##### 9.1. Configuración inicial

**1. Añadir dependencias en `build.gradle.kts` (módulo app):**

```kotlin
dependencies {
    implementation("com.auth0.android:auth0:2.10.2")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
}
```

**2. Configurar Auth0 en `res/values/strings.xml`:**

```xml
<resources>
    <string name="com_auth0_domain">tu-dominio.auth0.com</string>
    <string name="com_auth0_client_id">TU_CLIENT_ID</string>
</resources>
```

**3. Configurar el esquema en `AndroidManifest.xml`:**

```xml
<application>
    <!-- Otras configuraciones -->

    <activity
        android:name="com.auth0.android.provider.WebAuthActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data
                android:host="tu-dominio.auth0.com"
                android:pathPrefix="/android/YOUR_APP_PACKAGE_NAME/callback"
                android:scheme="https" />
        </intent-filter>
    </activity>
</application>
```

##### 9.2. Implementación del AuthManager

```kotlin
import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager(private val context: Context) {
    private val account = Auth0(context)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Authenticated(val credentials: Credentials) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun login() {
        _authState.value = AuthState.Loading

        WebAuthProvider.login(account)
            .withScheme("https")
            .withScope("openid profile email")
            .start(context as android.app.Activity, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    _authState.value = AuthState.Authenticated(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    _authState.value = AuthState.Error(error.getDescription())
                }
            })
    }

    fun logout() {
        _authState.value = AuthState.Loading

        WebAuthProvider.logout(account)
            .withScheme("https")
            .start(context as android.app.Activity, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    _authState.value = AuthState.Unauthenticated
                }

                override fun onFailure(error: AuthenticationException) {
                    _authState.value = AuthState.Error(error.getDescription())
                }
            })
    }
}
```

##### 9.3. ViewModel para gestionar el estado

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(private val authManager: AuthManager) : ViewModel() {

    val authState: StateFlow<AuthManager.AuthState> = authManager.authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthManager.AuthState.Unauthenticated
        )

    fun login() {
        authManager.login()
    }

    fun logout() {
        authManager.logout()
    }
}
```

##### 9.4. Interfaz con Jetpack Compose

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = authState) {
                is AuthManager.AuthState.Unauthenticated -> {
                    LoginContent(onLogin = { authViewModel.login() })
                }

                is AuthManager.AuthState.Loading -> {
                    CircularProgressIndicator()
                }

                is AuthManager.AuthState.Authenticated -> {
                    AuthenticatedContent(
                        credentials = state.credentials,
                        onLogout = { authViewModel.logout() }
                    )
                }

                is AuthManager.AuthState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = { authViewModel.login() }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginContent(onLogin: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Iniciar sesión")
        }
    }
}

@Composable
fun AuthenticatedContent(
    credentials: com.auth0.android.result.Credentials,
    onLogout: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "¡Sesión iniciada!",
            style = MaterialTheme.typography.headlineLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Información del usuario",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Access Token: ${credentials.accessToken.take(20)}...",
                    style = MaterialTheme.typography.bodySmall
                )

                credentials.idToken?.let {
                    Text(
                        text = "ID Token: ${it.take(20)}...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Error de autenticación",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )

        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}
```

##### 9.5. Activity principal

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)

        setContent {
            MaterialTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(authManager)
                )
                AuthScreen(authViewModel = authViewModel)
            }
        }
    }
}

// Factory para crear el ViewModel
class AuthViewModelFactory(
    private val authManager: AuthManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

##### 9.6. Características de seguridad implementadas

Este ejemplo implementa varios principios de seguridad:

1. **Autenticación delegada:** Auth0 gestiona las credenciales de manera segura.
2. **OAuth 2.0 y OpenID Connect:** Protocolos estándar de la industria.
3. **Tokens JWT:** Uso de tokens seguros para mantener sesiones.
4. **HTTPS:** Todas las comunicaciones son cifradas.
5. **Gestión de estado:** El estado de autenticación se gestiona de forma reactiva y segura.
6. **Separación de responsabilidades:** La lógica de autenticación está separada de la UI.

##### 9.7. Mejoras adicionales recomendadas

- Almacenar tokens de forma segura usando **EncryptedSharedPreferences**.
- Implementar renovación automática de tokens antes de que expiren.
- Añadir autenticación biométrica como capa adicional.
- Implementar manejo de deeplinks para recuperación de contraseña.
- Añadir análisis de dispositivos comprometidos (root/jailbreak).

##### 9.8. Configuración de Auth0 Dashboard (paso a paso)

Para usar Auth0 en tu aplicación, necesitas crear y configurar una cuenta:

**1. Crear cuenta en Auth0:**

- Visita [https://auth0.com/signup](https://auth0.com/signup)
- Regístrate con email o cuenta de GitHub/Google
- Selecciona la región más cercana para tu tenant

**2. Crear una aplicación:**

- En el Dashboard, ve a **Applications** → **Create Application**
- Nombre: "Mi App Android" (o el nombre que prefieras)
- Tipo: **Native** (para aplicaciones móviles)
- Haz clic en **Create**

**3. Configurar la aplicación:**

En la pestaña **Settings** de tu aplicación:

- **Domain:** Copia este valor (ej: `dev-abc123.auth0.com`)
- **Client ID:** Copia este valor (lo necesitarás en `strings.xml`)

En **Application URIs:**

- **Allowed Callback URLs:**
  ```
  https://tu-dominio.auth0.com/android/com.tuempresa.tuapp/callback
  ```
- **Allowed Logout URLs:**
  ```
  https://tu-dominio.auth0.com/android/com.tuempresa.tuapp/callback
  ```
- **Allowed Origins (CORS):** Dejar vacío para apps nativas

Haz clic en **Save Changes**

**4. Configurar conexiones de autenticación:**

En el menú lateral, ve a **Authentication** → **Database**:

- Puedes usar la conexión por defecto "Username-Password-Authentication"
- O crear una nueva conexión personalizada

Para redes sociales (Google, Facebook, etc.):

- Ve a **Authentication** → **Social**
- Haz clic en la red social deseada
- Sigue las instrucciones para configurar las credenciales

**5. Probar la autenticación:**

Puedes probar sin código usando **Authentication** → **Try** en el Dashboard:

- Selecciona tu aplicación
- Prueba el flujo de login
- Verifica que funciona correctamente

**6. Personalización (opcional):**

- **Branding:** Personaliza el Universal Login con tu logo y colores
  - Ve a **Branding** → **Universal Login**
- **Emails:** Personaliza templates de verificación y recuperación
  - Ve a **Branding** → **Email Templates**

- **Rules/Actions:** Añade lógica personalizada durante la autenticación
  - Ve a **Auth Pipeline** → **Rules** o **Actions**

**7. Modo de producción:**

Para pasar a producción:

- Verifica el plan de Auth0 que necesitas
- Configura un dominio personalizado
- Habilita MFA para mayor seguridad
- Configura logs y monitoreo
- Revisa las políticas de contraseña

---

### 10. Referencias y documentación

#### 10.1. Documentación oficial de Auth0

**Recursos principales:**

- **Auth0 Documentation:** [https://auth0.com/docs](https://auth0.com/docs)
  - Documentación completa sobre todos los aspectos de Auth0
- **Auth0 Android SDK:** [https://github.com/auth0/Auth0.Android](https://github.com/auth0/Auth0.Android)
  - Repositorio oficial del SDK de Auth0 para Android
  - Incluye ejemplos, guías de migración y changelog

- **Auth0 Quickstart for Android:** [https://auth0.com/docs/quickstart/native/android](https://auth0.com/docs/quickstart/native/android)
  - Guía paso a paso para integrar Auth0 en Android
  - Ejemplos con código completo

- **Auth0 Jetpack Compose Guide:** [https://auth0.com/docs/quickstart/native/android/interactive](https://auth0.com/docs/quickstart/native/android/interactive)
  - Implementación específica con Jetpack Compose
  - Mejores prácticas para apps modernas de Android

**Configuración y conceptos clave:**

- **Auth0 Dashboard:** [https://manage.auth0.com](https://manage.auth0.com)
  - Panel de administración para configurar aplicaciones
  - Gestión de usuarios, conexiones y reglas

- **OAuth 2.0 y OpenID Connect:** [https://auth0.com/docs/authenticate/protocols/oauth](https://auth0.com/docs/authenticate/protocols/oauth)
  - Explicación de los protocolos utilizados
  - Flujos de autenticación disponibles

- **Tokens y Claims:** [https://auth0.com/docs/secure/tokens](https://auth0.com/docs/secure/tokens)
  - Comprensión de Access Tokens, ID Tokens y Refresh Tokens
  - Cómo validar y usar tokens JWT

**Seguridad:**

- **Security Best Practices:** [https://auth0.com/docs/secure/security-guidance](https://auth0.com/docs/secure/security-guidance)
  - Recomendaciones de seguridad de Auth0
  - Prevención de vulnerabilidades comunes

- **Token Storage:** [https://auth0.com/docs/secure/security-guidance/data-security/token-storage](https://auth0.com/docs/secure/security-guidance/data-security/token-storage)
  - Mejores prácticas para almacenar tokens en Android
  - Uso de EncryptedSharedPreferences

#### 10.2. Documentación de seguridad en Android

- **Android Security Best Practices:** [https://developer.android.com/topic/security/best-practices](https://developer.android.com/topic/security/best-practices)
  - Guía oficial de seguridad de Android Developer

- **Android Jetpack Security:** [https://developer.android.com/jetpack/androidx/releases/security](https://developer.android.com/jetpack/androidx/releases/security)
  - Librería de seguridad de Jetpack
  - EncryptedSharedPreferences y EncryptedFile

- **Android Biometric Authentication:** [https://developer.android.com/training/sign-in/biometric-auth](https://developer.android.com/training/sign-in/biometric-auth)
  - Implementación de autenticación biométrica

#### 10.3. Estándares y especificaciones

- **OAuth 2.0 (RFC 6749):** [https://datatracker.ietf.org/doc/html/rfc6749](https://datatracker.ietf.org/doc/html/rfc6749)
  - Especificación oficial del protocolo OAuth 2.0

- **OpenID Connect:** [https://openid.net/connect/](https://openid.net/connect/)
  - Especificación de OpenID Connect sobre OAuth 2.0

- **JWT (JSON Web Tokens):** [https://jwt.io/](https://jwt.io/)
  - Información completa sobre tokens JWT
  - Debugger para inspeccionar tokens

- **PKCE (Proof Key for Code Exchange):** [https://oauth.net/2/pkce/](https://oauth.net/2/pkce/)
  - Extensión de OAuth 2.0 para aplicaciones móviles

#### 10.4. Criptografía y seguridad general

- **OWASP Mobile Top 10:** [https://owasp.org/www-project-mobile-top-10/](https://owasp.org/www-project-mobile-top-10/)
  - Los 10 riesgos de seguridad más críticos en aplicaciones móviles

- **OWASP Mobile Application Security:** [https://mas.owasp.org/](https://mas.owasp.org/)
  - Guía completa de seguridad para aplicaciones móviles

- **TLS/SSL Explained:** [https://www.cloudflare.com/learning/ssl/what-is-ssl/](https://www.cloudflare.com/learning/ssl/what-is-ssl/)
  - Explicación detallada de protocolos SSL/TLS

- **Cryptography Best Practices:** [https://www.owasp.org/index.php/Cryptographic_Storage_Cheat_Sheet](https://www.owasp.org/index.php/Cryptographic_Storage_Cheat_Sheet)
  - Mejores prácticas para almacenamiento criptográfico

#### 10.5. Tutoriales y recursos adicionales

- **Auth0 Blog - Android:** [https://auth0.com/blog/tags/android/](https://auth0.com/blog/tags/android/)
  - Artículos y tutoriales sobre Auth0 en Android

- **Auth0 Community:** [https://community.auth0.com/](https://community.auth0.com/)
  - Foro de la comunidad para resolver dudas

- **Jetpack Compose Authentication Patterns:** [https://developer.android.com/jetpack/compose/authentication](https://developer.android.com/jetpack/compose/authentication)
  - Patrones de autenticación en Compose

- **Android Developers - Security with HTTPS and SSL:** [https://developer.android.com/training/articles/security-ssl](https://developer.android.com/training/articles/security-ssl)
  - Seguridad en comunicaciones de red

#### 10.6. Herramientas útiles

- **JWT.io Debugger:** [https://jwt.io/](https://jwt.io/)
  - Decodificar y verificar tokens JWT

- **Postman:** [https://www.postman.com/](https://www.postman.com/)
  - Probar APIs y flujos de autenticación

- **Auth0 Terraform Provider:** [https://registry.terraform.io/providers/auth0/auth0/latest/docs](https://registry.terraform.io/providers/auth0/auth0/latest/docs)
  - Automatizar configuración de Auth0 con infraestructura como código

- **Android Studio Profiler:** [https://developer.android.com/studio/profile](https://developer.android.com/studio/profile)
  - Analizar seguridad y rendimiento de aplicaciones

#### 10.7. Ejemplos y repositorios de código

- **Auth0 Android Samples:** [https://github.com/auth0-samples/auth0-android-sample](https://github.com/auth0-samples/auth0-android-sample)
  - Ejemplos oficiales completos de Auth0

- **Google Samples - Security:** [https://github.com/android/security-samples](https://github.com/android/security-samples)
  - Ejemplos de seguridad de Android

- **Jetpack Compose Samples:** [https://github.com/android/compose-samples](https://github.com/android/compose-samples)
  - Ejemplos oficiales de Jetpack Compose

#### 10.8. Certificaciones y cursos recomendados

- **Auth0 Learning Center:** [https://auth0.com/learn/](https://auth0.com/learn/)
  - Recursos educativos sobre identidad y seguridad

- **Android Security Essentials (Google):** [https://developers.google.com/training/courses/android-security-essentials](https://developers.google.com/training/courses/android-security-essentials)
  - Curso oficial de seguridad en Android

- **OWASP Mobile Security Testing Guide:** [https://mobile-security.gitbook.io/mobile-security-testing-guide/](https://mobile-security.gitbook.io/mobile-security-testing-guide/)
  - Guía completa de testing de seguridad móvil

---

### 11. Glosario de términos

- **Access Token:** Token que permite acceder a recursos protegidos.
- **ID Token:** Token JWT que contiene información sobre el usuario autenticado.
- **Refresh Token:** Token de larga duración usado para obtener nuevos Access Tokens.
- **JWT (JSON Web Token):** Estándar abierto para crear tokens de acceso.
- **OAuth 2.0:** Framework de autorización para aplicaciones.
- **OpenID Connect:** Capa de identidad sobre OAuth 2.0.
- **PKCE:** Extensión de seguridad para OAuth 2.0 en aplicaciones públicas.
- **Scope:** Permisos específicos solicitados durante la autenticación.
- **Claims:** Información contenida en un token JWT.
- **Audience:** Identificador del destinatario previsto de un token.
- **Issuer:** Entidad que emite un token (Auth0).
- **MFA (Multi-Factor Authentication):** Autenticación de múltiples factores.
- **SSO (Single Sign-On):** Inicio de sesión único entre múltiples aplicaciones.
- **CORS (Cross-Origin Resource Sharing):** Mecanismo para permitir recursos entre dominios.

---
