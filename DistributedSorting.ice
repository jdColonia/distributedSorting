module DistributedSorting
{
    interface CallbackReceiver
    {
        void receiveMessage(string msg);
        void startWorker(int from, int to,string filename,string basepath);
        string getSortedList();
    }
    interface CallbackSender
    {
        void sendMessage(CallbackReceiver* proxy,string msg);
        void shutdown();
    }
}