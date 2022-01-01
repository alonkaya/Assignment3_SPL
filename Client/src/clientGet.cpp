//
// Created by eshm on 31/12/2021.
//
#include "BidiEncoderDecoder.h"
#include "clientGet.h"


clientGet::clientGet(ConnectionHandler *connectionHandler, bool &shouldTerminate, std::mutex &mutex, bool &isLoggedIn):
    connectionHandler(connectionHandler), shouldTerminate(shouldTerminate), mutex(mutex), isLoggedIn(isLoggedIn) {}


int clientGet::run() {
    BidiEncoderDecoder encdec;

    while(!shouldTerminate) {
        std::unique_lock<std::mutex> lock(mutex);
        char buffer[1<<10];
        std::string answer;
        if(!connectionHandler->getBytes(buffer, 1)) {
            std::cout << "Disconnected. Exiting. \n" <<std::endl;
            break;
        }
        answer = encdec.decodeNextByte(buffer[0]);

        if(answer.size() > 0) {
            std::cout << answer <<std::endl; //todo: check if working for logStat and Stat, if not use below lines
//            if(answer[0] == 1 & answer[1] == 0) {//if ACK
//                if(answer[3] == 7 | answer [3] == 8) { //logStat, Stat
//
//                }
//            }
            if(answer == "10 3") {
                shouldTerminate = true;
                return 0;
            }
            answer = "";
        }
    }
    return 0;
}


