package com.example.callslow.ui.exchange;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.MainActivity;
import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeSynchroBinding;
import com.example.callslow.objects.Comparaison;
import com.example.callslow.objects.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

public class ExchangeSynchroFragment extends Fragment {

    private FragmentEchangeSynchroBinding binding;
    private TextView monTextView;
    private TextView textCheckEchangeEnvoyer;
    private TextView textCheckEchangeRecu;

    private TextView textCheckBoiteRecu;
    private TextView textCheckBoiteEnvoyer;
    private String deviceName = "test";

    private String deviceRole = "autre";
    private String deviceRetour = "0000";
    private String deviceAddress = "00:00:00:00:00";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private BluetoothSocket socket;
    private final int BUFFER_SIZE = 8192;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        binding = FragmentEchangeSynchroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity mainActivity = (MainActivity) getActivity();
        BottomNavigationView navView = mainActivity.findViewById(R.id.nav_view);
        navView.setEnabled(false);
        navView.setVisibility(View.INVISIBLE);


        // Récupérer les informations de l'appareil transmises par le premier fragment
        Bundle args = getArguments();
        if (args != null) {
            deviceName = args.getString("deviceName");
            deviceAddress = args.getString("deviceAddress");
            deviceRole = args.getString("deviceRole");
            deviceRetour = args.getString("deviceRetour");

            monTextView = root.findViewById(R.id.nameDevice);
            monTextView.setText(deviceName);
        } else {
            Toast.makeText(requireContext(), "Erreur : Le bundle est null", Toast.LENGTH_SHORT).show();
        }

        if (deviceRole == "client") {

            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

            try {
                this.socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                this.socket.connect();
                try {
                    String retour1 = "1";
                    String retour2 = "1";
                    String retour3 = "1";
                    String retour4 = "1";

                    InputStream inputStream = this.socket.getInputStream();
                    OutputStream outputStream = this.socket.getOutputStream();

                    InputStream inputStream_BAL = this.socket.getInputStream();
                    OutputStream outputStream_BAL = this.socket.getOutputStream();

                    try {
                        // Envoyer le fichier message
                        FileInputStream fileStream1 = getContext().openFileInput("messages.json");
                        InputStream fileInputStream = new BufferedInputStream(fileStream1);


                        String result = "";
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                            StringWriter writer = new StringWriter();

                            char[] buffer = new char[4096];
                            int bytesRead;
                            while ((bytesRead = reader.read(buffer)) != -1) {
                                writer.write(buffer, 0, bytesRead);
                            }

                            result = writer.toString();
                        } catch (IOException e) {
                            retour1 = "0";
                            e.printStackTrace();
                        } catch (Exception e) {
                            retour1 = "0";
                            e.printStackTrace();
                        }

                        outputStream.write(result.getBytes());
                        outputStream.write(-128);
                        outputStream.flush();
                        fileInputStream.close();
                    } catch (Exception e) {
                        retour1 = "0";
                    }

                    try {
                        // Réception fichier message
                        FileOutputStream fileStream = getContext().openFileOutput("messages_exchange.json", Context.MODE_PRIVATE);
                        OutputStream fileOutputStream = new BufferedOutputStream(fileStream);

                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead = 0;


                        while (inputStream.available() == 0) {
                            // on attend
                        }

                        if (inputStream.available() > 0) {
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                if (buffer[bytesRead - 1] == -128) {
                                    fileOutputStream.write(buffer, 0, bytesRead - 1);
                                    break;
                                } else {
                                    fileOutputStream.write(buffer, 0, bytesRead);
                                }
                            }
                        }
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        retour3 = "0";
                        e.printStackTrace();
                    } catch (Exception e) {
                        retour3 = "0";
                        e.printStackTrace();
                    }

                    // Envoyer le fichier boite au lettre
                    try {
                        FileInputStream fileStream_bal = getContext().openFileInput("map.json");
                        InputStream fileInputStream_bal = new BufferedInputStream(fileStream_bal);
                        String result = "";

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream_bal));
                            StringWriter writer = new StringWriter();

                            char[] buffer_bal = new char[4096];
                            int bytesRead_bal;
                            while ((bytesRead_bal = reader.read(buffer_bal)) != -1) {
                                writer.write(buffer_bal, 0, bytesRead_bal);
                            }

                            result = writer.toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        outputStream_BAL.write(result.getBytes());
                        outputStream_BAL.write(-128);
                        outputStream_BAL.flush();
                        fileInputStream_bal.close();
                    } catch (IOException e) {
                        retour2 = "0";
                        e.printStackTrace();
                    } catch (Exception e) {
                        retour2 = "0";
                        e.printStackTrace();
                    }

                    // Réception fichier boite au lettre
                    try {
                        FileOutputStream fileStream_bal1 = getContext().openFileOutput("map_exchange.json", Context.MODE_PRIVATE);
                        OutputStream fileOutputStream_bal1 = new BufferedOutputStream(fileStream_bal1);

                        byte[] buffer_bal1 = new byte[BUFFER_SIZE];
                        int bytesRead_bal1 = 0;

                        while (inputStream_BAL.available() == 0) {
                            // on attend
                        }

                        if (inputStream_BAL.available() > 0) {
                            while ((bytesRead_bal1 = inputStream_BAL.read(buffer_bal1)) != -1) {
                                if (buffer_bal1[bytesRead_bal1 - 1] == -128) {
                                    fileOutputStream_bal1.write(buffer_bal1, 0, bytesRead_bal1 - 1);
                                    break;
                                } else {
                                    fileOutputStream_bal1.write(buffer_bal1, 0, bytesRead_bal1);

                                }
                            }
                        }
                        fileOutputStream_bal1.flush();
                        fileOutputStream_bal1.close();
                    } catch (IOException e) {
                        retour4 = "0";
                        e.printStackTrace();
                    } catch (Exception e) {
                        retour4 = "0";
                        e.printStackTrace();
                    }

                    deviceRetour = retour1 + retour2 + retour3 + retour4;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (this.socket != null) {
                        this.socket.close();
                    }
                }
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Connexion : impossible", Toast.LENGTH_SHORT).show();
                retour();
            }
        }

        textCheckEchangeEnvoyer = root.findViewById(R.id.textCheckEchangeEnvoyerTag);
        textCheckEchangeRecu = root.findViewById(R.id.textCheckEchangeRecuTag);
        textCheckBoiteEnvoyer = root.findViewById(R.id.textCheckBoiteEnvoyerTag);
        textCheckBoiteRecu = root.findViewById(R.id.textCheckBoiteRecuTag);

        if (deviceRetour.charAt(0) == '1') {
            textCheckEchangeEnvoyer.setTextColor(Color.GREEN);
            textCheckEchangeEnvoyer.setText("\u2714");
        } else {
            textCheckEchangeEnvoyer.setTextColor(Color.RED);
            textCheckEchangeEnvoyer.setText("\u274C");
        }

        if (deviceRetour.charAt(2) == '1') {
            textCheckEchangeRecu.setTextColor(Color.GREEN);
            textCheckEchangeRecu.setText("\u2714");
        } else {
            textCheckEchangeRecu.setTextColor(Color.RED);
            textCheckEchangeRecu.setText("\u274C");
        }

        if (deviceRetour.charAt(1) == '1') {
            textCheckBoiteEnvoyer.setTextColor(Color.GREEN);
            textCheckBoiteEnvoyer.setText("\u2714");
        } else {
            textCheckBoiteEnvoyer.setTextColor(Color.RED);
            textCheckBoiteEnvoyer.setText("\u274C");
        }

        if (deviceRetour.charAt(3) == '1') {
            textCheckBoiteRecu.setTextColor(Color.GREEN);
            textCheckBoiteRecu.setText("\u2714");
        } else {
            textCheckBoiteRecu.setTextColor(Color.RED);
            textCheckBoiteRecu.setText("\u274C");
        }

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
        Log.d("Destruction vue", "Oui");
        super.onDestroyView();
        binding = null;

    }
}
