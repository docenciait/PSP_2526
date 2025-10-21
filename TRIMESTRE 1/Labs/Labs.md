# Ejercicios y Labs

## Repositorios de laboratorios y ejercicios de clase

- [Laboratorios](https://github.com/docenciait/Labs_PSP2526)

## Manual java

- [Java](java.md)

## Ejercicio 1 – FCFS (First Come First Served)

Implementa un simulador de planificación con el algoritmo FCFS en un monoprocesador.

**Indicaciones técnicas**

- Define una clase `Proceso` con: `id`, `tiempoLlegada`, `tiempoCPU`, `tiempoFinalizacion`.
- Usa una **LinkedList** para representar la cola de listos (`readyQueue`), ya que el orden de llegada determina el orden de ejecución.
- Recorre los procesos en orden, acumulando el tiempo total, y calcula:

  - % CPU
  - Throughput

## Ejercicio 2 – SJF (Shortest Job First, no expropiativo)

### **Enunciado**

Crea un programa que implemente el algoritmo SJF no expropiativo en un monoprocesador.

### **Indicaciones técnicas**

- Usa una **PriorityQueue** (cola con prioridad) ordenada por el campo `tiempoCPU`.
- En cada instante en que la CPU queda libre, mete en la cola todos los procesos que ya hayan llegado.
- Extrae de la cola el proceso con menor `tiempoCPU` y ejecútalo hasta terminar.
- Calcula los tiempos de finalización, turnaround y espera.

### **Objetivo**

Comparar los tiempos promedio de turnaround y espera con los obtenidos en FCFS.

---

## Ejercicio 3 – Round Robin (q = 2)

Implementa el algoritmo Round Robin en un monoprocesador con quantum fijo de 2 unidades de tiempo.

### **Indicaciones técnicas**

- Usa una **Queue** (puede ser `LinkedList` o `ArrayDeque`) para modelar la cola circular de listos.
- Cada vez que un proceso obtiene la CPU:

  - Ejecútalo por `min(q, tiempoRestante)`.
  - Si todavía no ha terminado, re-encólalo al final de la cola.

- Lleva el control del tiempo global, encolando los procesos a medida que llegan.
- Guarda los tiempos de finalización y calcula turnaround y espera.

### **Objetivo**

Imprimir el orden de ejecución paso a paso (ejemplo: `t=0-2: P1`, `t=2-4: P2` …) y los tiempos finales de cada proceso.

---
