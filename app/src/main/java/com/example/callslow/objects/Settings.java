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

public class Settings extends Application {

    private final String SETTINGS_FILE = "settings.json";

    private String macAdress;
    private String pseudo;
    private JSONObject settings_json;

    private Context context = null;

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) instance = new Settings();
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;

        String json = readFile();

        try {
            settings_json = new JSONObject(json);

            try {
                macAdress = String.valueOf(settings_json.getJSONObject("macAdress"));
                pseudo = String.valueOf(settings_json.getJSONObject("pseudo"));
            } catch (Exception e) {}

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Settings() {
        // Init du fichier contact
    }

    private String readFile() {
        String json = "";

        try {
            FileInputStream fileInputStream = context.openFileInput(SETTINGS_FILE);
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
                FileOutputStream fileOutputStream = context.openFileOutput(SETTINGS_FILE, Context.MODE_PRIVATE);
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
        JSONObject array = new JSONObject();
        array.put("macAdress",macAdress);
        array.put("pseudo",pseudo);

        obj.put("settings",array);

        FileOutputStream fileOutputStream = context.openFileOutput(SETTINGS_FILE, Context.MODE_PRIVATE);
        String jsonString = obj.toString();
        fileOutputStream.write(jsonString.getBytes());
        fileOutputStream.close();


    }

    /*
     *  returns contacts in arraylist using a copy of the element.
     *  to add a contact, you need to use the add method.
     */
    public ArrayList<String> getSettings() {
        ArrayList<String> setting = new ArrayList<String>();
        setting.add(macAdress);
        setting.add(pseudo);
        return setting;
    }

    /*
     * Checks if the contact already exists and if not add the contact to the list
     * then writes the file
     */

    public boolean replaceMacAdress(String newMacAdress) throws Exception {
        macAdress = newMacAdress;
        return true;
    }

    public boolean replacePseudo(String newPseudo) throws Exception {
        pseudo = newPseudo;
        return true;
    }
}
