package com.example.callslow.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentSettingsBinding;
import com.example.callslow.objects.Settings;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private FragmentSettingsBinding binding;
    private ArrayList<String> settingslist;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Settings settings = Settings.getInstance();

        settings.init(getContext());
        settingslist = settings.getSettings();

        EditText editMacAdress = binding.myMacAddress;
        if (settingslist.get(0) != null) {
            editMacAdress.setText(settingslist.get(0));
        }
        EditText editPseudo = binding.myPseudo;
        if (settingslist.get(1) != null) {
            editPseudo.setText(settingslist.get(1));
        }

        Button saveSettingsButton = root.findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(this);

        view = root;

        return root;
    }

    @Override
    public void onClick(View v) {
        EditText editPseudo = binding.myPseudo;
        EditText editMacAddress = binding.myMacAddress;
        try {
            if (!editMacAddress.getText().toString().matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
                alertBox("Adrese MAC Invalide", "Le format de l'adresse MAC saisie est invalide.");
                return;
            } else {
                Settings.getInstance().changePseudo(editPseudo.getText().toString());
            }

            if (editPseudo.getText().toString().equals("")) {
                alertBox("Pseudo Invalide", "Veuillez saisir un pseudo.");
                return;
            } else {
                Settings.getInstance().changeMacAdress(editMacAddress.getText().toString());
            }

            alertBox("Modification des settings", "Enregistrement effectué");
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void alertBox(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}