package com.example.callslow.ui.exchange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class ExchangeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    protected FragmentEchangeMainBinding binding;
    protected BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private List<String> possibleNames = new ArrayList<>();

    private final List<BluetoothDevice> devicesList = new ArrayList<>();
    private View root;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!devicesList.contains(device)) {
                    devicesList.add(device);
                    if(device.getName() != null) {
                        System.out.println(device.getName());
                        possibleNames.add(device.getName());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };


    // ArrayList prenoms = new ArrayList {"Pixel 4", "SlowChatBox Lille", "Pixel 4", "SlowChatBox Hellemmes"};

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
        return root;
    }

    @SuppressLint("MissingPermission")
    private void startDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);

        devicesList.clear();

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
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment exchangeSynchroFragment = new ExchangeSynchroFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, exchangeSynchroFragment);
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