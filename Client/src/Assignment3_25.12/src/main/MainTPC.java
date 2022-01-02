package main;

import main.java.bgu.spl.net.api.MessageEncoderDecoder;
import main.java.bgu.spl.net.api.bidi.*;
import main.java.bgu.spl.net.messages.Message;
import main.java.bgu.spl.net.srv.ThreadPerClientImpl;

import java.sql.Connection;
import java.util.function.Supplier;

public class MainTPC {

    public static void main(String[] args){
        ConnectionsImpl<Message> connections = new ConnectionsImpl<>();
        int port = 7777;

        ThreadPerClientImpl<Message> TPC = new ThreadPerClientImpl<Message>(connections, port,
                ()-> {return new BidiMessagingProtocolImpl();}, ()->{ return new MessageEncoderDecoderImpl();});

        TPC.serve();


    }


}
