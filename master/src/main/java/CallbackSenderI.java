import com.zeroc.Ice.Current;

import DistributedSorting.CallbackReceiverPrx;

public final class CallbackSenderI implements DistributedSorting.CallbackSender {

    @Override
    public void initiateCallback(CallbackReceiverPrx proxy, String message, Current current) {

    }

    @Override
    public void sendMessage(CallbackReceiverPrx proxy, String msg, Current current) {

    }

    @Override
    public void makeWorker(CallbackReceiverPrx proxy, String msg, Current current) {

    }

    @Override
    public void shutdown(Current current) {
        System.out.println("Shutting down...");
        try {
            current.adapter.getCommunicator().shutdown();
        } catch(com.zeroc.Ice.LocalException ex) {
            ex.printStackTrace();
        }
    }

}