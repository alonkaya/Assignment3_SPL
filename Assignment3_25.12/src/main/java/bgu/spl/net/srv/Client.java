package main.java.bgu.spl.net.srv;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private final int id;
    private String username;
    private String password;
    //todo: conncurent linked lost or linked list is enough?
    private ConcurrentLinkedQueue<Client> following;
    private ConcurrentLinkedQueue<Client> followers;

    public Client(int id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public ConcurrentLinkedQueue<Client> getFollowers() {return followers;}
    public ConcurrentLinkedQueue<Client> getFollowing() {return following;}
    public String getPassword() {return password;}
    public String getUsername() {return username;}
    public int getId() {return id;}
}
