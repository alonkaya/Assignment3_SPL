package main.java.bgu.spl.net.srv;

import main.java.bgu.spl.net.api.MessageEncoderDecoder;
import main.java.bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import main.java.bgu.spl.net.api.bidi.ConnectionsImpl;
import main.java.bgu.spl.net.messages.Message;

import java.util.function.Supplier;

public class ThreadPerClientImpl<T> extends BaseServer<T> {
    public ThreadPerClientImpl(int port, Supplier<BidiMessagingProtocolImpl> protocolFactory, Supplier<MessageEncoderDecoder<T>> encdecFactory) {
        super(port, protocolFactory, encdecFactory);
    }

    protected void execute(BlockingConnectionHandler<T> handler){
        new Thread(handler).start();
    }
}
