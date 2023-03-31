package com.example.callslow.ui.contact;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentContactBinding;
import com.example.callslow.objects.Contact;
import com.example.callslow.objects.ContactAdapter;

import java.util.ArrayList;

public class ContactFragment extends Fragment implements SearchView.OnQueryTextListener{

    private ListView mListView;
    private SearchView mSearchView;
    private ContactAdapter mAdapter;
    private ArrayList<Contact> mContactList;


    private FragmentContactBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactViewModel contactViewModel =
                new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mListView = root.findViewById(R.id.contact_list);
        mSearchView = root.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(this);


        mContactList = new ArrayList<>();
        mContactList.add(new Contact("John Doe", "f8:2e:dd:df:0e:40"));
        mContactList.add(new Contact("Jane Smith", "58:5a:b6:1c:e2:29"));
        mContactList.add(new Contact("Virgo", "24:60:87:f6:0a:ae"));
        mContactList.add(new Contact("Maya", "ad:52:bf:69:7a:9f"));
        mContactList.add(new Contact("Daisy", "1b:63:09:7a:66:15"));
        mContactList.add(new Contact("Athéna", "66:bf:8d:dc:25:61"));
        mContactList.add(new Contact("Cookie", "21:cc:66:fb:ee:9f"));
        mContactList.add(new Contact("Poilu", "88:05:f3:23:e8:b3"));
        mContactList.add(new Contact("LéoGarçon", "19:2f:c9:bc:8b:0e"));

        mAdapter = new ContactAdapter(getActivity(), mContactList);
        mListView.setAdapter(mAdapter);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        mAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mAdapter.getFilter().filter(s);
        return false;
    }
}