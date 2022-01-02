//
// Created by eshm on 01/01/2022.
//
#include <BidiEncoderDecoder.h>
#include <clientGet.h>
#include <clientSend.h>
#include <mutex>
#include <thread>
int main (int argc, char *argv[]) {
    if(argc < 3) {
        std::cerr <<"Usage: " << argv[0] << "host port" << std::endl << std::endl;
        return -1;
    }
    //Termination flag
    bool shouldTerminate = false;
    //LoggedIn flag
    bool isLoggedIn = true;
    //host address
    std::string host = argv[1];
    //port
    short port = atoi(argv[2]);
    //Client connectionHandler
    ConnectionHandler *connectionHandler = new ConnectionHandler(host,port);
    //connecting the client
    connectionHandler->connect();
    //lock mechanism
    std::mutex mutex;
    clientGet clientGet(connectionHandler, shouldTerminate, mutex, isLoggedIn);
    clientSend clientSend(connectionHandler, shouldTerminate, mutex, isLoggedIn);
    //creating threads for receiving and sending messages
    std::thread getThread(&clientGet::run, &clientGet);
    std::thread sendThread(&clientSend::run, &clientSend);
    getThread.join();
    sendThread.join();
    connectionHandler->close();
    return 0;
};