package com.example.callslow.ui.exchange;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeMainBinding;
import com.example.callslow.databinding.FragmentEchangeSynchroBinding;

import java.util.ArrayList;
import java.util.List;

public class ExchangeSynchroFragment extends Fragment {

    private FragmentEchangeSynchroBinding binding;

    ListView mListView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        binding = FragmentEchangeSynchroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button mBtnRetour;
        mBtnRetour = root.findViewById(R.id.BtnRetour);
        mBtnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment exchangeFragment = new ExchangeFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, exchangeFragment);
                transaction.setReorderingAllowed(true);
                transaction.addToBackStack(null);
                transaction.commit();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}