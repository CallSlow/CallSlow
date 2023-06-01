package com.example.callslow.objects;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Message {
    UUID uuid;
    String content, senderMac, receiverMac, sendingDate;

    public Message(UUID uuid, String content, String senderMac, String receiverMac, String sendingDate) {
        this.uuid = uuid;
        this.content = content;
        this.senderMac = senderMac;
        this.receiverMac = receiverMac;
        this.sendingDate = sendingDate;
    }

    @SuppressLint("SimpleDateFormat")
    public Message(JSONObject obj) throws JSONException, ParseException {
        uuid = UUID.fromString(obj.getString("uuid"));
        content = obj.getString("content");
        senderMac = obj.getString("senderMac");
        receiverMac = obj.getString("receiverMac");
        sendingDate = obj.getString("sendingDate");
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderMac() {
        return senderMac;
    }

    public void setSenderMac(String senderMac) {
        this.senderMac = senderMac;
    }

    public String getReceiverMac() {
        return receiverMac;
    }

    public void setReceiverMac(String receiverMac) {
        this.receiverMac = receiverMac;
    }

    public String getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(String sendingDate) {
        this.sendingDate = sendingDate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uuid", uuid);
        obj.put("content", content);
        obj.put("senderMac", senderMac);
        obj.put("receiverMac", receiverMac);
        obj.put("sendingDate", sendingDate);

        return obj;
    }
}