package com.example.callslow.ui.exchange;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeMainBinding;

import java.util.ArrayList;
import java.util.List;

public class ExchangeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentEchangeMainBinding binding;

    ListView mListView;
    // ArrayList prenoms = new ArrayList {"Pixel 4", "SlowChatBox Lille", "Pixel 4", "SlowChatBox Hellemmes"};
    List<String> possibleNames = new ArrayList<>(List.of("Bluetooth A", "Bluetooth B", "Bluetooth C", "Bluetooth D", "Bluetooth E", "Bluetooth F"));


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        binding = FragmentEchangeMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mListView = (ListView) root.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, possibleNames);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
}