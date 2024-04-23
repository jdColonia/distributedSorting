import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.LocalException;

import DistributedSorting.CallbackReceiverPrx;

public final class CallbackSenderI implements DistributedSorting.CallbackSender {

    private static final String BASE_PATH = "data/";

    private Map<String, CallbackReceiverPrx> clients = new HashMap<>();
    private Map<String, CallbackReceiverPrx> workers = new HashMap<>();
    private MergeSort<ComparableString> mergeSort = new MergeSort<>();

    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, String message, Current current) {

    }

    @Override
    public void sendMessage(CallbackReceiverPrx proxy, String msg, Current current) {
        System.out.println("Initiating callback");

        try {
            String out = evaluateOrder(msg, proxy);
            proxy.receiveMessage(out);
        } catch (LocalException | IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void makeWorker(CallbackReceiverPrx proxy, String msg, Current current) {

    }

    @Override
    public void shutdown(Current current) {
        System.out.println("Shutting down...");
        try {
            current.adapter.getCommunicator().shutdown();
        } catch(LocalException ex) {
            ex.printStackTrace();
        }
    }

    private String evaluateOrder(String msg, CallbackReceiverPrx proxy) throws IOException {
        String[] msgArray = msg.split("-");
        String order = msgArray[msgArray.length - 1];
        String hostname = msgArray[msgArray.length - 2];

        if (order.startsWith("register as worker")) {
            System.out.println("Registering worker");
            registerWorker(hostname, proxy);
        } else if (order.startsWith("dist_sorter")) {
            String[] orderArray = msg.split(":");
            String filename = orderArray[orderArray.length - 1];
            registerClient(hostname, proxy);
            handleDistSorting(filename, proxy);
        } else {
            return "Order not recognized";
        }

        return "Success";
    }

    private void registerWorker(String hostname, CallbackReceiverPrx proxy) {
        workers.put(hostname, proxy);
    }

    private void registerClient(String hostname, CallbackReceiverPrx proxy) {
        clients.putIfAbsent(hostname, proxy);
    }

    private void handleDistSorting(String filename, CallbackReceiverPrx proxy) throws IOException {
        if (!verifyFileExists(filename)) {
            proxy.receiveMessage("File does not exist");
            return;
        }

        if (verifyFileWasSorted(filename)) {
            proxy.receiveMessage("File was already sorted");
            return;
        }

        if (workers.size() <= 1) {
            monolithicSort(filename, proxy);
        } else {
            int numberOfLines = getNumberOfLines(filename);
            TaskManager tm = divideWorkWithWorkers(new TaskManager(proxy, filename, BASE_PATH, workers), numberOfLines, workers.size());
            startSorting(tm);
        }
    }

    private boolean verifyFileExists(String filename) {
        File f = new File(BASE_PATH + filename);
        return f.exists() && !f.isDirectory();
    }

    private boolean verifyFileWasSorted(String filename) {
        File f = new File(BASE_PATH + "sorted." + filename);
        return f.exists() && !f.isDirectory();
    }

    private int getNumberOfLines(String filename) throws IOException {
        try (Stream<String> fileStream = Files.lines(Paths.get(BASE_PATH + filename))) {
            return (int) fileStream.count();
        }
    }

    private TaskManager divideWorkWithWorkers(TaskManager tm, int numberOfLines, int numWorkers) {
        int segmentSize = numberOfLines / numWorkers;
        for (int i = 0; i < numWorkers; i++) {
            int start = i * segmentSize + 1;
            int end = (i + 1) * segmentSize;
            tm.addTask(new Task(start, end));
        }

        return tm;
    }

    private void startSorting(TaskManager taskManager) {
        CompletableFuture.runAsync(taskManager);
    }

    private void monolithicSort(String filename, CallbackReceiverPrx proxy) throws IOException {
        long startTime = System.currentTimeMillis();

        List<ComparableString> dataList = readFile(BASE_PATH + filename);
        dataList = mergeSort.mergeSort(dataList);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Sorting time of " + filename + ": " + elapsedTime + " milliseconds");
        proxy.receiveMessage("Sorting time of " + filename + ": " + elapsedTime + " milliseconds");

        saveSortedData(dataList, filename);
    }

    private List<ComparableString> readFile(String filePath) throws IOException {
        List<ComparableString> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(new ComparableString(line));
            }
        }
        return dataList;
    }

    private void saveSortedData(List<ComparableString> result, String filename) throws IOException {
        File file = new File(BASE_PATH + "sorted." + filename);

        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (ComparableString cc : result) {
                writer.write(cc.getValue() + "\n");
            }
        }
    }

}