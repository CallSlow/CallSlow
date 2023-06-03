package com.example.callslow.ui.exchange;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    private FragmentManager fragmentManager;

    public BluetoothServerThread(BluetoothAdapter adapter, UUID uuid, View view, FragmentManager manager) {
        bluetoothAdapter = adapter;
        MY_UUID = uuid;
        parentView = view;
        status = (TextView) view.findViewById(R.id.tagServeur);
        fragmentManager = manager;
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
                String retour1 = (message != null) ? "1" : "0";

                // Envoie fichier message
                OutputStream outputStream = socket.getOutputStream();
                String messageRecu = "Coucou, je suis le fichier message";
                outputStream.write(messageRecu.getBytes());
                String retour2 = (messageRecu != null) ? "1" : "0";

                // Réception fichier Boite
                InputStream inputStream2 = socket.getInputStream();
                byte[] buffer2 = new byte[1024];
                int bytesRead2 = inputStream2.read(buffer2);
                String message2 = new String(buffer2, 0, bytesRead2);
                System.out.println(message2);
                String retour3 = (message2 != null) ? "1" : "0";

                // Envoie fichier Boite
                OutputStream outputStream2 = socket.getOutputStream();
                String messageRecu2 = "Coucou, je suis le fichier boite";
                outputStream2.write(messageRecu2.getBytes());
                String retour4 = (messageRecu2 != null) ? "1" : "0";


                // Ouverture de la page de sychronisation
                status.setText("Serveur : Redirection");
                System.out.println("-- Serveur : Redirection --");
                    String deviceName = bluetoothAdapter.getName();
                    String deviceAddress = bluetoothAdapter.getAddress();

                    System.out.println(deviceName);
                    System.out.println(deviceAddress);

                    // Créer un Bundle pour stocker les informations de l'appareil sélectionné
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceName", deviceName);
                    bundle.putString("deviceAddress", deviceAddress);
                    bundle.putString("deviceRole", "serveur");
                    bundle.putString("deviceRetour", retour1 + retour2 + retour3 + retour4);

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
                socket.close();

            }
            //serverSocket.close();

        } catch (IOException e) {
            System.out.println("-- Erreur : Serveur --");
            // Une erreur s'est produite lors de l'attente d'une connexion Bluetooth entrante
            e.printStackTrace();
        }

        System.out.println(" -- Fermeture serveur reception message --");
    }

}

