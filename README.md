This repository contains the implementation of a distributed sorting using Zero-C or ICE middleware and Java

# distributedSorting

*Aplicación de distribucion* implementada en Java e ICE que permite a los usuarios *utilizar un algoritmo de ordenamiento de datos*.

"Implementacion realizada mediante una distribucion de tipo *MergeSort*."

# Integrantes:

- Juan David Colonia - A00395956
- David Santiago Malte - A00368867
- David Henao Salazar - A00394033

# Instrucciones de uso del programa:

- Paso 1: Abrir la terminal dentro del proyecto de ICE.
- Paso 2: Rehacer la construccion (build) de la compilacion de las clases mediante el comando *.\gradlew build*
- Paso 3: Ejecutar *Server.jar* mediante el comando *java -jar /direccion_de_clase* (Esto puede ser en otro ordenador)
- Paso 4 Ejecutar *Worker.jar* con el mismo comando anterior, teniendo en cuenta la compilacion específica (este worker puede ser ejecutado multiples veces para el proceso de la distribucion optima) (Esto puede ser en otro ordenador)
- Paso 5: Ejecutar *Client.jar* con el mismo comando anterior, teniendo en cuenta la compilacion específica
- Pso 6: Tener listo el *archivo* a probar/distribuir dentro de la carpeta /data
- Paso 6: El programa va a desplegar un menú con opciones, la *opcion uno* para usar la distribución y la *opcion dos* para poder salir, para ponerlo a prueba, se selecciona la *opcion uno* y se escribe *sort:<filename>*, reemplazando *filename* con el nombre del archivo a probar
- Paso 7: Cuando se necesite acabar el proceso, se da a salir y se cierra  el proceso.

> [!IMPORTANT]
> La implementación puede llevarse a cabo ya sea en un único equipo utilizando distintas consolas, o desde múltiples equipos. Para que la implementación funcione correctamente, es imprescindible contar con el nombre de host del equipo. Por favor, dirígete al método `evaluateOrder` de la clase `CallbackSenderI` en el paquete `master`, y comenta la línea correspondiente a la ejecución que estés utilizando.

# Informe
[Informe - Google Colab](https://colab.research.google.com/drive/1BfUfXcEtnjCBmH0zJXnpBhkRcMadawx6?usp=sharing)
