import com.zeroc.Ice.Current;

public class CallbackReceiverI implements DistributedSorting.CallbackReceiver{

    @Override
    public void receiveMessage(String msg, Current current) {
        // Imprimir el mensaje recibido en la consola
        System.out.println("Message received: " + msg);
    }

    @Override
    public void startWorker(int from, int to, String filename, String basepath, Current current) {

    }

    @Override
    public String getHalfAndRemove(Current current) {
        return null;
    }

    @Override
    public int verifyLength(Current current) {
        return 0;
    }

}