package main.java;

import java.io.Serializable;
import java.util.ArrayList;

public class Conversation implements Serializable{

    private String user;
    private ArrayList<String> messages;

    public Conversation(String user, ArrayList<String> messages){
        this.messages = messages;
        this.user = user;
    }

    public String getUser(){
        return this.user;
    }

    public ArrayList<String> getMessages(){
        return messages;
    }

    public void addMessage(String message){
    	this.messages.add(message);
    }


}
