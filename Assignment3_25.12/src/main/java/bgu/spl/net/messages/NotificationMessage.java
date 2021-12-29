package main.java.bgu.spl.net.messages;

public class NotificationMessage implements Message{
    private final short opcode = 9;
    private int type = -1;
    private String postingUser;
    private String content;

    @Override
    public short getOpcode() {return opcode;}

    public int getType() {return type;}
    public String getContent() {return content;}
    public String getPostingUser() {return postingUser;}

    public void setContent(String content) {this.content = content;}
    public void setPostingUser(String postingUser) {this.postingUser = postingUser;}
    public void setType(int type) {this.type = type;}
}
