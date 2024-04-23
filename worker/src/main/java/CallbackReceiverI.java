import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Current;

public class CallbackReceiverI implements DistributedSorting.CallbackReceiver {

    private List<ComparableDouble> sortedList;
    private  MergeSort<ComparableDouble> mergeSort = new MergeSort<ComparableDouble>();

    @Override
    public void receiveMessage(String msg, Current current) {
        // Imprimir el mensaje recibido en la consola
        System.out.println("Message received: " + msg);
    }

    @Override
    public void startWorker(int from, int to, String filename, String basepath, Current current) {
        // Imprimir un mensaje indicando el inicio del trabajo con los parámetros recibidos
        System.out.println("Start of work requested in the Worker. Parameters:");
        System.out.println(" - Range: from " + from + " to " + to);
        System.out.println(" - File: " + filename);
        System.out.println(" - Base path: " + basepath);
        // Leer el archivo y procesarlo
        readAndProcessFileRange(basepath + filename, from, to);
    }

    @Override
    public String getHalfAndRemove(Current current) {
        // Definir un límite arbitrario para la cantidad de datos a procesar
        int half = 30000;
        // Verificar si la lista de datos tiene al menos la mitad de los elementos
        if (sortedList.size() < half) {
            // Si la lista tiene menos elementos que la mitad, envia todos los datos disponibles
            List<ComparableDouble> halfList = sortedList.subList(0, sortedList.size());
            // Limpia la lista de datos para indicar que todos los datos han sido enviados
            sortedList = new ArrayList<>();
            // Devuelve la mitad de los datos como una cadena
            return halfList.toString();
        } else {
            // Si la lista tiene más elementos que la mitad, envia la mitad de los datos disponibles
            List<ComparableDouble> halfList = sortedList.subList(0, half);
            // Actualiza la lista de datos para eliminar la mitad que ya ha sido enviada
            sortedList = sortedList.subList(half, sortedList.size());
            // Devuelve la mitad de los datos como una cadena
            return halfList.toString();
        }
    }

    @Override
    public int verifyLength(Current current) {
        // Imprime la longitud actual de los datos en la consola
        System.out.println("Length verification in the Worker: " + sortedList.size());
        // Devuelve la longitud actual de los datos
        return sortedList.size();
    }

    private void readAndProcessFileRange(String filePath, int from, int to) {
        // Inicializa el tiempo
        long startTime = System.currentTimeMillis();
        // Inicializa una lista para almacenar las líneas leídas del archivo
        List<ComparableDouble> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;
            // Itera sobre cada línea del archivo
            while ((line = reader.readLine()) != null) {
                // Verifica si la línea se encuentra dentro del rango especificado
                if (lineNumber >= from && lineNumber <= to) {
                    // Si está dentro del rango, la añadimos a la lista de datos
                    Double value = Double.parseDouble(line);
                    dataList.add(new ComparableDouble(value));
                }
                // Incrementa el número de línea para la siguiente iteración
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Imprime un mensaje indicando que el archivo ha sido leído
        System.out.println("The file has been read.");
        // Procesa los datos leídos y los ordena
        sortedList = mergeSort.mergeSort(dataList);
        // Imprime un mensaje indicando que el archivo fue ordenado
        System.out.println("The list was ordered");
        // Finaliza la medición del tiempo
        long endTime = System.currentTimeMillis();
        // Calcula el tiempo transcurrido
        long elapsedTime = endTime - startTime;
        // Imprime el tiempo transcurrido
        System.out.println("File reading time: " + elapsedTime + " milliseconds");
    }

}