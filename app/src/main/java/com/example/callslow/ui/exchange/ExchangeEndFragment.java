package com.example.callslow.ui.exchange;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.MainActivity;
import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeEndBinding;
import com.example.callslow.databinding.FragmentEchangeSynchroBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ExchangeEndFragment extends Fragment {

    private FragmentEchangeEndBinding binding;

    ListView mListView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        binding = FragmentEchangeEndBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button mBtnRetour;
        mBtnRetour = root.findViewById(R.id.buttonEnd);
        mBtnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                BottomNavigationView navView = mainActivity.findViewById(R.id.nav_view);
                navView.setEnabled(false);
                navView.setVisibility(View.VISIBLE);

                Fragment exchangeFragment = new ExchangeFragment();
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