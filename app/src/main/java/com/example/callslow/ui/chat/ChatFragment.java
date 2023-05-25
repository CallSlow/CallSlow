package com.example.callslow.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentChatBinding;
import com.example.callslow.objects.ChatAdapter;
import com.example.callslow.objects.Contact;
import com.example.callslow.objects.ContactAdapter;
import com.example.callslow.objects.Contacts;
import com.example.callslow.ui.conversation.ConversationFragment;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentChatBinding binding;
    private String[] array;
    private ListView listView;
    private ArrayList<Contact> ChatList;
    private ChatAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = root.findViewById(R.id.list_chat);
        Contacts.getInstance().init(getContext());
        ChatList = Contacts.getInstance().getContacts();
        adapter = new ChatAdapter(getActivity(), ChatList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = new Bundle();
        bundle.putString("name",ChatList.get(i).getName());
        bundle.putString("mac_adress",ChatList.get(i).getMac());

        Fragment conversationFragment = new ConversationFragment();
        conversationFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.list_fragment, conversationFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}