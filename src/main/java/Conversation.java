package main.java;

import java.io.Serializable;
import java.util.ArrayList;

public class Conversation implements Serializable{

    private String user;
    private ArrayList<Mensaje> messages;

    public Conversation(String user, ArrayList<Mensaje> messages){
        this.messages = messages;
        this.user = user;
    }

    public String getUser(){
        return this.user;
    }

    public ArrayList<Mensaje> getMessages(){
        return messages;
    }

    public void addMessage(Mensaje message){
    	this.messages.add(message);
    }

}
