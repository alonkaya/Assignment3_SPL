package main.java.bgu.spl.net.api.bidi;

import main.java.bgu.spl.net.messages.*;
import main.java.bgu.spl.net.srv.Client;
import javafx.util.Pair;
import main.java.bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Integer.parseInt;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{
    private int id;
    private ConnectionsImpl<Message> connections;
    private Client client;
    private String[] wordsToFilter = {"Luke", "I", "am", "your", "father"};
    private boolean terminate = false;


    public void start(int connectionId, Connections<Message> connections) {
        this.connections = (ConnectionsImpl<Message>)connections;
        this.id = connectionId;
    }

    public void process(Message message) {
        short opcode = message.getOpcode();
        this.client = connections.getClient(id);//should be null if client hasn't registered yet
        Message reply = null;

        ///////////////////////////////////////Register///////////////////////////////////////
        if(opcode == 1){
            String userName = ((RegisterMessage)message).getUsername();
            String password = ((RegisterMessage)message).getPassword();
            String birthday = ((RegisterMessage)message).getBirthday();
            int yearBorn = parseInt(birthday.substring(6));
            short age = (short)(2021 - yearBorn);

            if(connections.getClient(userName) != null){//If username already exists in system
                reply = new ErrorMessage((short)1);
            }
            else {
                this.client = new Client(id, userName, password, age);
                connections.addClient(client);
                connections.setClient(id, client);///Setting connection handler's client from null to this client, and matches ids
                reply = new AckMessage((short)1);
            }
        }
        ///////////////////////////////////////Login///////////////////////////////////////
        else if(opcode == 2){
            String userName = ((LoginMessage)message).getUsername();
            String password = ((LoginMessage)message).getPassword();
            byte captcha = ((LoginMessage)message).getCaptcha();
            Client loggingClient = connections.getClient(userName);


            //If user doesn't exist, password doesn't match, captcha is 0, or user logged in already: send Error
            if(loggingClient == null || !(loggingClient.getPassword()).equals(password) || loggingClient.isLoggedIn() || captcha == 0){
                reply = new ErrorMessage((short) 2);
            }
            else {//login successful
                this.client = loggingClient;
                client.login();
                connections.setClient(id, client);//sets connection handler's client to this client, and sets his id to be the client's old one
                this.id = client.getId();//sets this id to be the client's old one

                //Now that user has logged in: send him all notification that he missed while being logged out-
                ConcurrentLinkedQueue<NotificationMessage> waitingNotifications =  connections.getWaitingNotifications(id);
                while (!waitingNotifications.isEmpty()){
                    connections.send(id, waitingNotifications.poll());
                }
                terminate = false;
                reply = new AckMessage((short) 2);
            }
        }
        ///////////////////////////////////////Logout///////////////////////////////////////
        else if(opcode == 3) {
            if(client.isLoggedIn()){
                client.logOut();
                terminate = true;
                connections.disconnect(id);
                reply = new AckMessage((short) 3);
            }else
                reply = new ErrorMessage((short) 3);

        }
        ///////////////////////////////////////Follow/Unfollow///////////////////////////////////////
        else if(opcode == 4) {
            int follow  = ((FollowMessage)message).getFollow();
            String username = ((FollowMessage)message).getUsername();
            Client toFollow = connections.getClient(username);

            if(follow == 0){ //Follow
                //succeeds if client exists, logged in, not already following this user, and not blocked by this requested user.
                if(toFollow != null && client.isLoggedIn() && !client.isFollowing(username) && toFollow.isUserBlocked(client)){
                    client.addFollowing(toFollow);
                    toFollow.addFollower(client);
                    //Create an ack reply message containing username for which the client is trying to follow
                    reply = new AckMessage((short) 4);
                    ((AckMessage)reply).setUsername(username);
                } else //follow command didn't succeed
                    reply = new ErrorMessage((short) 4);

            }else if(follow == 1) {//Unfollow
                if(toFollow != null && client.isLoggedIn() && client.isFollowing(username)){//client logged in and indeed following this user
                    client.removeFollowing(toFollow);
                    toFollow.removeFollower(client);
                    //Create an ack reply message containing username for which the client is trying to follow
                    reply = new AckMessage((short) 4);
                    ((AckMessage)reply).setUsername(username);
                }else //unfollow command didn't succeed
                    reply = new ErrorMessage((short) 4);
            }
        }
        ///////////////////////////////////////Post///////////////////////////////////////
        else if(opcode == 5) {
            if(client.isLoggedIn()){
                //First create the notification message needed to be delivered to the relevant users from the post that this client posted
                String content = ((PostMessage)message).getContent();
                NotificationMessage notificationMessage = new NotificationMessage(1, client, content);

                //Now send this notification to all users following this user
                for(Client followingClient : client.getFollowers()){
                    sendNotificationMessage(followingClient, notificationMessage);
                }
                //Send it also to all users that his name appears within '@' in the content of the post-
                LinkedList<String> mentions = ((PostMessage)message).getMentions();
                for(String mention : mentions){
                    Client clientMentioned = connections.getClient(mention);
                    if (clientMentioned != null && !client.isUserBlocked(clientMentioned)){//client exists and is not blocked
                        sendNotificationMessage(clientMentioned, notificationMessage);
                    }
                }
                connections.addPost(id, (PostMessage)message, client);

                reply = new AckMessage((short) 5);
            }else
                reply = new ErrorMessage((short) 5);
        }
        ///////////////////////////////////////PM///////////////////////////////////////
        else if(opcode == 6){
            if(client.isLoggedIn()){
                String username = ((PMMessage)message).getUsername();
                String content = ((PMMessage)message).getContent();
                String time = ((PMMessage)message).getTime();

                Client recipient = connections.getClient(username);
                if(recipient != null && client.isFollowing(username)){//If recipient exists and our client is following him
                    String filteredContent = filter(content);
                    NotificationMessage notificationMessage = new NotificationMessage(0, client, filteredContent);
                    sendNotificationMessage(recipient, notificationMessage);
                    connections.addPM(id, (PMMessage)message);

                    reply = new AckMessage((short) 6);
                }
                else reply = new ErrorMessage((short) 6);
            } else reply = new ErrorMessage((short) 6);
        }
        ///////////////////////////////////////Logstat///////////////////////////////////////
        else if(opcode == 7){
            if(client.isLoggedIn()){
                reply = new AckMessage((short) 7);
                //todo: i need to go through all logged in clients(whether through the connectionHandlerMap or the clients queue,
                // but even though they are concurrent, some clients logged in state might state from the Logstat command to the getter
                for(Client clientToStat : connections.getClients()){
                    //parsing through all logged in clients
                    if (clientToStat.isLoggedIn() && clientToStat != client){
                        reply = new AckMessage((short) 7);
                        ((AckMessage)reply).addStat(clientToStat.getAge(), clientToStat.getNumOfPosts(),
                                clientToStat.getNumFollowers(), clientToStat.getNumFollowing());
                    }
                }
            }
            else reply = new ErrorMessage((short) 7);
        }
        ///////////////////////////////////////Stat///////////////////////////////////////
        else if(opcode == 8){
            if(client.isLoggedIn()){
                LinkedList<String> usersToStat = ((StatMessage)message).getUsersList();
                for(String username : usersToStat){
                    Client clientToStat = connections.getClient(username);
                    if (clientToStat != null){
                        reply = new AckMessage((short) 8);
                        ((AckMessage)reply).addStat(clientToStat.getAge(), clientToStat.getNumOfPosts(),
                                clientToStat.getNumFollowers(), clientToStat.getNumFollowing());
                    }
                }
            }
            else reply = new ErrorMessage((short) 8);

        }
        ///////////////////////////////////////Block///////////////////////////////////////
        else if(opcode == 12){
            String username = ((BlockMessage)message).getUsername();
            Client clientToBlock = connections.getClient(username);
            if (clientToBlock != null && !client.isUserBlocked(client)){//client exists and not already blocked
                this.client.addBlockedUser(clientToBlock);
                client.removeFollower(clientToBlock);
                clientToBlock.removeFollowing(client);

                reply = new AckMessage((short) 12);
            }
            else reply = new ErrorMessage((short) 12);
        }








        if(reply != null){
            connections.send(id, reply);
        }
        System.out.println("something's wrong");//todo: delete this line
    }



    public boolean shouldTerminate() {
        return terminate;
    }

    public void sendNotificationMessage(Client client, NotificationMessage message){
        if (client.isLoggedIn())
            connections.send(client.getId(), message);
        else
            connections.addWaitingNotification(client.getId(), message);
    }
    public String filter(String content){
        String[] contentWords = content.split(" "); //string to array of words
        String filteredContent = "";

        for(String word : contentWords){//go through each word in content
            boolean filter = false;
            for(String filteredWord : wordsToFilter) //check if word is in filtered list
                if (word.equals(filteredWord)) {
                    filter = true;
                    break;
                }
            if(filter)
                filteredContent = filteredContent + "<filter> ";
            else
                filteredContent = filteredContent + word + " ";
        }
        return filteredContent;
    }
}

