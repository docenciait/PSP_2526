## A1

Crea una nuevo proyecto Java (package psp.actividades y como clase principal U2A1_Shutdowner). Usando la línea de comandos, pide al usuario qué acción quiere realizar (apagar, reiniciar o suspender) y cuánto tiempo quiere dejar antes de realizar la acción de apagado del sistema.

Busca información sobre el funcionamiento del comando shutdown en GNU/Linux y haz que tu aplicación funcione para ambos sistemas.

La aplicación tiene que preparar el comando correcto para la selección que haya hecho el usuario y para el sistema operativo en el que la esté ejecutando.

Muestra por consola el resultado del método ProcessBuilder.command() de forma legible.

## A2

Crea un paquete Java ( U2A2_DirectorioTrabajo y como clase principal psp.activities.U2A2_WorkingDirectory) Escribe un programa que ejecute el comando ls o dir. Modifica el valor de la propiedad user.dir. En la misma aplicación, cambiar el directorio de trabajo a la carpeta c:/temp o /tmp, dependiendo del sistema operativo.

Muestra el valor devuelto por el método directory():

- Después de crear la instancia de ProcessBuilder
- Después de cambiar el valor de user.dir
- Después de cambiar el directorio de trabajo al directorio temporal del sistema.
  En este momento tu programa todavía no mostrará ningún listado.

## A3

Crea un nuevo paquete: (configura como nombre del proyecto U2A3_ExitValue y como clase principal psp.activities.U2A3_ExitValue). Prepara un programa que ejecute varios comandos (notepad, calc, comandos shell) uno detrás de otro, de forma secuencial y haz que tu programa obtenga el código de finalización de cada uno de ellos. Para cada programa imprime el nombre y su código de finalización.

Prueba a poner aplicaciones que no existan o comandos con parámetros incorrectos.

¿Qué hace Netbeans si pones System.exit(10) para acabar tu programa?. Fíjate en la consola. ¿Qué hace Netbeans si pones System.exit(0) para acabar tu programa.? Cuál es entonces el valor por defecto?

# A4

Crea un nuevo paquete Java (configura como nombre del proyecto U2A3_Lanzador y como clase principal psp.activities.U2A4_Lanzador).

Ahora, en el mismo proyecto y dentro del mismo paquete crea otra clase, U2A4_Lanzado, con un método main que recibirá el nombre del programa que debe ejecutar como parámetro del método main(args). Haz que esta aplicación cree un nuevo proceso para ejecutar el programa recibido como parámetro.

La clase terminará devolviendo como código de finalización el que el programa lanzado le haya devuelto a ella.

Método System.exit()

Cero. El código cero debe devolverse cuando la ejecución del proceso haya ido bien, esto es, que ha terminado su ejecución sin problemas. Distinto de cero. Un código distinto de cero indica una terminación con errores. Java nos permite usar códigos diferentes para los diferentes tipos de error.

Por último, podemos hacer que U2A4_Lanzador pregunte al usuario qué aplicación quiere ejecutar y pasársela a la clase U2A4_Lanzado.

En Lanzador recoge el código de finalización de Lanzado y muestra un mensaje indicando si el proceso terminó bien o con errores.
