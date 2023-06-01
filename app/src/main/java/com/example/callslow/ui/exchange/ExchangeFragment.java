package com.example.callslow.ui.exchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // private

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!devicesList.contains(device)) {
                    devicesList.add(device);
                    if (device.getName() != null && device.getName().contains("*")) {
                        System.out.println("-");
                        System.out.println(device);
                        System.out.println(device.getName());
                        System.out.println(device.getAddress());
                        System.out.println("-");
                        possibleNames.add(device.getName());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            /*if(possibleNames.size() == 0){
                mTagView = root.findViewById(R.id.tagAppareil);
                mTagView.setText("Aucun appareil trouvé ...");
            } else {
                mTagView.setText("");
            }*/
        }
    };


    // Lance un serveur pour reception message en arriere plan

    private List<String> getPairedDeviceNames() {
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
                startDiscovery();
            } else {
                Toast.makeText(getActivity(), "Permission Bluetooth refusée", Toast.LENGTH_SHORT).show();
            }
        });
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        mListView = root.findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, possibleNames);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mListView2 = root.findViewById(R.id.listViewConnu);
        mAdapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, pairedDeviceNames);
        mAdapter2.addAll(getPairedDeviceNames());
        mListView2.setAdapter(mAdapter2);
        mListView2.setOnItemClickListener(this);


        Button BtnServeur;
        BtnServeur = root.findViewById(R.id.BtnServeur);
        BtnServeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothServerThread server = new BluetoothServerThread(bluetoothAdapter, MY_UUID);
                Thread thread = new Thread(server);
                thread.start();
                Toast.makeText(getActivity(), "Start server", Toast.LENGTH_SHORT).show();

            }
        });

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // Temps de visibilité en secondes
        startActivity(discoverableIntent);



        return root;
    }

    @SuppressLint("MissingPermission")
    private void startDiscovery() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);

        devicesList.clear();

        pairedDeviceNames.addAll(getPairedDeviceNames());

        if (checkAndRequestBluetoothPermissions(getContext())) {
            // Commence la découverte des appareils Bluetooth inconnus
            adapter.startDiscovery();

        } else {
            System.out.println("Pas toutes les perm ---");
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Arrête la découverte des appareils Bluetooth et retire le BroadcastReceiver
        if (adapter != null) {
            adapter.cancelDiscovery();
        }
        getActivity().unregisterReceiver(broadcastReceiver);
        binding = null;

        pairedDeviceNames.clear();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String deviceName = possibleNames.get(position);
        String deviceAddress = devicesList.get(position).getAddress();

        System.out.println(deviceName);
        System.out.println(deviceAddress);

        // Créer un Bundle pour stocker les informations de l'appareil sélectionné
        Bundle bundle = new Bundle();
        bundle.putString("deviceName", deviceName);
        bundle.putString("deviceAddress", deviceAddress);

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
        String[] permissions = new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT};
        for (String permission : permissions) {
            // Check if the necessary permission is not granted and set allPermissionsGranted to false
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // Setting the allPermissionsGranted flag to false
                allPermissionsGranted = false;
                // Requesting the necessary permissions for Bluetooth discovery from the user
                ActivityCompat.requestPermissions((Activity) context, permissions, 1);

                System.out.println("***********  Requesting bluetooth permissions");
                break;
            }
        }
        return allPermissionsGranted;
    }

}