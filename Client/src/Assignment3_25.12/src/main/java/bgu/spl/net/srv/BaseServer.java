package main.java.bgu.spl.net.srv;

import main.java.bgu.spl.net.api.MessageEncoderDecoder;
import main.java.bgu.spl.net.api.MessagingProtocol;
import main.java.bgu.spl.net.api.bidi.BidiMessagingProtocol;
import main.java.bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import main.java.bgu.spl.net.api.bidi.ConnectionsImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private ConnectionsImpl<T> connections;

    public BaseServer(
            ConnectionsImpl<T> connections,
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.connections = connections;
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<T>(
                        connections,
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get());

                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
