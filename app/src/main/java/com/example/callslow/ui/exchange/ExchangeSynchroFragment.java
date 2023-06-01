package com.example.callslow.ui.exchange;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeSynchroBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ExchangeSynchroFragment extends Fragment {

    private FragmentEchangeSynchroBinding binding;
    private TextView monTextView;
    private TextView textCheckEchangeEnvoyer;
    private TextView textCheckEchangeRecu;
    private String deviceName = "test";
    private String deviceAddress = "00:00:00:00:00";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

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
            Toast.makeText(requireContext(), "Erreur : Le bundle est null", Toast.LENGTH_SHORT).show();
        }

        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

        try {
            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            try {
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
                Toast.makeText(requireContext(), messageRecu, Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Connexion : impossible", Toast.LENGTH_SHORT).show();
            retour();
        }

        Button mBtnRetour = root.findViewById(R.id.BtnRetour);
        mBtnRetour.setOnClickListener(v -> retour());

        Button mBtnSuivant = root.findViewById(R.id.BtnSuivant);
        mBtnSuivant.setOnClickListener(v -> {
            Fragment exchangeFragment = new ExchangeEndFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, exchangeFragment);
            transaction.setReorderingAllowed(true);
            transaction.addToBackStack(null);
            transaction.commit();
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
