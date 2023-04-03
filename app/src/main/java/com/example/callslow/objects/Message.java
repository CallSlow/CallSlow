package com.example.callslow.objects;

import java.util.Date;

public class Message {
    String content;
    Contact sender, receiver;
    Date sendingDate;

    public Message(String content, Contact sender, Contact receiver, Date sendingDate) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.sendingDate = sendingDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Contact getSender() {
        return sender;
    }

    public void setSender(Contact sender) {
        this.sender = sender;
    }

    public Contact getReceiver() {
        return receiver;
    }

    public void setReceiver(Contact receiver) {
        this.receiver = receiver;
    }

    public Date getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(Date sendingDate) {
        this.sendingDate = sendingDate;
    }
}
