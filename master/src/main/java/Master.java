import com.zeroc.Ice.*;

public class Master {
    
    public static void main(String[] args) {
        int status = 0;
        try (Communicator communicator = Util.initialize(args,"master.cfg")) {
            ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Server");         
            CallbackSenderI callbackSender = new CallbackSenderI();
            adapter.add(callbackSender, Util.stringToIdentity("callbackSender"));
            adapter.activate();
            communicator.waitForShutdown();
        } 
        System.exit(status);
    }
}
