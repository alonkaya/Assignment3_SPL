package main.java.bgu.spl.net.srv;

import javafx.util.Pair;
import main.java.bgu.spl.net.api.MessageEncoderDecoder;
import main.java.bgu.spl.net.api.MessagingProtocol;
import main.java.bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import main.java.bgu.spl.net.api.bidi.ConnectionsImpl;
import main.java.bgu.spl.net.messages.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocolImpl protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private ConnectionsImpl<T> connections;
    private int id;
    private Client client;



    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocolImpl protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;

    }

    @Override
    public void send(T msg) {
//        try{
//            out.write(msg.encode());
//            out.flush();
//        }catch (IOException ex){ex.printStackTrace();}
    }


    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            id = connections.getAndIncrementID();

//            connections.connect(id, new Pair<>(client, this)); //Insert id and connection handler to map

            //todo: not sure about next line. is this the place to callc start?
            protocol.start(id,  connections, this); //Initiates id and connections in bidi protocol (kinda like a constructor)


            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    public void setConnections(ConnectionsImpl<Message> connection) {this.connections = connection;}
}
