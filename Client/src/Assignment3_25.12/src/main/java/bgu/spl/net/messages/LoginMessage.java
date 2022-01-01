package main.java.bgu.spl.net.messages;

public class LoginMessage implements Message{
    private final short opcode = 2;
    private String username;
    private String password;
    private byte captcha;

    @Override
    public short getOpcode() {return opcode;}

    public String getUsername(){return username;}
    public String getPassword() {return password;}
    public byte getCaptcha() {return captcha;}

    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setCaptcha(byte captcha) {this.captcha = captcha;}
}
