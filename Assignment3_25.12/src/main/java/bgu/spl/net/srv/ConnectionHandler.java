package main.java.bgu.spl.net.srv;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;

public interface ConnectionHandler<T> extends Closeable{

    void send(T msg) ;

}
