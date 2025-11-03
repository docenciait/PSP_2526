# 2.4 Corrutinas en Android con Jetpack Compose

Las corrutinas de Kotlin son fundamentales en el desarrollo de aplicaciones Android modernas, especialmente cuando se trabaja con Jetpack Compose. En esta sección exploraremos cómo utilizar corrutinas para manejar operaciones asíncronas, llamadas a API, acceso a bases de datos y actualización de la UI de manera eficiente.

## ¿Por qué usar corrutinas en Android?

Las corrutinas resuelven varios problemas comunes en el desarrollo Android:

1. **Evitan bloquear el hilo principal (Main Thread)**: Las operaciones largas se ejecutan en segundo plano
2. **Simplifican el código asíncrono**: No más callbacks anidados
3. **Integración con Lifecycle**: Se cancelan automáticamente cuando el componente se destruye
4. **Mejor manejo de errores**: try-catch funciona como código síncrono
5. **Composición sencilla**: Fácil combinar múltiples operaciones asíncronas

:::warning Hilo principal en Android
El hilo principal (Main Thread o UI Thread) es responsable de todas las interacciones con la UI. Si se bloquea con operaciones largas, la aplicación se congela y puede mostrar el temido "Application Not Responding" (ANR).

**Regla de oro**: Nunca bloquees el hilo principal con operaciones que tarden más de 16ms.
:::

## Configuración inicial

### Dependencias en build.gradle.kts

```kotlin title="app/build.gradle.kts"
dependencies {
    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Lifecycle y ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Para llamadas de red (opcional)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Room para base de datos (opcional)
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
}
```

## Dispatchers en Android

Android proporciona dispatchers específicos para diferentes tipos de trabajo:

```kotlin
import kotlinx.coroutines.*

// Dispatchers.Main - Para operaciones de UI
launch(Dispatchers.Main) {
    // Actualizar UI
    textView.text = "Cargado"
}

// Dispatchers.IO - Para operaciones de I/O (red, base de datos, archivos)
launch(Dispatchers.IO) {
    val data = database.getAllUsers()
    // Cambiar a Main para actualizar UI
    withContext(Dispatchers.Main) {
        updateUI(data)
    }
}

// Dispatchers.Default - Para operaciones intensivas de CPU
launch(Dispatchers.Default) {
    val result = complexCalculation()
}
```

## ViewModel con corrutinas

El `ViewModel` es el lugar ideal para lanzar corrutinas en Android:

```kotlin title="UserViewModel.kt"
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class User(val id: Int, val name: String, val email: String)

data class UiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val users = repository.getUsers()
                _uiState.value = UiState(users = users, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun refreshUsers() {
        loadUsers()
    }
}
```

:::info viewModelScope
`viewModelScope` es un `CoroutineScope` vinculado al ciclo de vida del ViewModel. Todas las corrutinas lanzadas en este scope se cancelan automáticamente cuando el ViewModel se limpia (método `onCleared()`).
:::

## Composables con corrutinas

### LaunchedEffect

Ejecuta código de corrutina cuando el composable entra en la composición:

```kotlin title="UserScreen.kt"
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserScreen(viewModel: UserViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // LaunchedEffect con key
    LaunchedEffect(key1 = Unit) {
        // Se ejecuta una vez cuando el composable entra
        println("UserScreen mostrado")
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.error != null -> {
                Text("Error: ${uiState.error}")
                Button(onClick = { viewModel.refreshUsers() }) {
                    Text("Reintentar")
                }
            }
            else -> {
                UserList(users = uiState.users)
            }
        }
    }
}

@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserItem(user = user)
        }
    }
}

@Composable
fun UserItem(user: User) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

### rememberCoroutineScope

Para lanzar corrutinas desde event handlers:

```kotlin
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    val results by viewModel.searchResults.collectAsState()

    Column {
        TextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery
                // Lanzar búsqueda en una corrutina
                scope.launch {
                    delay(300) // Debounce
                    viewModel.search(newQuery)
                }
            },
            placeholder = { Text("Buscar...") }
        )

        LazyColumn {
            items(results) { result ->
                Text(result)
            }
        }
    }
}
```

### produceState

Convierte un Flow o cualquier fuente de datos en State:

```kotlin
@Composable
fun TimerScreen() {
    val seconds by produceState(initialValue = 0) {
        while (true) {
            delay(1000)
            value++
        }
    }

    Text(text = "Segundos transcurridos: $seconds")
}
```

### collectAsStateWithLifecycle

Recolecta un Flow respetando el ciclo de vida:

```kotlin
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ObservingScreen(viewModel: MyViewModel) {
    // Se pausa cuando la app va a segundo plano
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Usar state...
}
```

## Ejemplo completo: App de tareas con API

### 1. Modelo de datos

```kotlin title="models/Task.kt"
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val completed: Boolean,
    val userId: Int
)
```

### 2. API Service con Retrofit

```kotlin title="network/TaskApiService.kt"
import retrofit2.http.*

interface TaskApiService {
    @GET("todos")
    suspend fun getTasks(): List<Task>

    @GET("todos/{id}")
    suspend fun getTask(@Path("id") id: Int): Task

    @POST("todos")
    suspend fun createTask(@Body task: Task): Task

    @PUT("todos/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: Task): Task

    @DELETE("todos/{id}")
    suspend fun deleteTask(@Path("id") id: Int)
}

object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val apiService: TaskApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaskApiService::class.java)
    }
}
```

### 3. Repository

```kotlin title="repository/TaskRepository.kt"
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepository(private val apiService: TaskApiService) {

    fun getTasks(): Flow<List<Task>> = flow {
        val tasks = apiService.getTasks()
        emit(tasks)
    }

    suspend fun getTask(id: Int): Task {
        return apiService.getTask(id)
    }

    suspend fun createTask(task: Task): Task {
        return apiService.createTask(task)
    }

    suspend fun updateTask(task: Task): Task {
        return apiService.updateTask(task.id, task)
    }

    suspend fun deleteTask(taskId: Int) {
        apiService.deleteTask(taskId)
    }
}
```

### 4. ViewModel

```kotlin title="viewmodel/TaskViewModel.kt"
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTask: Task? = null
)

sealed class TaskEvent {
    object LoadTasks : TaskEvent()
    data class SelectTask(val task: Task) : TaskEvent()
    data class ToggleTaskCompletion(val task: Task) : TaskEvent()
    data class DeleteTask(val taskId: Int) : TaskEvent()
    data class CreateTask(val title: String, val description: String) : TaskEvent()
}

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.LoadTasks -> loadTasks()
            is TaskEvent.SelectTask -> selectTask(event.task)
            is TaskEvent.ToggleTaskCompletion -> toggleTaskCompletion(event.task)
            is TaskEvent.DeleteTask -> deleteTask(event.taskId)
            is TaskEvent.CreateTask -> createTask(event.title, event.description)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                repository.getTasks().collect { tasks ->
                    _uiState.value = TaskUiState(
                        tasks = tasks,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar tareas: ${e.message}"
                )
            }
        }
    }

    private fun selectTask(task: Task) {
        _uiState.value = _uiState.value.copy(selectedTask = task)
    }

    private fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(completed = !task.completed)
                repository.updateTask(updatedTask)
                loadTasks()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar tarea: ${e.message}"
                )
            }
        }
    }

    private fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
                loadTasks()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar tarea: ${e.message}"
                )
            }
        }
    }

    private fun createTask(title: String, description: String) {
        viewModelScope.launch {
            try {
                val newTask = Task(
                    id = 0,
                    title = title,
                    description = description,
                    completed = false,
                    userId = 1
                )
                repository.createTask(newTask)
                loadTasks()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al crear tarea: ${e.message}"
                )
            }
        }
    }
}
```

### 5. UI con Compose

```kotlin title="ui/TaskScreen.kt"
@Composable
fun TaskScreen(viewModel: TaskViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Crear tarea")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    ErrorView(
                        error = uiState.error!!,
                        onRetry = { viewModel.onEvent(TaskEvent.LoadTasks) }
                    )
                }

                uiState.tasks.isEmpty() -> {
                    EmptyTasksView()
                }

                else -> {
                    TaskList(
                        tasks = uiState.tasks,
                        onTaskClick = { viewModel.onEvent(TaskEvent.SelectTask(it)) },
                        onToggleComplete = { viewModel.onEvent(TaskEvent.ToggleTaskCompletion(it)) },
                        onDelete = { viewModel.onEvent(TaskEvent.DeleteTask(it.id)) }
                    )
                }
            }
        }

        if (showCreateDialog) {
            CreateTaskDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { title, description ->
                    viewModel.onEvent(TaskEvent.CreateTask(title, description))
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskCard(
                task = task,
                onClick = { onTaskClick(task) },
                onToggleComplete = { onToggleComplete(task) },
                onDelete = { onDelete(task) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.completed) {
                        TextDecoration.LineThrough
                    } else null
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggleComplete() }
            )

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, "Eliminar")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Estás seguro de que quieres eliminar esta tarea?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva tarea") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(title, description) },
                enabled = title.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ErrorView(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
fun EmptyTasksView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No hay tareas",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Pulsa + para crear una nueva",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
```

## Room Database con corrutinas

Las corrutinas se integran perfectamente con Room:

```kotlin title="database/TaskDao.kt"
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val completed: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
```

## WorkManager con corrutinas

Para tareas en segundo plano que deben completarse incluso si la app se cierra:

```kotlin title="workers/SyncWorker.kt"
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Trabajo asíncrono
            val repository = TaskRepository(RetrofitClient.apiService)
            val tasks = repository.getTasks()

            // Guardar en base de datos local
            tasks.collect { taskList ->
                // Guardar...
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Programar el worker
val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
    15, TimeUnit.MINUTES
).build()

WorkManager.getInstance(context).enqueue(syncWorkRequest)
```

## Patrones avanzados

### Side Effects con Channels

```kotlin
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object NavigateBack : UiEvent()
}

class MyViewModel : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onSaveClick() {
        viewModelScope.launch {
            try {
                // Guardar datos
                _uiEvent.send(UiEvent.ShowSnackbar("Guardado correctamente"))
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }
}

@Composable
fun MyScreen(viewModel: MyViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.NavigateBack -> {
                    // Navegar atrás
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        // Contenido
    }
}
```

### Debouncing en búsquedas

```kotlin
class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(300) // Esperar 300ms después del último cambio
                .distinctUntilChanged() // Solo si cambió
                .collect { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        _searchResults.value = emptyList()
                    }
                }
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    private suspend fun performSearch(query: String) {
        // Buscar...
    }
}
```

## Mejores prácticas

1. **Usa viewModelScope**: Para corrutinas vinculadas al ViewModel
2. **Usa lifecycleScope**: Para corrutinas vinculadas a Activity/Fragment
3. **Maneja excepciones**: Siempre usa try-catch en operaciones de red
4. **Usa Dispatchers apropiados**: Main para UI, IO para red/BD
5. **Cancela corrutinas**: Aprovecha la cancelación automática de scopes
6. **Evita GlobalScope**: Siempre usa scopes estructurados
7. **StateFlow para estado**: Mejor que LiveData para Compose
8. **Channel para eventos únicos**: Snackbars, navegación, etc.

## Conclusión

Las corrutinas en Android con Jetpack Compose permiten:

- Código asíncrono simple y legible
- Gestión automática del ciclo de vida
- Mejor rendimiento y experiencia de usuario
- Integración perfecta con arquitectura MVVM
- Manejo robusto de errores

Dominar las corrutinas es esencial para desarrollar aplicaciones Android modernas y eficientes.
