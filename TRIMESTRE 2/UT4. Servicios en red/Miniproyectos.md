### Reto Android: "Weather Card App"

**Objetivo:**
Crear una aplicación Android utilizando **Kotlin**, **Jetpack Compose** y **Retrofit2** que consulte la temperatura actual de una ciudad específica (por ejemplo, Madrid) y la muestre de forma elegante en una tarjeta (Card).

---

#### 1. El Servicio API (Backend)

Utilizaremos la API gratuita de **Open-Meteo**. No requiere registro ni clave (API Key).

- **Base URL:** `https://api.open-meteo.com/`
- **Endpoint:** `v1/forecast`
- **Parámetros necesarios:**
  - `latitude`: 40.41 (Madrid)
  - `longitude`: -3.70 (Madrid)
  - `current_weather`: true

---

#### 2. Requisitos Técnicos (Lo que deben programar)

**A. Modelo de Datos (Data Class):**
Debes crear las clases necesarias para mapear el JSON que devuelve la API. Fíjate que la temperatura está dentro de un objeto llamado `current_weather`.

```kotlin
// Ejemplo de estructura necesaria:
data class WeatherResponse(val current_weather: CurrentData)
data class CurrentData(val temperature: Double)
```

**B. Interfaz de Retrofit (`ApiService`):**
Define la interfaz con una función `getWeather`. Debes usar la anotación `@Query` para pasar la latitud, longitud y el booleano de clima actual.

**C. Lógica de Negocio (`MainViewModel`):**

- Crea un `StateFlow` para almacenar la temperatura (puedes empezar con un valor de "0.0").
- Crea una función que use `viewModelScope.launch` para llamar a la API y actualizar el estado con el valor recibido.

**D. Interfaz de Usuario (Jetpack Compose):**
Diseña una pantalla que contenga:

1.  **Una Card:** Dentro de la Card debe aparecer el nombre de la ciudad ("Madrid") y la temperatura actual con un estilo llamativo (ej. `HeadlineLarge`).
2.  **Un Botón:** Al pulsarlo, debe disparar la petición a la API para actualizar la información de la Card.

---

#### 3. Ejemplo Visual Esperado (UI)

La interfaz debería verse algo así:

```kotlin
@Composable
fun WeatherCard(city: String, temperature: String, onRefresh: () -> Unit) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = city, style = MaterialTheme.typography.titleLarge)
            Text(text = "$temperature °C", style = MaterialTheme.typography.displayMedium)
            Button(onClick = onRefresh) {
                Text("Actualizar Clima")
            }
        }
    }
}
```

---

