import DistributedSorting.CallbackReceiverPrx;
import com.zeroc.Ice.Current;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

//
// Copyright (c) ZeroC, Inc. All rights reserved.
//


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

    }

}