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
        mContactList.add(new Contact("John Doe", "555-1234"));
        mContactList.add(new Contact("Jane Smith", "555-5678"));
        mContactList.add(new Contact("Virgo", "555-59965"));
        mContactList.add(new Contact("Maya", "123-45678"));
        mContactList.add(new Contact("Daisy", "123-45678"));
        mContactList.add(new Contact("Athéna", "123-45678"));
        mContactList.add(new Contact("Cookie", "123-45678"));
        mContactList.add(new Contact("Poilu", "123-45678"));
        mContactList.add(new Contact("LéoGarçon", "123-45678"));

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