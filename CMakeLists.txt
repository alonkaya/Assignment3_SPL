cmake_minimum_required(VERSION 3.21)
project(Assignment_3)

set(CMAKE_CXX_STANDARD 14)
SET(LDFLAGS "-lboost_system -lboost_thread -pthread")
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${LDFLAGS}")
SET(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ${GCC_COVERAGE_LINK_FLAGS}")

include_directories(Client/include)

add_executable(Assignment_3
        Client/include/BidiEncoderDecoder.h
        Client/include/connectionHandler.h
        Client/src/connectionHandler.cpp
        Client/src/BGSclient.cpp
        Client/src/clientGet.cpp
        Client/src/clientSend.cpp
        Client/src/BidiEncoderDecoder.cpp)
