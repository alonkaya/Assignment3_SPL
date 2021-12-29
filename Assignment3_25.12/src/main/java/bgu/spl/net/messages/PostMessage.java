package main.java.bgu.spl.net.messages;

public class PostMessage implements Message{
    private final short opcode = 5;
    private String content;

    @Override
    public short getOpcode() {return opcode;}

    public String getContent() {return content;}

    public void setContent(String content) {this.content = content;}
}
