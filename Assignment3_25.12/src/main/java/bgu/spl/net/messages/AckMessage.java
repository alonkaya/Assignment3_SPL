package main.java.bgu.spl.net.messages;

public class AckMessage implements Message{
    private final short opcode = 10;
    private short messageOpcode = -1;
    ////For Follow message:////
    private String username;
    ////For stat/logStat message:////
    private short age = -1;
    private short numPosts = -1;
    private short numFollowers = -1;
    private short numFollowing = -1;

    public AckMessage(short messageOpcode){
        this.messageOpcode = messageOpcode;
    }

    @Override
    public short getOpcode() {return opcode;}

    public short getMessageOpcode() {return messageOpcode;}
    public void setMessageOpcode(short messageOpcode) {this.messageOpcode = messageOpcode;}

    ////For Follow message:////
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    ////For stat/logStat message:////

    public short getAge() {return age;}
    public void setAge(short age) {this.age = age;}

    public short getNumPosts() {return numPosts;}
    public void setNumPosts(short numPosts) {this.numPosts = numPosts;}

    public short getNumFollowers() {return numFollowers;}
    public void setNumFollowers(short numFollowers) {this.numFollowers = numFollowers;}

    public short getNumFollowing() {return numFollowing;}
    public void setNumFollowing(short numFollowing) {this.numFollowing = numFollowing;}
}
