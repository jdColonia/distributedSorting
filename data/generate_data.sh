#!/bin/bash

# Nombre del archivo y número de elementos
filename="bigData.txt"
num_elements=100

# Función para generar un número aleatorio entre un rango
generate_random_number() {
    min=$1
    max=$2
    echo $(shuf -i $min-$max -n 1)
}

# Crea el archivo de datos con números aleatorios
echo "Generando $num_elements números aleatorios en $filename ..."
for ((i=0; i<$num_elements; i++)); do
    random_number=$(generate_random_number 1 10000)
    echo $random_number >> $filename
done

echo "Filed saved: $filename"