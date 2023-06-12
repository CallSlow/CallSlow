package com.example.callslow.ui.settings;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.databinding.FragmentSettingsBinding;
import com.example.callslow.objects.Contacts;
import com.example.callslow.objects.Settings;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private ArrayList<String> settingslist;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Settings.getInstance().init(getContext());
        settingslist = Settings.getInstance().getSettings();

        final TextView textView = binding.textView;
        EditText macAdress = binding.myMacAdress;
        if (settingslist.get(0) != null) {
            macAdress.setText(settingslist.get(0));
        } else {
            WifiManager manager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String address = info.getMacAddress();
            macAdress.setText(address);
            try {
                Settings.getInstance().changeMacAdress(address);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        EditText pseudo = binding.myPseudo;
        if (settingslist.get(1) != null) {
            pseudo.setText(settingslist.get(1));
        } else {
            pseudo.setText("Entrez votre pseudo");
            try {
                Settings.getInstance().changePseudo(pseudo.getText().toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}