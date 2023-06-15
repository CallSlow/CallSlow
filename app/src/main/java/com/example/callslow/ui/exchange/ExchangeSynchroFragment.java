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

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeSynchroBinding;
import com.example.callslow.objects.Comparaison;
import com.example.callslow.objects.Settings;

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

        if(deviceRole == "client") {

            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

            try {
                this.socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                this.socket.connect();
                try {
                    InputStream inputStream = this.socket.getInputStream();
                    OutputStream outputStream = this.socket.getOutputStream();

                    InputStream inputStream_BAL = this.socket.getInputStream();
                    OutputStream outputStream_BAL = this.socket.getOutputStream();


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
                        e.printStackTrace();
                    }

                    outputStream.write(result.getBytes());
                    outputStream.write(-128);
                    outputStream.flush();
                    fileInputStream.close();


                    // Si success
                    textCheckEchangeEnvoyer = root.findViewById(R.id.textCheckEchangeEnvoyerTag);
                    textCheckEchangeEnvoyer.setTextColor(Color.GREEN);
                    textCheckEchangeEnvoyer.setText("\u2714");

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
                            if (buffer[bytesRead-1] == -128) {
                                fileOutputStream.write(buffer, 0, bytesRead-1);
                                break;
                            } else {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();


                    // Si success
                    textCheckEchangeRecu = root.findViewById(R.id.textCheckEchangeRecuTag);
                    textCheckEchangeRecu.setTextColor(Color.GREEN);
                    textCheckEchangeRecu.setText("\u2714");

                    // Envoyer le fichier boite au lettre
                    FileInputStream fileStream_bal = getContext().openFileInput("map.json");
                    InputStream fileInputStream_bal = new BufferedInputStream(fileStream_bal);

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

                    // Si success
                    textCheckBoiteEnvoyer = root.findViewById(R.id.textCheckBoiteEnvoyerTag);
                    textCheckBoiteEnvoyer.setTextColor(Color.GREEN);
                    textCheckBoiteEnvoyer.setText("\u2714");

                    // Réception fichier boite au lettre
                    FileOutputStream fileStream_bal1 = getContext().openFileOutput("map_exchange.json", Context.MODE_PRIVATE);
                    OutputStream fileOutputStream_bal1 = new BufferedOutputStream(fileStream_bal1);

                    byte[] buffer_bal1 = new byte[BUFFER_SIZE];
                    int bytesRead_bal1 = 0;

                    while (inputStream_BAL.available() == 0) {
                        // on attend
                    }

                    if (inputStream_BAL.available() > 0) {
                        while ((bytesRead_bal1 = inputStream_BAL.read(buffer_bal1)) != -1) {
                            if (buffer_bal1[bytesRead_bal1-1] == -128) {
                                fileOutputStream_bal1.write(buffer_bal1, 0, bytesRead_bal1-1);
                                break;
                            } else {
                                fileOutputStream_bal1.write(buffer_bal1, 0, bytesRead_bal1);

                            }
                        }
                    }

                    fileOutputStream_bal1.flush();
                    fileOutputStream_bal1.close();

                    textCheckBoiteRecu = root.findViewById(R.id.textCheckBoiteRecuTag);
                    textCheckBoiteRecu.setTextColor(Color.GREEN);
                    textCheckBoiteRecu.setText("\u2714");

                    Comparaison compare = new Comparaison(getContext());
                    String json_list_1 = compare.readFiletoJSONFile("messages.json");
                    String json_list_2 = compare.readFiletoJSONFile("messages_exchange.json");

                    // Comparaison liste de messages
                    try {
                        JSONObject message_json1 = new JSONObject(json_list_1);
                        JSONObject message_json2 = new JSONObject(json_list_2);

                        Log.d("Comparaison Objet JSON",String.valueOf(message_json1));
                        Log.d("Comparaison Objet JSON 2",String.valueOf(message_json2));

                        JSONArray array_json1 = message_json1.getJSONArray("messages");
                        JSONArray array_json2 = message_json2.getJSONArray("messages");

                         Log.d("Affichage du premier tableau", array_json1.toString());
                         Log.d("Affichage du deuxiÃ¨me tableau", array_json2.toString());

                        System.out.println(Settings.getInstance().getSettings().get(0));

                        JSONArray finalArray = compare.getNewValues(array_json1, array_json2, new String[]{"uuid"});
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

                        JSONArray finalArray = compare.getNewValues(array_json3, array_json4, new String[]{"uuid"});
                        Log.d("Affichage du tableau final", finalArray.toString());
                        Log.d("Taille tableau final",String.valueOf(finalArray.length()));
                        String param = "point";

                        compare.writeJSONArrayToFile(param, array_json3,finalArray,"map.json");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
        if(deviceRole == "serveur") {
            Toast.makeText(requireContext(), deviceRetour, Toast.LENGTH_SHORT).show();
            System.out.println(deviceRetour);
            if(deviceRetour.charAt(0) == '1'){
                textCheckEchangeEnvoyer = root.findViewById(R.id.textCheckEchangeEnvoyerTag);
                textCheckEchangeEnvoyer.setTextColor(Color.GREEN);
                textCheckEchangeEnvoyer.setText("\u2714");
            } else {
                textCheckEchangeEnvoyer = root.findViewById(R.id.textCheckEchangeEnvoyerTag);
                textCheckEchangeEnvoyer.setTextColor(Color.RED);
                textCheckEchangeEnvoyer.setText("\u274C");
            }

            if(deviceRetour.charAt(1) == '1'){
                textCheckEchangeRecu = root.findViewById(R.id.textCheckEchangeRecuTag);
                textCheckEchangeRecu.setTextColor(Color.GREEN);
                textCheckEchangeRecu.setText("\u2714");
            } else {
                textCheckEchangeRecu = root.findViewById(R.id.textCheckEchangeRecuTag);
                textCheckEchangeRecu.setTextColor(Color.RED);
                textCheckEchangeRecu.setText("\u274C");
            }

            if(deviceRetour.charAt(2) == '1'){
                textCheckBoiteEnvoyer = root.findViewById(R.id.textCheckBoiteEnvoyerTag);
                textCheckBoiteEnvoyer.setTextColor(Color.GREEN);
                textCheckBoiteEnvoyer.setText("\u2714");
            } else {
                textCheckBoiteEnvoyer = root.findViewById(R.id.textCheckBoiteEnvoyerTag);
                textCheckBoiteEnvoyer.setTextColor(Color.RED);
                textCheckBoiteEnvoyer.setText("\u274C");
            }

            if(deviceRetour.charAt(3) == '1'){
                textCheckBoiteRecu = root.findViewById(R.id.textCheckBoiteRecuTag);
                textCheckBoiteRecu.setTextColor(Color.GREEN);
                textCheckBoiteRecu.setText("\u2714");
            } else {
                textCheckBoiteRecu = root.findViewById(R.id.textCheckBoiteRecuTag);
                textCheckBoiteRecu.setTextColor(Color.RED);
                textCheckBoiteRecu.setText("\u274C");
            }
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
        Log.d("Destruction vue","Oui");
        super.onDestroyView();

        Fragment exchangeFragment = new ExchangeFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, exchangeFragment);
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();

        binding = null;

    }
}
