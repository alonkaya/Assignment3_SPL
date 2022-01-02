package main;

import main.java.bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import main.java.bgu.spl.net.api.bidi.ConnectionsImpl;
import main.java.bgu.spl.net.api.bidi.MessageEncoderDecoderImpl;
import main.java.bgu.spl.net.messages.Message;
import main.java.bgu.spl.net.srv.Reactor;

public class MainReactor {
    public static void main (String[] args){
        ConnectionsImpl<Message> connections = new ConnectionsImpl<>();

        Reactor<Message> reactor = new Reactor<>(connections, Integer.decode(args[1]).intValue(),Integer.decode(args[0]).intValue(),
                ()-> {return new BidiMessagingProtocolImpl();}, ()->{ return new MessageEncoderDecoderImpl();});

        reactor.serve();
    }
}
