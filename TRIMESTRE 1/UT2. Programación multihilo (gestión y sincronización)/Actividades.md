# Actividades

1. Ejercicio: crear un programa que lance 10 hilos de ejecución donde a cada hilo se le pasará la base y la altura de un triángulo, y cada hilo ejecutará el cálculo del área de dicho triángulo informando de qué base y qué altura recibió y cual es el área resultado.

> Solución Clase Thread

```Java
public class TrianguloHilo extends Thread {
    private double base;
    private double altura;

    public TrianguloHilo(double base, double altura) {
        this.base = base;
        this.altura = altura;
    }

    @Override
    public void run() {
        double area = (base * altura) / 2;
        System.out.println("Base: " + base + ", Altura: " + altura + " => Área: " + area);
    }

    public static void main(String[] args) {
        double[] bases = {3, 5, 7, 2, 6, 4, 8, 9, 1, 10};
        double[] alturas = {4, 2, 5, 6, 3, 7, 2, 8, 9, 1};

        for (int i = 0; i < 10; i++) {
            TrianguloHilo hilo = new TrianguloHilo(bases[i], alturas[i]);
            hilo.start();
        }
    }
}
```

> Solución Runnable

```java
public class TrianguloRunnable implements Runnable {
    private double base;
    private double altura;

    public TrianguloRunnable(double base, double altura) {
        this.base = base;
        this.altura = altura;
    }

    @Override
    public void run() {
        double area = (base * altura) / 2;
        System.out.println("Base: " + base + ", Altura: " + altura + " => Área: " + area);
    }

    public static void main(String[] args) {
        double[] bases = {3, 5, 7, 2, 6, 4, 8, 9, 1, 10};
        double[] alturas = {4, 2, 5, 6, 3, 7, 2, 8, 9, 1};

        for (int i = 0; i < 10; i++) {
            Thread hilo = new Thread(new TrianguloRunnable(bases[i], alturas[i]));
            hilo.start();
        }
    }
}
```

- Ejercicio 2: Lanzar varios hilos con Thread
  Crea un programa que lance 5 hilos usando la clase Thread y cada hilo imprima su nombre y un mensaje indicando que ha iniciado y que ha finalizado tras dormir 1 segundo.

- Ejercicio 3: Usar la función thread() de Kotlin
  Haz un programa que cree 3 hilos usando la función thread() de Kotlin. Cada hilo debe imprimir su nombre y realizar una cuenta regresiva desde 3 hasta 1, esperando medio segundo entre cada número.

- Ejercicio 4: Extender la clase Thread
  Implementa una clase que extienda Thread y sobrescriba el método run() para mostrar un mensaje personalizado. Crea e inicia dos instancias de esta clase y espera a que ambas terminen usando join().
