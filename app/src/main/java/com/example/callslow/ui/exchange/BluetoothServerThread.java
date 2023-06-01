package com.example.callslow.ui.exchange;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.callslow.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothServerThread extends Thread {
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID;

    private View parentView;

    private TextView status;

    public BluetoothServerThread(BluetoothAdapter adapter, UUID uuid, View view) {
        bluetoothAdapter = adapter;
        MY_UUID = uuid;
        parentView = view;

        status = (TextView) view.findViewById(R.id.tagServeur);
    }

    @Override
    public void run() {
        try {

            System.out.println(" -- Démarrage serveur reception message --");

            BluetoothServerSocket serverSocket = null;
            try {
                serverSocket= bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("CallSlow", MY_UUID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



            BluetoothSocket socket;
            while (true) {
                status.setText("Serveur : Prêt");
                status.setTextColor(Color.GREEN);
                socket = serverSocket.accept();
                status.setText("Serveur : en cours...");
                status.setTextColor(Color.YELLOW);
                System.out.println("-- Une connexion Bluetooth entrante a été établie avec succès --");


                // Réception fichier message
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);

                String message = new String(buffer, 0, bytesRead);
                System.out.println(message);

                // Envoie fichier message
                OutputStream outputStream = socket.getOutputStream();
                String messageRecu = "Coucou, je suis le serveur";
                outputStream.write(messageRecu.getBytes());

                serverSocket.close(); // Fermez le socket du serveur après la communication
            }

        } catch (IOException e) {
            System.out.println("-- Erreur : Serveur --");
            // Une erreur s'est produite lors de l'attente d'une connexion Bluetooth entrante
            e.printStackTrace();
        }
        System.out.println(" -- Fermeture serveur reception message --");
    }

}

