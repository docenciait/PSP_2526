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
