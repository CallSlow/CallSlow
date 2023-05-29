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
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Contacts extends Application {

    private final String CONTACT_FILE = "contacts.json";

    private ArrayList<Contact> contact_list = new ArrayList<>();
    private JSONObject contact_json;

    private Context context = null;

    private static Contacts instance;

    public static Contacts getInstance() {
        if (instance == null) instance = new Contacts();
        return instance;
    }

    public void init(Context ctx) {
        contact_list = new ArrayList<>();

        context = ctx;

        String json = readFile();

        try {
            contact_json = new JSONObject(json);

            try {
                JSONArray array = contact_json.getJSONArray("contacts");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject ct = array.getJSONObject(i);
                    try {
                        contact_list.add(new Contact(ct));
                    } catch (Exception e) {
                        System.err.println("Impossible d'importer le contact : " + e.getMessage());
                    }
                }

            } catch (Exception e) {}

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Contacts() {
        // Init du fichier contact
    }

    private String readFile() {
        String json = "";

        try {
            FileInputStream fileInputStream = context.openFileInput(CONTACT_FILE);
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
                FileOutputStream fileOutputStream = context.openFileOutput(CONTACT_FILE, Context.MODE_PRIVATE);
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
        // préparation de l'array json
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for (Contact c : contact_list) {
            array.put(c.toJson());
        }

        obj.put("contacts",array);

        FileOutputStream fileOutputStream = context.openFileOutput(CONTACT_FILE, Context.MODE_PRIVATE);
        String jsonString = obj.toString();
        fileOutputStream.write(jsonString.getBytes());
        fileOutputStream.close();


    }

    /*
     *  returns contacts in arraylist using a copy of the element.
     *  to add a contact, you need to use the add method.
     */
    public ArrayList<Contact> getContacts() {
        return new ArrayList<Contact>(contact_list);
    }

    /*
     * Checks if the contact already exists and if not add the contact to the list
     * then writes the file
     */
    public boolean addContact(Contact ct) throws Exception {
        // vérification de doublons
        for(Contact c : contact_list) {
            if (ct.getMac().equals(c.getMac())) {
                return false;
            }
        }
        contact_list.add(ct);
        writeFile();
        return true;
    }

    public boolean deleteContact(Contact ct) throws Exception {
        if (contact_list.contains(ct)) {
            contact_list.remove(ct);
            writeFile();
            return true;
        }
        return false;
    }

    public boolean deleteContact(String mac) throws Exception {
        for (Contact c : contact_list) {
            if (c.getMac().equals(mac)) {
                return deleteContact(c);
            }
        }
        return false;
    }

    public boolean replaceContact(Contact oldContact, Contact newContact) throws Exception {
        int index = contact_list.indexOf(oldContact);
        if (index == -1) {
            return addContact(newContact);
        }

        contact_list.set(index, newContact);
        writeFile();
        return true;
    }

}
