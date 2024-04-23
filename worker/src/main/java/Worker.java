import java.net.InetAddress;
import java.net.UnknownHostException;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import DistributedSorting.CallbackReceiverPrx;
import DistributedSorting.CallbackSenderPrx;

public class Worker {

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "worker.cfg")) {
            run(communicator);
        }
    }

    private static void run(Communicator communicator) {
        // Obtener el proxy del sender para enviar mensajes al servidor
        CallbackSenderPrx sender = CallbackSenderPrx.checkedCast(
                communicator.propertyToProxy("CallbackSender.Proxy")).ice_twoway().ice_timeout(-1).ice_secure(false);

        if (sender == null) {
            System.err.println("Proxy invalid.");
            return;
        }

        // Configuración del Worker y lógica de interacción con el servidor
        configureAndInteract(sender, communicator);
    }

    private static void configureAndInteract(CallbackSenderPrx sender, Communicator communicator) {
        // Configuración inicial del Worker

        // Creación del adapter para el Worker
        ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Worker");
        adapter.add(new CallbackReceiverI(), Util.stringToIdentity("callbackReceiver"));
        adapter.activate();

        // Obtener el proxy del receptor para recibir mensajes del servidor
        CallbackReceiverPrx receiver = CallbackReceiverPrx
                .uncheckedCast(adapter.createProxy(Util.stringToIdentity("callbackReceiver")));

        // Obtener información del sistema para identificación
        String username = System.getProperty("user.name");
        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Interacción con el servidor
        interactWithServer(sender, receiver, username, hostname);
    }

    private static void interactWithServer(CallbackSenderPrx sender, CallbackReceiverPrx receiver, String username,
            String hostname) {
        // Registro del Worker en el servidor
        registerWorker(sender, receiver, username, hostname);
    }

    private static void registerWorker(CallbackSenderPrx sender, CallbackReceiverPrx receiver, String username,
            String hostname) {
        // Mensaje de registro del Worker
        String message = username + "-" + hostname + "-" + "register as worker";

        // Envío del mensaje al servidor
        sender.sendMessage(receiver, message);
    }

}