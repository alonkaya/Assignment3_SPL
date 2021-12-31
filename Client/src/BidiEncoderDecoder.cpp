//
// Created by eshm on 30/12/2021.
//
#include <string>
#include <boost/lexical_cast.hpp>
#include "BidiEncoderDecoder.h"

//-------constructor---------
BidiEncoderDecoder::BidiEncoderDecoder():length(0), position(0), opCode(0), messageType(0), decodedMessage(""),bytesVector(),stringVector() {}

// ------ Destructor------------
BidiEncoderDecoder::~BidiEncoderDecoder() {
    reset();
}



void BidiEncoderDecoder::reset() {
    length = 0;
    position = 0;
    opCode = 0;
    messageType = 0;
    bytesVector.clear();
    stringVector.clear();
}

//----------------Encode------------------

std::string BidiEncoderDecoder::encode(std::string message){
    std::string encodedMessage = "";

    //gets the first word of the message which represents the type of it
    int first = message.find_first_of(" ");
    std::string messageType = message.substr(0,first);
    //gets the opcode of the message to encode
    opCode = getType(messageType);
    message = message.substr(first + 1);
    std::stringstream s1(message);
    std::string segment;


    switch(opCode) {
        case 1: //register
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            while(std::getline(s1,segment,' ')) {
                stringVector.push_back(segment);
            }
            encodedMessage += stringVector[0]; //username
            encodedMessage += '\0';
            encodedMessage += stringVector[1]; //password
            encodedMessage += '\0';
            encodedMessage += stringVector[2]; //birthday
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 2: //login
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            while(std::getline(s1,segment,' ')) {
                stringVector.push_back(segment);
            }
            encodedMessage += stringVector[0];
            encodedMessage += '\0';
            encodedMessage += stringVector[1];
            encodedMessage += '\0';
            encodedMessage += stringVector[2];
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 3: //logout
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 4: //follow
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            while(std::getline(s1,segment,' ')) {
                stringVector.push_back(segment);
            }
            encodedMessage += std::stoi(stringVector[0]);
            encodedMessage += stringVector[1];
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 5: //post
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += message;
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 6: //PM
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            first = message.find_first_of(' ');
            encodedMessage += message.substr(0, first);
            encodedMessage += '\0';
            message = message.substr(first + 1);
            encodedMessage += message;
            encodedMessage += '\0';
            //todo: add time stamp to the message
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 7: //logStat
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += ';';
            reset();
            return encodedMessage;
        case 8: //stat
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += message;
            encodedMessage += '\0';
            encodedMessage += ';';
//            while(message.size() != 0){
//                //searching for the next username in the list
//                first = message.find_first_of('|');
//                //todo: check the encoderDecoder in java how it works to match with this encoder
//                if(first != std::string::npos) {
//                    encodedMessage += message.substr(0, first);
//                    encodedMessage += '\0';
//                    message = message.substr(first + 1);
//                }
//                // if it's the last one on the list
//                else {
//                    encodedMessage += message;
//                    encodedMessage += '\0';
//                    encodedMessage += ';';
//                    message = "";
//                }
//
//            }
            reset();
            return encodedMessage;
        case 9: //notification
        case 10: //ack
        case 11: //error
        case 12: //block
            ShortToBytes(opCode, &bytesVector);
            encodedMessage += bytesVector[0];
            encodedMessage += bytesVector[1];
            encodedMessage += message;
            encodedMessage += '\0';
            encodedMessage += ';';
            reset();
            return encodedMessage;
    }
    return nullptr;
}


//-----------------Decode----------------------

std::string BidiEncoderDecoder::decodeNextByte(char nextByte) {
    if(opCode == 0){
        if(length == 1){
            bytesVector.push_back(nextByte);
            length++;
            opCode = bytesToShort(0);
            return "";
        }
        bytesVector.push_back(nextByte);
        length++;
        return "";

    }
    if(length > 3) {
        if (opCode == 11) {
            messageType = bytesToShort(2);
            std::string ans = "ERROR " + std::to_string(messageType);
            reset();
            return ans;
        }
        if (opCode == 10) {
            messageType = bytesToShort(2);
            if(nextByte != '\0') {
                bytesVector.push_back(nextByte);
            }
            else {
                std::string ans = "ACK " + messageType + 
            }
        }

    }
    bytesVector.push_back(nextByte);
    length++;
    return "";
}





















short BidiEncoderDecoder::getType(std::string MessageType) {
    if(MessageType == "REGISTER")
        return 1;
    if(MessageType == "LOGIN")
        return 2;
    if(MessageType == "LOGOUT")
        return 3;
    if(MessageType == "FOLLOW")
        return 4;
    if(MessageType == "POST")
        return 5;
    if(MessageType == "PM")
        return 6;
    if(MessageType == "LOGSTAT")
        return 7;
    if(MessageType == "STAT")
        return 8;
    if(MessageType == "NOTIFICATION")
        return 9;
    if(MessageType == "ACK")
        return 10;
    if(MessageType == "ERROR")
        return 11;
    if(MessageType == "BLOCK")
        return 12;
    return 0;
}

void BidiEncoderDecoder::ShortToBytes(short num, std::vector<char> *bytesArr) {
    bytesArr-> push_back((num>>8) & 0xFF);
    bytesArr->push_back(num & 0xFF);
}

short BidiEncoderDecoder::bytesToShort(int startingPosition) {
    short result = (short)((bytesVector[startingPosition] & 0xff) << 8);
    result += (short)(bytesVector[startingPosition + 1] & 0xff);
    return result;
}