package main.java.bgu.spl.net.api.bidi;

import javafx.util.Pair;
import main.java.bgu.spl.net.api.bidi.Connections;
import main.java.bgu.spl.net.srv.Client;
import main.java.bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ConnectionsImpl<T> implements Connections<T> {
    //todo:
    // 1. check if the import for the Client clas is the right one.

    private ConcurrentHashMap<Integer, Pair<Client, ConnectionHandler<T>>> clientMap;
    private AtomicInteger id;

    public ConnectionsImpl(){
        clientMap = new ConcurrentHashMap<>();
        id = new AtomicInteger();
    }

    public boolean send(int connectionId, T msg){
        ConnectionHandler clientCH = clientMap.get(connectionId).getValue(); //get the client's key
        if(clientCH != null) { //If client exists send the message and return true
            clientCH.send(msg);
            return true;
        }
        return false; //If client doesn't exist return false
    }

    public void broadcast(T msg){
        for (HashMap.Entry<Integer,  Pair<Client, ConnectionHandler<T>>> entry : clientMap.entrySet()) { //loop over all clients connection handlers
            ConnectionHandler clientCH = entry.getValue().getValue(); //get each client's connection handler;
            clientCH.send(msg);
        }
    }

    public void disconnect(int connectionId){
        clientMap.remove(connectionId);
    }

    public void connect(int connectionId, Pair<Client, ConnectionHandler<T>> pair){
        clientMap.put(connectionId, pair);
    }


    public int getAndIncrementID() {
        int id = this.id.getAndIncrement();
        return id;
    }

    public boolean isUsernameTaken(String username) {
        boolean ans = false;
        for (HashMap.Entry<Integer, Pair<Client, ConnectionHandler<T>>> entry : clientMap.entrySet()) { //loop over all clients connection handlers
            if (entry.getValue().getKey().getUsername() == username) return true;
        }
        return false;
    }



}
