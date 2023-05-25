package com.example.callslow.ui.exchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeMainBinding;
import com.example.callslow.databinding.FragmentEchangeSynchroBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExchangeSynchroFragment extends Fragment {

    private FragmentEchangeSynchroBinding binding;

    ListView mListView;

    private TextView monTextView;

    private TextView textCheckEchangeEnvoyer;
    private TextView textCheckBoiteEnvoyer;
    private TextView textCheckEchangeRecu;
    private TextView textCheckBoiteRecu;
    String deviceName = "test";
    String deviceAddress = "00:00:00:00:00";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        binding = FragmentEchangeSynchroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Récupérer les informations de l'appareil transmises par le premier fragment
        Bundle args = getArguments();
        if (args != null) {
            deviceName = args.getString("deviceName");
            deviceAddress = args.getString("deviceAddress");
            monTextView = root.findViewById(R.id.nameDevice);
            monTextView.setText(deviceName);
        } else {
            System.out.println("Erreur : Le bundle est null");
        }

        System.out.println("Début de connexion");

        // Obtention de l'adresse Bluetooth du périphérique distant
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        // Création d'un socket Bluetooth pour la communication sortante
        @SuppressLint("MissingPermission") BluetoothSocket socket = null;
        try {

            Toast.makeText(binding.getRoot().getContext(), "Connexion : Recherche", Toast.LENGTH_SHORT).show();

            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();

            Toast.makeText(binding.getRoot().getContext(), "Connexion : ok", Toast.LENGTH_SHORT).show();

            try {
                // Obtenir les flux d'entrée et de sortie de la socket
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                // Envoyer le fichier échange
                String message = "Coucou, je suis le client";
                outputStream.write(message.getBytes());
                textCheckEchangeEnvoyer = root.findViewById(R.id.textCheckEchangeEnvoyer);
                textCheckEchangeEnvoyer.setTextColor(Color.parseColor("#00FF00"));

                // Réception fichier message
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                textCheckEchangeRecu = root.findViewById(R.id.textCheckEchangeRecu);
                textCheckEchangeRecu.setTextColor(Color.GREEN);

                String messageRecu = new String(buffer, 0, bytesRead);
                System.out.println(messageRecu);
                Toast.makeText(binding.getRoot().getContext(), messageRecu, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(binding.getRoot().getContext(), "Fin de l'échange", Toast.LENGTH_SHORT).show();
            socket.close();
        } catch (IOException e) {
            Toast.makeText(binding.getRoot().getContext(), "Connexion : impossible", Toast.LENGTH_SHORT).show();
            retour();

            // throw new RuntimeException(e);
        }



        Button mBtnRetour;
        mBtnRetour = root.findViewById(R.id.BtnRetour);
        mBtnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retour();
            }
        });


        Button mBtnSuivant;
        mBtnSuivant = root.findViewById(R.id.BtnSuivant);
        mBtnSuivant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment exchangeFragment = new ExchangeEndFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, exchangeFragment);
                transaction.setReorderingAllowed(true);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return root;
    }

    public void retour() {
        Fragment exchangeFragment = new ExchangeFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, exchangeFragment);
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}