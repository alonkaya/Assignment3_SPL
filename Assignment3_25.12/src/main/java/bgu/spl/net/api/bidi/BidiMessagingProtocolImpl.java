package main.java.bgu.spl.net.api.bidi;

import main.java.bgu.spl.net.messages.AckMessage;
import main.java.bgu.spl.net.messages.ErrorMessage;
import main.java.bgu.spl.net.srv.Client;
import javafx.util.Pair;
import main.java.bgu.spl.net.messages.Message;
import main.java.bgu.spl.net.messages.RegisterMessage;
import main.java.bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{
    private ConnectionsImpl<Message> connections;
    private int id;
    private ConnectionHandler<Message> connectionHandler;
    private Client client;

    private boolean terminate = false;


    public void start(int connectionId, ConnectionsImpl<T> connections, ConnectionHandler<Message> handler) {
        this.connections = (ConnectionsImpl<Message>)connections;
        this.id = connectionId;
        this.connectionHandler = handler;
    }

    public void process(Message message) {
        short opcode = ((Message)message).getOpcode();
        if(opcode == 1){ //Register
            String userName = ((RegisterMessage)message).getUsername();
            String password = ((RegisterMessage)message).getPassword();
            String birthday = ((RegisterMessage)message).getBirthday();

            Message reply;
            if(connections.isUsernameTaken(userName)){
                reply = new ErrorMessage((short)1);
            }
            else {
                this.client = new Client(id, userName, password);
                connections.connect(id, new Pair<Client, ConnectionHandler<T>>(client, connectionHandler)); //Insert id and connection handler to map
                reply = new AckMessage((short)1);
            }
            connections.send(id, reply);
        }
        else if(opcode == 2){//Login

        }


    }

    public boolean shouldTerminate() {
        return terminate;
    }
}
