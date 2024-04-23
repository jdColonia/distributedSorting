import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import DistributedSorting.CallbackReceiverPrx;

public class Container{
    
    private CallbackReceiverPrx worker;
    private Queue<String> queue;

    public Container(CallbackReceiverPrx worker, String queue){
        this.worker = worker;
        String elements = queue.substring(1, queue.length() - 1);
        this.queue = new LinkedList<>(Arrays.asList(elements.split(", ")));
    }

    public Queue<String> getQueue() {
        return queue;
    }

}