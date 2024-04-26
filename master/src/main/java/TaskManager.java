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
        List<ComparableDouble> result = new ArrayList<>();
        StringBuilder resultSB = new StringBuilder();
        for (Map.Entry<String, CallbackReceiverPrx> entry : workers.entrySet()) {
            String elements = entry.getValue().getSortedList();
            if (!elements.isEmpty()) {
                String[] dataArray = elements.split(", ");
                List<ComparableDouble> subList = new ArrayList<>();
                for (String data : dataArray) {
                    subList.add(new ComparableDouble(Double.parseDouble(data)));
                }
                result.addAll(subList);
            }
        }
        result = mergeSort.mergeSort(result);
        for (ComparableDouble element : result) {
            resultSB.append(element).append("\n");
        }
        saveSortedData(resultSB.toString());
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        client.receiveMessage("Sorting completed for " + filename + ". Elapsed time: " + elapsedTime + " milliseconds");
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