package com.example.callslow.objects;

import android.app.Application;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Messages extends Application {

    private final String MESSAGE_FILE = "messages.json";

    private ArrayList<Message> message_list = new ArrayList<>();
    private JSONObject message_json;

    private Context context = null;

    private static Messages instance;

    public static Messages getInstance() {
        if (instance == null) instance = new Messages();
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;

        String json = readFile();

        try {
            message_json = new JSONObject(json);

            try {
                JSONArray array = message_json.getJSONArray("messages");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject msg = array.getJSONObject(i);
                    try {
                        message_list.add(new Message(msg));
                    } catch (Exception e) {
                        System.err.println("Impossible d'importer le message : " + e.getMessage());
                    }
                }

            } catch (Exception e) {}

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Messages() {
        // Init du fichier Message
    }

    public String readFile() {
        String json = "";

        try {
            FileInputStream fileInputStream = context.openFileInput(MESSAGE_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
            json = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(MESSAGE_FILE, Context.MODE_PRIVATE);
                JSONObject jsonObject = new JSONObject();
                String jsonString = jsonObject.toString();
                fileOutputStream.write(jsonString.getBytes());
                fileOutputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    /*
        Writes the file in the memory
     */
    public void writeFile() throws Exception {
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for (Message m : message_list) {
            array.put(m.toJson());
        }

        obj.put("messages",array);

        FileOutputStream fileOutputStream = context.openFileOutput(MESSAGE_FILE, Context.MODE_PRIVATE);
        String jsonString = obj.toString();
        fileOutputStream.write(jsonString.getBytes());
        fileOutputStream.close();
    }

    public void addMessage(Message msg) throws Exception {
        message_list.add(msg);
        writeFile();
    }

    public ArrayList<Message> getMessages() {
        return new ArrayList<Message>(message_list);
    }

    public void setMessages(ArrayList<Message> message_list) {
        this.message_list = message_list;
    }
}