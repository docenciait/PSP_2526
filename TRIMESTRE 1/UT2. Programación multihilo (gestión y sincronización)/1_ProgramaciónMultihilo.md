# Tema 2. Programación Multihilo y concurrente

## 2.1 Programación concurrente, paralela y distribuida

#### Programación concurrente

Es la programación de aplicaciones capaces de realizar varias tareas de forma simultánea utilizando hilos o threads. En este caso todas las tareas compiten por el uso del procesador (lo más habitual es disponer sólo de uno) y en un instante determinado sólo una de ellas se encuentra en ejecución. Además, habrá que tener en cuenta que diferentes hilos pueden compartir información entre sí y eso complica mucho su programación y coordinación.

#### Programación paralela

Es la programación de aplicaciones que ejecutan tareas de forma paralela, de forma que no compiten por el procesador puesto que cada una de ellas se ejecuta en uno diferente. Normalmente buscan resultados comunes dividiendo el problema en varias tareas que se ejecutan al mismo tiempo.

#### Programación distribuida

Es la programación de aplicaciones en las que las tareas a ejecutar se reparten entre varios equipos diferentes (conectados en red, a los que llamaremos nodos). Juntos, estos equipos, forman lo que se conoce como un _Sistema Distribuido_, que busca formar redes de equipos que trabajen con un fin común [1)](https://psp.codeandcoke.com/apuntes:threads#fn__1)

[![concurrencia.jpg](https://psp.codeandcoke.com/_media/apuntes:concurrencia.jpg "concurrencia.jpg")](https://psp.codeandcoke.com/_detail/apuntes:concurrencia.jpg?id=apuntes%3Athreads "apuntes:concurrencia.jpg")

Figure 2: Programación concurrente / paralela

[![distribuida.jpg](https://psp.codeandcoke.com/_media/apuntes:distribuida.jpg "distribuida.jpg")](https://psp.codeandcoke.com/_detail/apuntes:distribuida.jpg?id=apuntes%3Athreads "apuntes:distribuida.jpg")

Figure 3: Programación distribuida

### ¿Qué son los hilos?

Un hilo o thread es cada una de las tareas que puede realizar de forma simultánea una aplicación. Por defecto, toda aplicación dispone de un único hilo de ejecución, al que se conoce como _hilo principal_. Si dicha aplicación no despliega ningún otro hilo, sólo será capaz de ejecutar una tarea al mismo tiempo en ese hilo principal.

Así, para cada tarea adicional que se quiera ejecutar en esa aplicación, se deberá lanzar un nuevo hilo o thread. Para ello, todos los lenguajes de programación, como Java, disponen de una API para crear y trabajar con ellos.

En cualquier caso, es muy importante conocer los estados en los que se pueden encontrar un hilo. Estos estados se suelen representar mediante un gráfico como el que sigue:

#### Estados de un hilo

[![](https://psp.codeandcoke.com/_media/apuntes:estados_hilo.png)](https://psp.codeandcoke.com/_detail/apuntes:estados_hilo.png?id=apuntes%3Athreads "apuntes:estados_hilo.png")

Figure 4: Estados de un hilo

## Programación multihilo en Java

### Creación y ejecución de un hilo

Para la creación de hilos en Java disponemos de varias vías, combinando el uso de la clase [Thread](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html "https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html") y el interface [Runnable](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html "https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html") según nos interese:

- Podemos utiliza la clase `Thread` heredando de ella. Es quizás la forma más cómoda porque una clase que hereda de `Thread` se convierte automáticamente en un hilo. Tiene una pega: esa clase ya no podrá heredera de ninguna otra, por lo que si la arquitectura de nuestra aplicación lo requiere ya no podríamos.
- Si tenemos la limitación que acabamos de comentar para el primer caso, podemos implementar el interface `Runnable` de forma que la clase que nosotros estamos implementado podrá además heredar sin ninguna limitación. Sólo cambia un poco la forma de trabajar directamente con la clase hilo.
- Por otra parte también podemos crear un hilo utilizando una clase anónima. No es un método que se recomiende pero en algunos casos, cuando la clase que hace de hilo no va a tener una estructura concreta es bastante cómodo hacerlo de esta manera.

#### Crear un hilo heredando de la clase Thread

En este caso, la clase `Tarea` se conviene automáticamente en un hilo por el mero hecho de heredar de `Thread`. Sólo tenemos que tener en cuenta que, al heredar de esta clase, tenemos que implementar el método `run()` y escribir en él el código que queremos que esta clase ejecute cuando se lance como un hilo con el método `start()` (que también hereda de `Thread`)

```java
public class Tarea extends Thread {
  @Override
  public void run() {
    for (int i = 0; i < 10; i++) {
      System.out.println("Soy un hilo y esto es lo que hago");
    }
  }
}

public class Programa {
  public static void main(String args[]) {
    Tarea tarea = new Tarea();
    tarea.start();
    System.out.println("Yo soy el hilo principal y sigo haciendo mi trabajo");
    System.out.println("Fin del hilo principal");
  }
}
```

#### Crear un hilo implementado el interfaz Runnable

En este caso, suponemos que necesitamos que nuestra clase hilo herede de una segunda clase. En este caso, la clase deberá además implementar el interfaz `Runnable` y, como en el primer caso, implementar el método `run()` con la misma idea que en el punto anterior. Más adelante, tendremos que crear un objeto directamente de la clase `Thread` y pasarle como parámetro al constructor un objeto de nuestra clase hilo. De esa manera, el objeto `Thread` será un hilo que se comportará como el método `run()` de nuestra clase `Tarea` haya definido.

```java
public class OtraClase {
  // ...
}

public class Tarea extends OtraClase implements Runnable {
  @Override
  public void run() {
    for (int i = 0; i < 10; i++) {
      System.out.println("Soy un hilo y esto es lo que hago");
    }
  }
}

public class Programa {
  public static void main(String args[]) {
    Tarea tarea = new Tarea();
    Thread hilo = new Thread(tarea);
    hilo.start();
    System.out.println("Yo soy el hilo principal y sigo haciendo mi trabajo");
    System.out.println("Fin del hilo principal");
  }
}
```

#### Crear un hilo implementado una clase anónima

Por último, implementar una clase anónima también permite crear hilos aunque sólo se recomienda para casos en los que la clase que se convierte en un hilo no tenga una estructura muy compleja ya que quedaría un código bastante ilegible.

```java
public class Programa {
  public static void main(String args[]) {
    Thread hilo = new Thread(new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 10; i++) {
          System.out.println("Soy un hilo y esto es lo que hago");
        }
      }
    });
    hilo.start();
    System.out.println("Yo soy el hilo principal y sigo haciendo mi trabajo");
    System.out.println("Fin del hilo principal");
  }
}
```

En cualquier caso tenemos que tener siempre en cuenta las siguientes consideraciones:

- Siempre se debe sobreescribir (`Override`) el método `run()` e implementar allí lo que tiene que hacer el hilo
- Podemos hacer que el hilo haga un número finito de cosas o bien que esté siempre en segundo plano (tendremos que asegurar que el método `run()` se ejecuta de forma continuada)(¿cómo se hace eso?)
- Los problemas vienen cuando existen varios hilos. Hay que tener en cuenta que pueden compartir datos y código y encontrarse en diferentes estados de ejecución
- La ejecución de nuestra aplicación será _thread-safe_ si se puede garantizar una correcta manipulación de los datos que comparten los hilos de la aplicación sin resultados inesperados (más adelante veremos cómo)
- Además, en el caso de aplicaciones multihilo, también nos puede interesar sincronizar y comunicar unos hilos con otros
