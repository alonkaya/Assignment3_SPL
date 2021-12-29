package main.java.bgu.spl.net.api.bidi;

import main.java.bgu.spl.net.srv.ConnectionHandler;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);

}
