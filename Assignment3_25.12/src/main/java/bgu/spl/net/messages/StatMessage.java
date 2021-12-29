package main.java.bgu.spl.net.messages;

import java.util.LinkedList;
import java.util.List;

public class StatMessage implements Message{
    private final short opcode = 8;
    private List<String> usersList = new LinkedList<String>();

    @Override
    public short getOpcode() {return opcode;}

    public void addUsername(String username){usersList.add(username);}



}
