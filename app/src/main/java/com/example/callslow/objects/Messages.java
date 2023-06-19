package com.example.callslow.objects;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

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
        message_list = new ArrayList<>();

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

    public void setMessagesContact (String oldValue, String value) throws Exception {
        for (Message m : message_list) {
            if (m.getSenderMac().equalsIgnoreCase(oldValue)) {
                m.setSenderMac(value);
            }

            if(m.getReceiverMac().equalsIgnoreCase(oldValue)) {
                m.setReceiverMac(value);
            }
        }
        writeFile();
    }

    public ArrayList<Message> getMessages() {
        return new ArrayList<Message>(message_list);
    }

    public void setMessages(ArrayList<Message> message_list) {
        this.message_list = message_list;
    }

    public void addOldMessage() throws Exception {
        UUID uuid = UUID.randomUUID();
        String content = "Contenu du message";
        String senderMac = "Mettre adresse MAC";
        String receiverMac = "Mettre adresse MAC";
        String sendingDate = "01/01/2020 00:14:38"; // Date d'envoi du message

        Message message = new Message(uuid, content, senderMac, receiverMac, sendingDate);
        message_list.add(message);
        writeFile();
    }

    public boolean deleteMessagesDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, -2); // Soustraire 2 ans

        long twoYearsAgoMillis = calendar.getTimeInMillis();

        Iterator<Message> iterator = message_list.iterator();
        while (iterator.hasNext()) {
            Message m = iterator.next();
            Date messageDate = sdf.parse(m.getSendingDate());
            long messageDateMillis = messageDate.getTime();
            if (messageDateMillis < twoYearsAgoMillis) {
                iterator.remove();
            }
        }

        writeFile();
        return false;
    }
}