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
import java.nio.channels.FileChannel;

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
import java.util.ArrayList;
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
                    if (buffer[bytesRead-1] == -128) {
                        fileOutputStream.write(buffer, 0, bytesRead - 1);
                        break;
                    } else {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }

                fileOutputStream.flush();
                fileOutputStream.close();
               // String retour1 = (message != null) ? "1" : "0";

                // Envoie fichier message
                FileInputStream fileStream1 = this.context.openFileInput("messages.json");
                InputStream fileInputStream = new BufferedInputStream(fileStream1);

                byte[] buffer1 = new byte[BUFFER_SIZE];
                int bytesRead1 = 0;

                while ((bytesRead1 = fileInputStream.read(buffer1)) != -1) {
                    outputStream.write(buffer1, 0, bytesRead1);
                }
                outputStream.write(-128);
                Log.d("Fichier - Envoi - Client","C'est bon");
                outputStream.flush();
                fileInputStream.close();

              //  String retour2 = (messageRecu != null) ? "1" : "0";

                // Réception fichier Boite

                FileOutputStream fileStream_bal1 = context.openFileOutput("map_exchange.json", Context.MODE_PRIVATE);
                OutputStream fileOutputStream_bal1 = new BufferedOutputStream(fileStream_bal1);

                byte[] buffer_bal1 = new byte[BUFFER_SIZE];
                int bytesRead_bal1 = 0;

                while (inputStream_BAL.available() == 0) {
                    // attente

                }
                while ((bytesRead_bal1 = inputStream_BAL.read(buffer_bal1)) != -1) {
                    if (buffer_bal1[bytesRead_bal1-1] == -128) {
                        fileOutputStream_bal1.write(buffer_bal1, 0, bytesRead_bal1 - 1);
                        break;
                    } else {
                        fileOutputStream_bal1.write(buffer_bal1, 0, bytesRead_bal1);
                    }
                }

                fileOutputStream_bal1.flush();
                fileOutputStream_bal1.close();

                //  String retour3 = (message2 != null) ? "1" : "0";

                // Envoie fichier Boite

                FileInputStream fileStream_bal =  context.openFileInput("map.json");
                InputStream fileInputStream_bal = new BufferedInputStream(fileStream_bal);

                byte[] buffer_bal = new byte[BUFFER_SIZE];
                int bytesRead_bal = 0;

                while ((bytesRead_bal = fileInputStream_bal.read(buffer_bal)) != -1) {
                    outputStream_BAL.write(buffer_bal, 0, bytesRead_bal);
                }

                outputStream_BAL.write(-128);
                Log.d("Fichier - Envoi - Client","C'est bon");
                outputStream_BAL.flush();
                fileInputStream_bal.close();

              //   String retour4 = (messageRecu2 != null) ? "1" : "0";

                // Ouverture de la page de sychronisation
                writeToView("Serveur : Redirection", Color.BLUE);

                System.out.println("-- Serveur : Redirection --");


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