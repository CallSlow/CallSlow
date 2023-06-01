package com.example.callslow.ui.exchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.callslow.databinding.FragmentEchangeMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothServerThread extends Thread {
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID MY_UUID;

    public BluetoothServerThread(BluetoothAdapter adapter, UUID uuid) {
        bluetoothAdapter = adapter;
        MY_UUID = uuid;
    }

    @Override
    public void run() {
        try {

            System.out.println(" -- Démarrage serveur reception message --");

            BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("CallSlow", MY_UUID);
            BluetoothSocket socket = serverSocket.accept();
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
        } catch (IOException e) {
            System.out.println("-- Erreur : Serveur --");
            // Une erreur s'est produite lors de l'attente d'une connexion Bluetooth entrante
            e.printStackTrace();
        }
        System.out.println(" -- Fermeture serveur reception message --");
    }

}

