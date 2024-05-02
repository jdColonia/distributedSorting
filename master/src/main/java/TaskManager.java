import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import DistributedSorting.CallbackReceiverPrx;

public class TaskManager implements Runnable {

    private CallbackReceiverPrx client;
    private String filename;
    private String basePath;
    private Queue<Task> tasks = new LinkedList<>();
    private Map<String, CallbackReceiverPrx> workers;
    private MergeSort<ComparableDouble> mergeSort = new MergeSort<ComparableDouble>();

    public TaskManager(CallbackReceiverPrx client, String filename, String basePath, Map<String, CallbackReceiverPrx> workers) {
        this.client = client;
        this.filename = filename;
        this.basePath = basePath;
        this.workers = workers;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void run() {
        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (Map.Entry<String, CallbackReceiverPrx> entry : workers.entrySet()) {
                CallbackReceiverPrx worker = entry.getValue();
                Task task = tasks.poll();
                if (task != null) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        worker.startWorker(task.getFrom(), task.getTo(), filename, basePath);
                    });
                    futures.add(future);
                }
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            handleSorting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSorting() throws IOException {
        long startTime = System.currentTimeMillis();
        List<List<ComparableDouble>> sortedLists = new ArrayList<>();
        StringBuilder resultSB = new StringBuilder();
        for (Map.Entry<String, CallbackReceiverPrx> entry : workers.entrySet()) {
            String elements = entry.getValue().getSortedList();
            if (!elements.isEmpty()) {
                List<ComparableDouble> subList = new ArrayList<>();
                String[] dataArray = elements.split(", ");
                for (String data : dataArray) {
                    subList.add(new ComparableDouble(Double.parseDouble(data)));
                }
                sortedLists.add(subList);
            }
        }
        List<ComparableDouble> result = mergeKSortedArrays(sortedLists);
        for (ComparableDouble element : result) {
            resultSB.append(element).append("\n");
        }
        saveSortedData(resultSB.toString());
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        client.receiveMessage("Sorting completed for " + filename + ". Elapsed time: " + elapsedTime + " milliseconds");
    }

    private List<ComparableDouble> mergeKSortedArrays(List<List<ComparableDouble>> arrays) {
        // Utilizamos un min heap para obtener eficientemente el mínimo de todas las listas
        PriorityQueue<ArrayContainer> minHeap = new PriorityQueue<>(Comparator.comparingDouble(a -> a.array.get(a.index).getValue()));
        // Agregamos el primer elemento de cada lista al min heap
        for (List<ComparableDouble> array : arrays) {
            if (!array.isEmpty()) {
                minHeap.offer(new ArrayContainer(array, 0));
            }
        }
        List<ComparableDouble> mergedList = new ArrayList<>();
        // Continuamos extrayendo y fusionando elementos hasta que el min heap esté vacío
        while (!minHeap.isEmpty()) {
            ArrayContainer container = minHeap.poll();
            mergedList.add(container.array.get(container.index));
            // Si quedan elementos en la lista original de donde se extrajo el mínimo,
            // agregamos el siguiente elemento al min heap
            if (container.index + 1 < container.array.size()) {
                minHeap.offer(new ArrayContainer(container.array, container.index + 1));
            }
        }
        return mergedList;
    }
    
    private void saveSortedData(String result) throws IOException {
        File file = new File(basePath + "sorted." + filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);
        writer.write(result);
        writer.close();
    }

}