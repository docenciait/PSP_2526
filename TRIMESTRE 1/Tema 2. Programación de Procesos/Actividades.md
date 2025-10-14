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
