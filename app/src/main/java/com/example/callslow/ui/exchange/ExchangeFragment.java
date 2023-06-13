package com.example.callslow.ui.exchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeMainBinding;
import com.google.android.material.transition.MaterialSharedAxis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ExchangeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    protected FragmentEchangeMainBinding binding;
    protected BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private List<String> possibleNames = new ArrayList<>();
    private List<String> pairedDeviceNames = new ArrayList<>();

    private final List<BluetoothDevice> devicesList = new ArrayList<>();
    private View root;

    private TextView mTagConnuView;

    private TextView mTagView;
    private ListView mListView;
    private ListView mListView2;
    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> mAdapter2;

    private ArrayList<BluetoothDevice> liste_found = new ArrayList<>();

    private static final int REQUEST_CODE_PERMISSION = 100;

    private static final UUID MY_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // private

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BRECIEV", "broadcastReciever");

            String action = intent.getAction();
            Log.i("BRECIEV", "Action : " + action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null && device.getName() != null && device.getBondState() != BluetoothDevice.BOND_BONDED && !liste_found.contains(device)) {
                    if (device.getName().length() != 0) {
                        possibleNames.add(device.getName() + " - " + device.getAddress());
                        liste_found.add(device);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // PASS
                if (liste_found.size() == 0) {
                    Log.i("BRECIEV", "Aucun appareil trouvés.");
                }
            }

        }
    };


    private boolean permissionChecker() {
        boolean perm1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH)  != PackageManager.PERMISSION_GRANTED;
        boolean perm2 = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.BLUETOOTH_ADMIN)  != PackageManager.PERMISSION_GRANTED;

        if ( perm1 || perm2) {
            // fix pour android 12+
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN)  != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT)  != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            }
        }



        return true;
    }


    // Lance un serveur pour reception message en arriere plan

    private List<String> getPairedDeviceNames() {
        // retourne du vide si pas les perms
        if (!permissionChecker()) {
            return new ArrayList<String>();
        }


        List<String> deviceNames = new ArrayList<>();

        // Récupère les appareils appairés
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            if (deviceName != null && deviceName.contains("*")) {
                deviceNames.add(deviceName);
            }
        }
        /*if(deviceNames.size() == 0){
            mTagConnuView = root.findViewById(R.id.tagAppareilConnu);
            mTagConnuView.setText("Aucun appareil trouvé ...");
        } else {
            mTagConnuView.setText("");
        }*/


        return deviceNames;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        binding = FragmentEchangeMainBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        // Demande la permission Bluetooth
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (permissionChecker()) {
                    startDiscovery();
                } else {
                    Toast.makeText(getActivity(), "Permission Bluetooth refusée (1). Autorisez les permissions et revenez à la vue !!", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_CODE_PERMISSION);
                    }

                }
            } else {
                Toast.makeText(getActivity(), "Permission Bluetooth refusée (2). Autorisez les permissions et revenez à la vue !!", Toast.LENGTH_SHORT).show();
            }
        });
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN);
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
        }


        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(broadcastReceiver, filter);


        mListView = root.findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, possibleNames);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        /*mListView2 = root.findViewById(R.id.listViewConnu);
        mAdapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, pairedDeviceNames);
        mAdapter2.addAll(getPairedDeviceNames());
        mListView2.setAdapter(mAdapter2);
        mListView2.setOnItemClickListener(this);*/


        Button BtnServeur;
        BtnServeur = root.findViewById(R.id.BtnServeur);
        BtnServeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothServerThread server = new BluetoothServerThread(bluetoothAdapter, MY_UUID, getView(), getParentFragmentManager());

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // Temps de visibilité en secondes
                startActivity(discoverableIntent);

                Thread thread = new Thread(server);
                thread.start();
                Toast.makeText(getActivity(), "Start server", Toast.LENGTH_SHORT).show();

            }
        });

        Button BtnRecherche;
        BtnRecherche = root.findViewById(R.id.BtnRecherche);
        BtnRecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }});

        return root;
    }

    @SuppressLint("MissingPermission")
    private void startDiscovery() {
        Log.d("DISCOVER", "startDiscovery()");
        devicesList.clear();

        pairedDeviceNames.addAll(getPairedDeviceNames());

        if (true) {
        //if (checkAndRequestBluetoothPermissions(getContext())) {
            // Commence la découverte des appareils Bluetooth inconnus
            if (adapter.isDiscovering()) {
                adapter.cancelDiscovery();
            }


            if (!adapter.startDiscovery()) {
                showErrorDialog("La discovery n'a pu démarrer en raison des autorisations.\nVérifiez les permissions et réessayez");
            } else {
                Log.d("DISCOVER", "Découverte des périphériques en cours...");
            }


        } else {
            Log.d("DISCOVER", "Manque de permissions...");
        }

    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Erreur")
                .setMessage(errorMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Code à exécuter lorsque l'utilisateur clique sur le bouton OK
                        dialog.dismiss(); // Fermer la boîte de dialogue
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Arrête la découverte des appareils Bluetooth et retire le BroadcastReceiver
        if (adapter != null) {
            if (permissionChecker())
                adapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(broadcastReceiver);
        binding = null;

        pairedDeviceNames.clear();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // on arrête la découverte
        adapter.cancelDiscovery();
        BluetoothDevice selected = null;

        for (BluetoothDevice d : liste_found) {
            if (possibleNames.get(position).contains(d.getAddress())) {
                selected = d;
                break;
            }
        }

        if (selected == null) {return;}

        String deviceName = selected.getName();
        String deviceAddress = selected.getAddress();

        System.out.println(deviceName);
        System.out.println(deviceAddress);

        // Créer un Bundle pour stocker les informations de l'appareil sélectionné
        Bundle bundle = new Bundle();
        bundle.putString("deviceName", deviceName);
        bundle.putString("deviceAddress", deviceAddress);
        bundle.putString("deviceRole", "client");

        // Créer une instance du fragment destination et lui transmettre les informations
        Fragment destinationFragment = new ExchangeSynchroFragment();
        destinationFragment.setArguments(bundle);

        // Redirection vers le fragment Synchronisation
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, destinationFragment);
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    // method to check and request necessary permissions for Bluetooth discovery
    private boolean checkAndRequestBluetoothPermissions(Context context) {
        // Set allPermissionsGranted to true, assuming all necessary permissions are granted by default
        boolean allPermissionsGranted = true;
        // Create an array of strings containing the necessary permissions for Bluetooth discovery
        String[] permissions = new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT};
        for (String permission : permissions) {
            // Check if the necessary permission is not granted and set allPermissionsGranted to false
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // Setting the allPermissionsGranted flag to false
                allPermissionsGranted = false;
                Toast.makeText(context, permission, Toast.LENGTH_SHORT).show();
                // Requesting the necessary permissions for Bluetooth discovery from the user
                ActivityCompat.requestPermissions((Activity) context, permissions, 1);

                System.out.println("***********  Requesting bluetooth permissions");
                break;
            }
        }
        return allPermissionsGranted;
    }

}