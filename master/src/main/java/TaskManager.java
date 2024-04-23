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
        List<Container> containers = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        boolean areAllWorkersEmpty;
        do {
            areAllWorkersEmpty = true;
            for (Map.Entry<String, CallbackReceiverPrx> entry : workers.entrySet()) {
                String elements = entry.getValue().getHalfAndRemove();
                containers.add(new Container(entry.getValue(), elements));
                if (entry.getValue().verifyLength() != 0) {
                    areAllWorkersEmpty = false;
                    break;
                }
            }
            PriorityQueue<Pair<String, Container>> priorityQueue = new PriorityQueue<>(Comparator.comparing(Pair::getFirst));
            for (Container container : containers) {
                if (!container.getQueue().isEmpty()) {
                    String elementToAdd = container.getQueue().poll();
                    elementToAdd = elementToAdd.trim();
                    elementToAdd = elementToAdd.replaceAll("\\s+", "");
                    priorityQueue.add(new Pair<>(elementToAdd, container));
                }
            }
            while (!priorityQueue.isEmpty()) {
                Pair<String, Container> pair = priorityQueue.poll();
                Container container = pair.getSecond();
                String elementToSave = pair.getFirst();
                result.append(elementToSave).append("\n");
                if (!container.getQueue().isEmpty()) {
                    String element = container.getQueue().poll();
                    priorityQueue.add(new Pair<>(element, container));
                }
            }
            containers.clear();
        } while (!areAllWorkersEmpty);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        saveSortedData(result.toString());
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