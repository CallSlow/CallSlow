package com.example.callslow.ui.conversation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentConversationBinding;
import com.example.callslow.objects.Contact;
import com.example.callslow.objects.ContactAdapter;
import com.example.callslow.objects.Message;
import com.example.callslow.objects.MessageAdaptater;

import java.util.ArrayList;
import java.util.Date;

public class ConversationFragment extends Fragment {

    private FragmentConversationBinding binding;
    private ListView mListView;
    private MessageAdaptater mAdapter;
    private ArrayList<Message> mMessageList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConversationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mListView = root.findViewById(R.id.conversation_list);

        Contact receiver = new Contact("Basile Chevalier", "555-1234");
        Contact sender = new Contact("Mathieu Maes", "555-1234");
        Date sendDate = new Date();
        mMessageList = new ArrayList<Message>();
        mMessageList.add(new Message("Message émis", sender.getMac(), receiver.getMac(), sendDate));

        mAdapter = new MessageAdaptater(getActivity(), mMessageList);
        mListView.setAdapter(mAdapter);
        return root;
    }
}
