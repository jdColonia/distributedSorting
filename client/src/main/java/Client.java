import com.zeroc.Ice.*;

public class Client {

    public static void main(String[] args) {
        try(Communicator communicator = Util.initialize(args, "client.cfg")) {
            run(communicator);
        }
    }

    private static void run(Communicator communicator) {
    
    }

}