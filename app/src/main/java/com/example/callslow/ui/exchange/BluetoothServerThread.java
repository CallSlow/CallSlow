package com.example.callslow.ui.exchange;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.callslow.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import com.example.callslow.objects.Comparaison;
import com.example.callslow.objects.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothServerThread extends Thread {
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID;

    private View parentView;

    private TextView status;

    private FragmentManager fragmentManager;

    private Context context;

    private BluetoothSocket socket;

    private BluetoothServerSocket serverSocket;

    private final int BUFFER_SIZE = 8192;

    public BluetoothServerThread(BluetoothAdapter adapter, UUID uuid, View view, FragmentManager manager, Context context1) {
        bluetoothAdapter = adapter;
        MY_UUID = uuid;
        parentView = view;
        status = (TextView) view.findViewById(R.id.tagServeur);
        fragmentManager = manager;
        context = context1;
    }

    private void writeToView(String message, int color) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                status.setTextColor(color);
                status.setText(message);
            }
        });
    }

    @Override
    public void run() {
        try {

            System.out.println(" -- Démarrage serveur reception message --");

            this.serverSocket = null;



            try {
                this.serverSocket= bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("CallSlow", MY_UUID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            while (true) {
                writeToView("Serveur : Prêt", Color.GREEN);
                this.socket = this.serverSocket.accept();
                writeToView("Serveur : en cours...", Color.YELLOW);

                System.out.println("-- Une connexion Bluetooth entrante a été établie avec succès --");

                BluetoothDevice remoteDevice = socket.getRemoteDevice();
                String deviceAddress = remoteDevice.getAddress();
                String deviceName = remoteDevice.getName();

                InputStream inputStream = this.socket.getInputStream();
                OutputStream outputStream = this.socket.getOutputStream();

                InputStream inputStream_BAL = this.socket.getInputStream();
                OutputStream outputStream_BAL = this.socket.getOutputStream();


                // Réception fichier message
                FileOutputStream fileStream = this.context.openFileOutput("messages_exchange.json", Context.MODE_PRIVATE);
                OutputStream fileOutputStream = new BufferedOutputStream(fileStream);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;

                while (inputStream.available() == 0) {
                    // attente

                }
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    Log.d("ECHANGE", new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
                    fileOutputStream.write(buffer, 0, bytesRead);
                    if (buffer[bytesRead-1] == -128) {
                        break;
                    }
                }


//                bytesRead = inputStream.read(buffer);
//                fileOutputStream.write(buffer, 0, bytesRead);

                fileOutputStream.flush();
                fileOutputStream.close();
                //inputStream.close();
               // String retour1 = (message != null) ? "1" : "0";

                // Envoie fichier message
                FileInputStream fileStream1 = this.context.openFileInput("messages.json");
                InputStream fileInputStream = new BufferedInputStream(fileStream1);

                byte[] buffer1 = new byte[BUFFER_SIZE];
                int bytesRead1 = 0;

                while ((bytesRead1 = fileInputStream.read(buffer1)) != -1) {
                    outputStream_BAL.write(buffer1, 0, bytesRead1);
                }
                outputStream_BAL.write(-128);
                Log.d("Fichier - Envoi - Client","C'est bon");
                outputStream_BAL.flush();
                fileInputStream.close();

              //  String retour2 = (messageRecu != null) ? "1" : "0";

                // Réception fichier Boite

                FileOutputStream fileStream_bal1 = context.openFileOutput("map_exchange.json", Context.MODE_PRIVATE);
                OutputStream fileOutputStream_bal1 = new BufferedOutputStream(fileStream_bal1);

                byte[] buffer_bal1 = new byte[BUFFER_SIZE];
                int bytesRead_bal1 = 0;

                bytesRead_bal1 = inputStream_BAL.read(buffer_bal1);
                fileOutputStream_bal1.write(buffer_bal1, 0, bytesRead_bal1);
                fileOutputStream_bal1.flush();
                fileOutputStream_bal1.close();

                //  String retour3 = (message2 != null) ? "1" : "0";


                // Envoie fichier Boite

                FileInputStream fileStream_bal =  context.openFileInput("map.json");
                InputStream fileInputStream_bal = new BufferedInputStream(fileStream_bal);

                byte[] buffer_bal = new byte[BUFFER_SIZE];
                int bytesRead_bal = 0;

                bytesRead_bal = fileInputStream_bal.read(buffer_bal);
                outputStream_BAL.write(buffer_bal, 0, bytesRead_bal);
                fileInputStream_bal.close();

              //   String retour4 = (messageRecu2 != null) ? "1" : "0";

                // Ouverture de la page de sychronisation
                writeToView("Serveur : Redirection", Color.BLUE);

                System.out.println("-- Serveur : Redirection --");


                // Comparaison fichier message

                Comparaison compare = new Comparaison(context);
                String json_list_1 = compare.readFiletoJSONFile("messages.json");
                String json_list_2 = compare.readFiletoJSONFile("messages_exchange.json");

                try {
                    JSONObject message_json1 = new JSONObject(json_list_1);
                    JSONObject message_json2 = new JSONObject(json_list_2);


                    JSONArray array_json1 = message_json1.getJSONArray("messages");
                    JSONArray array_json2 = message_json2.getJSONArray("messages");

                     Log.d("Affichage du premier tableau", array_json1.toString());
                     Log.d("Affichage du deuxiÃ¨me tableau", array_json2.toString());

                    System.out.println(Settings.getInstance().getSettings().get(0));

                    JSONArray finalArray = compare.getNewValues(array_json1, array_json2, new String[]{"uuid"},myMacAdress);
                    Log.d("Affichage du tableau final", finalArray.toString());
                    Log.d("Taille tableau final",String.valueOf(finalArray.length()));
                    String param = "messages";

                    compare.writeJSONArrayToFile(param, array_json1,finalArray,"messages.json");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Comparaison fichier carte

                String json_list_3 = compare.readFiletoJSONFile("map.json");
                String json_list_4 = compare.readFiletoJSONFile("map_exchange.json");

                try {
                    JSONObject message_json3 = new JSONObject(json_list_3);
                    JSONObject message_json4 = new JSONObject(json_list_4);

                    Log.d("Comparaison Objet JSON",String.valueOf(message_json3));
                    Log.d("Comparaison Objet JSON 2",String.valueOf(message_json4));


                    JSONArray array_json3 = message_json3.getJSONArray("point");
                    JSONArray array_json4 = message_json4.getJSONArray("point");

                    Log.d("Affichage du premier tableau", array_json3.toString());
                    Log.d("Affichage du deuxiÃ¨me tableau", array_json4.toString());

                    JSONArray finalArray = compare.getNewValues(array_json3, array_json4, new String[]{"uuid"},"");
                    Log.d("Affichage du tableau final", finalArray.toString());
                    Log.d("Taille tableau final",String.valueOf(finalArray.length()));
                    String param = "point";

                    compare.writeJSONArrayToFile(param, array_json3,finalArray,"map.json");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // Créer un Bundle pour stocker les informations de l'appareil sélectionné
                Bundle bundle = new Bundle();
                bundle.putString("deviceName", deviceName);
                bundle.putString("deviceAddress", deviceAddress);
                bundle.putString("deviceRole", "serveur");
                bundle.putString("deviceRetour", "1111");

                try{
                    // Créer une instance du fragment destination et lui transmettre les informations
                    Fragment destinationFragment = new ExchangeSynchroFragment();
                    destinationFragment.setArguments(bundle);

                    // Redirection vers le fragment Synchronisation

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, destinationFragment);
                    transaction.setReorderingAllowed(true);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    System.out.println("-- Serveur : Redirection Impossible --");
                    e.printStackTrace();
                }

                System.out.println("-- Serveur : Redirection Fin --");



                // Fermez le socket du serveur après la communication

            }
            //serverSocket.close();

        } catch (IOException e) {
            System.out.println("-- Erreur : Serveur --");
            // Une erreur s'est produite lors de l'attente d'une connexion Bluetooth entrante
            e.printStackTrace();
        }
        finally {
            try {
                if (this.socket != null) {
                this.socket.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        System.out.println(" -- Fermeture serveur reception message --");
    }

}