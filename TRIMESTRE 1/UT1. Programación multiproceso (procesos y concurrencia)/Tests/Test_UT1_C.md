# Test UT1 - C

Responde a cada pregunta eligiendo la opción correcta (sólo una respuesta es válida).

---

1. ¿Cuál es la diferencia fundamental entre un programa y un proceso?

- a) Un programa es pasivo mientras que un proceso es activo en ejecución
- b) Un programa está en memoria y un proceso en disco
- c) Un proceso solo tiene un hilo
- d) Son conceptos idénticos

2. En un sistema monoprocesador, ¿cómo se logra la multiprogramación?

- a) Duplicando la velocidad del procesador
- b) Mediante alternancia rápida entre procesos usando el planificador del SO
- c) Ejecutando procesos de forma secuencial uno tras otro
- d) Ejecutando todos los procesos a la vez

3. ¿Qué es la concurrencia en sistemas operativos?

- a) Un concepto obsoleto
- b) La ejecución secuencial de tareas
- c) Solo es posible con múltiples procesadores
- d) La capacidad de realizar varias tareas de forma simultánea en un procesador

4. ¿Cuál es la diferencia entre concurrencia y paralelismo?

- a) Paralelismo es más lento que concurrencia
- b) En concurrencia los procesos compiten por un procesador, en paralelismo cada uno usa un procesador diferente
- c) Concurrencia necesita múltiples procesadores, paralelismo no
- d) No hay diferencia

5. ¿Qué es un sistema distribuido según Tanenbaum?

- a) Una colección de computadores independientes que aparecen como un único sistema coherente
- b) Un sistema que solo funciona en la nube
- c) Un sistema con múltiples procesadores en el mismo equipo
- d) Un sistema que ejecuta múltiples procesos en un solo ordenador

6. ¿Qué es el algoritmo Round Robin?

- a) Los procesos se ejecutan según su prioridad
- b) Los procesos se ejecutan en orden de llegada
- c) Los procesos se asignan un tiempo equitativo en intervalos llamados quantum
- d) Los procesos cortos se ejecutan primero

7. ¿Cuál es la principal desventaja del algoritmo de planificación por prioridades?

- a) Requiere hardware especial
- b) Los procesos de baja prioridad pueden sufrir inanición
- c) Es muy lento
- d) No funciona en monoprocesador

8. ¿Cuál es la ventaja principal del algoritmo Round Robin con respecto a FCFS?

- a) No necesita cambios de contexto
- b) Garantiza equidad en la distribución del tiempo de CPU entre todos los procesos
- c) Es más rápido
- d) Usa menos memoria

9. ¿Qué es el PCB (Bloque de Control de Procesos)?

- a) El identificador del proceso
- b) Un registro especial del procesador
- c) Una estructura que almacena información necesaria para restaurar un proceso tras su suspensión
- d) El archivo ejecutable del programa

10. ¿Qué es un cambio de contexto?

- a) El cambio de prioridad de un proceso
- b) La modificación de variables de entorno
- c) El reemplazo de un proceso por otro en el procesador guardando información del suspendido
- d) El cambio de directorio de trabajo de un proceso

11. ¿Qué es la prioridad de un proceso en el planificador?

- a) La cantidad de memoria que usa
- b) El grado de preferencia para obtener tiempo de CPU
- c) El tiempo total de ejecución
- d) El número de veces que se ha ejecutado

12. ¿Qué identifica de forma única a cada proceso en Linux?

- a) Su directorio de trabajo
- b) Su usuario propietario
- c) Su PID (Process ID)
- d) Su nombre

13. ¿Cuál es la función del PPID (Parent Process ID)?

- a) Es el puerto de comunicación
- b) Es la prioridad del proceso
- c) Es el identificador del proceso padre
- d) Es el identificador del siguiente proceso

14. ¿Qué es un hilo?

- a) Un archivo
- b) Un proceso ligero
- c) Un usuario
- d) Un periférico

15. ¿Qué mecanismo permite la comunicación entre procesos?

- a) Mouse
- b) Pipes
- c) Teclado
- d) Monitor

16. ¿Qué sucede cuando un proceso pasa del estado bloqueado al listo?

- a) Se pausa indefinidamente
- b) Se elimina de la memoria
- c) Queda pendiente de ser planificado por el SO para pasar a ejecución
- d) Pasa inmediatamente a ejecución

17. ¿Qué es la planificación de procesos?

- a) El registro de procesos ejecutados
- b) La memoria asignada a cada proceso
- c) El mecanismo que decide qué proceso se ejecuta y cuándo
- d) El archivo de configuración del SO

18. ¿Cuál es la característica principal del algoritmo FCFS (First Come First Served)?

- a) Se alternan procesos en intervalos de tiempo fijos
- b) Los procesos de mayor prioridad se ejecutan primero
- c) Los procesos se ejecutan en el orden en que llegan
- d) Los procesos cortos se ejecutan primero

19. ¿Cuál es el principal problema del algoritmo SJF (Shortest Job First)?

- a) Es más lento que FCFS
- b) No funciona en monoprocesador
- c) Puede causar inanición de procesos largos
- d) Es demasiado complejo

20. ¿Qué diferencia hay entre planificación sin desalojo y apropiativa?

- a) Son sinónimos
- b) La apropiativa no existe en sistemas modernos
- c) Sin desalojo un proceso se ejecuta hasta terminar, con desalojo puede ser interrumpido
- d) No hay diferencia

21. En Java, ¿cuál es la clase que gestiona los atributos de un proceso antes de su ejecución?

- a) ProcessBuilder
- b) Runtime
- c) ProcessHandle
- d) Process

22. ¿Cuáles son los dos constructores de ProcessBuilder?

- a) Uno con List<String> y otro con String... (varargs)
- b) Uno sincrónico y otro asincrónico
- c) Uno para procesos y otro para hilos
- d) Una versión para Windows y otra para Linux

23. ¿Cómo se deben pasar argumentos a ProcessBuilder?

- a) Como un array separando comando y argumentos en partes diferentes
- b) Como un archivo de configuración
- c) Solo como variables de entorno
- d) Como una única cadena de texto

24. ¿Qué devuelve el método start() de ProcessBuilder?

- a) Una referencia al proceso en forma de objeto Process
- b) El código de salida del proceso
- c) El PID del nuevo proceso
- d) Un booleano indicando éxito

25. ¿Cuál es el código de finalización que indica ejecución correcta?

- a) 0
- b) 255
- c) -1
- d) 1

26. ¿Cuál es la ventaja de los hilos frente a los procesos?

- a) Mayor consumo de recursos
- b) No pueden ejecutarse
- c) Menor consumo de recursos
- d) No pueden comunicarse

27. ¿Qué método se usa para esperar a que un proceso hijo termine?

- a) waitFor()
- b) exitValue()
- c) isAlive()
- d) destroy()

28. ¿Cuál es la función del directorio de trabajo en ProcessBuilder?

- a) Definir dónde se ejecuta el proceso
- b) Especificar variables de entorno
- c) Configurar permisos del proceso
- d) Almacenar archivos temporales

29. ¿Qué información proporciona ProcessHandle.Info?

- a) Comando, argumentos, instante de inicio, CPU usado y usuario
- b) Solo la memoria utilizada
- c) Solo el estado del proceso
- d) Solo el PID del proceso

30. ¿Cuál es el método para obtener información sobre el proceso actual en Java?

- a) ProcessHandle.current()
- b) Runtime.current()
- c) System.getProcess()
- d) Process.getInfo()

---

## Hoja de respuestas

| Pregunta | Respuesta | Pregunta | Respuesta | Pregunta | Respuesta |
| -------- | --------- | -------- | --------- | -------- | --------- |
| 1        | a         | 11       | b         | 21       | a         |
| 2        | b         | 12       | c         | 22       | a         |
| 3        | d         | 13       | c         | 23       | a         |
| 4        | b         | 14       | b         | 24       | a         |
| 5        | a         | 15       | b         | 25       | a         |
| 6        | c         | 16       | c         | 26       | c         |
| 7        | b         | 17       | c         | 27       | a         |
| 8        | b         | 18       | c         | 28       | a         |
| 9        | c         | 19       | c         | 29       | a         |
| 10       | c         | 20       | c         | 30       | a         |

---

## Rúbrica de evaluación

| Calificación  | Rango de aciertos |
| ------------- | ----------------- |
| Sobresaliente | 27-30 (90%-100%)  |
| Notable       | 24-26 (80%-89%)   |
| Bien          | 21-23 (70%-79%)   |
| Aprobado      | 18-20 (60%-69%)   |
| Suspenso      | 0-17 (0%-59%)     |
