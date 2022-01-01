//
// Created by eshm on 31/12/2021.
//
#include "BidiEncoderDecoder.h"
#include "clientSend.h"


clientSend::clientSend(ConnectionHandler *connectionHandler, bool &shouldTerminate, std::mutex &mutex, bool &isLoggedIn):
        connectionHandler(connectionHandler), shouldTerminate(shouldTerminate), mutex(mutex), isLoggedIn(isLoggedIn) {}

int clientSend::run() {
    BidiEncoderDecoder encdec;

    while (!shouldTerminate) {
        char buffer[1<<10];
        std::cin.getline(buffer, 1<<10);
        std::string line(buffer);
        line = encdec.encode(line);
        if(line != "") {
            if(!connectionHandler->sendBytes(line.c_str(), line.size())){
                std::cout << "connection closed, Disconnected." <<std::endl;
                break;
            }
        }
    }
    return 0;
};