package com.example.callslow;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.callslow.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("---- Début du programme ---- \n");
        this.checkBluetooth();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_exchange, R.id.navigation_chat, R.id.navigation_maps, R.id.navigation_contact, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    protected void checkBluetooth() {

        System.out.println("--- Début CheckBluetooth --- \n");
        // Initialiser un  BluetoothManager pour pouvoir utiliser les fonctions Bluetooth
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Fermer l'application => Mettre une fenêtre d'erreur
        }

        System.out.println(bluetoothAdapter.toString());

        // isEnabled => regarde si le Bluetooth est activé
        if (!bluetoothAdapter.isEnabled()) {
            System.out.println("--- Pas de Bluetooth ---");
            Intent bluetoothPicker = new Intent("android.provider.Settings.ACTION_BLUETOOTH_SETTINGS"); //  A BOUGER
            startActivity(bluetoothPicker);
        }
        else {
            System.out.println("Bluetooth yiyoug)àrsdegseser");
        }


    }

}