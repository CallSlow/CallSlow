package com.example.callslow.objects;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Comparaison {
    private Context context = null;


    public Comparaison(Context ctx) {
        this.context = ctx;
    }

    public String readFiletoJSONFile(String filePath) {
        String json = "";
        try {
            FileInputStream fileInputStream = this.context.openFileInput(filePath);
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
                FileOutputStream fileOutputStream = context.openFileOutput(filePath, Context.MODE_PRIVATE);
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

    public int[] getStatGlobales(JSONArray list, String property) {
        int amReceiver = 0;
        int amSender = 0;

        try {
            for (int i = 0; i < list.length(); i++) {
                JSONObject jsonObject = list.getJSONObject(i);
                String receiverMAC = jsonObject.optString("receiverMac", "");
                String senderMAC = jsonObject.optString("senderMac", "");

                if (receiverMAC.equalsIgnoreCase(property)) {
                    amReceiver++;
                }

                if (senderMAC.equalsIgnoreCase(property)) {
                    amSender++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new int[]{amSender, amReceiver};

    }

    public JSONArray getNewValues(JSONArray list1, JSONArray list2, String[] properties) {
        JSONArray newValue = new JSONArray();


        for (int i = 0; i < list2.length(); i++) {
            JSONObject jsonObject2 = list2.optJSONObject(i);

            if (jsonObject2 != null) {
                boolean found = false;

                if (list1.length() > 0) {
                    for (int j = 0; j < list1.length(); j++) {
                        JSONObject jsonObject1 = list1.optJSONObject(j);
                        boolean equal = true;

                        if (jsonObject1 != null && jsonObject2 != null) {
                            for (String property : properties) {
                                if (!jsonObject1.optString(property).equalsIgnoreCase(jsonObject2.optString(property))) {
                                    equal = false;
                                    break;
                                }
                            }

                            if (equal) {
                                found = true;
                                break;
                            }
                        }
                    }
                }

                if (!found) {
                    newValue.put(jsonObject2);
                }
            }
        }

        return newValue;
    }

    public JSONObject writeJSONArrayToFile(String param, JSONArray oldArray, JSONArray newArray, String pathFile) throws IOException, JSONException {
        JSONObject obj = new JSONObject();

        for (int i = 0; i < newArray.length(); i++) {
            oldArray.put(newArray.get(i));
        }

        obj.put(param,oldArray);

        FileOutputStream fileOutputStream = context.openFileOutput(pathFile, Context.MODE_PRIVATE);
        String jsonString = obj.toString();
        fileOutputStream.write(jsonString.getBytes());
        fileOutputStream.close();
        return obj;
    }

    public int[] countByReceiver(JSONArray jsonArray, String property) {
        int amReceiver = 0;
        int amNotReceiver = 0;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String receiverMAC = jsonObject.optString("receiverMac", "");
                if (receiverMAC.equalsIgnoreCase(property)) {
                    amReceiver++;
                } else {
                    amNotReceiver++;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new int[]{amReceiver, amNotReceiver};
    }

}