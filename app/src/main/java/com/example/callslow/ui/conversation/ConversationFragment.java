package com.example.callslow.ui.conversation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentConversationBinding;
import com.example.callslow.objects.Contact;
import com.example.callslow.objects.Contacts;
import com.example.callslow.objects.Message;
import com.example.callslow.objects.MessageAdapter;
import com.example.callslow.objects.Messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ConversationFragment extends Fragment implements View.OnClickListener {

    private FragmentConversationBinding binding;
    private ListView mListView;
    private MessageAdapter mAdapter;
    private ArrayList<Message> mMessageList;
    private String name;
    private String mac_adress;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConversationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mListView = root.findViewById(R.id.conversation_list);
        Messages messages = Messages.getInstance();

        // Lecture des messages existants
        Bundle arguments = getArguments();
        if (arguments != null) {
            name = arguments.getString("name");
            mac_adress = arguments.getString("mac_adress");
        }
        messages.readFile();
        mMessageList = new ArrayList<Message>();
        for (Message msg: messages.getMessages()) {
            if (msg.getSenderMac().equals("AA:AA:AA:AA:AA:AA") || msg.getReceiverMac().equals("AA:AA:AA:AA:AA:AA")) {// TODO : Récupérer la MAC locale depuis les settings
                if (msg.getSenderMac().equals(mac_adress) || msg.getReceiverMac().equals(mac_adress)) {
                    mMessageList.add(msg);
                }
            }
        }

        // Ajout des messages sur la view
        mAdapter = new MessageAdapter(getActivity(), mMessageList);
        mListView.setAdapter(mAdapter);

        // Bouton Envoyer
        Button sendMessageButton = root.findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(this);

        return root;
    }

    public void createMessage(Message msg) {
        try {
            Messages.getInstance().addMessage(msg);
            Messages.getInstance().writeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
    }

    @Override
    public void onClick(View v) {
        Contact me = new Contact("Mathieu Maes", "AA:AA:AA:AA:AA:AA");// TODO : Récupérer la MAC locale depuis les settings

        Date date = new Date();

        // Initialisation du message à envoyer
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        EditText editMessage = binding.getRoot().findViewById(R.id.editMessage);
        Message newMessage = new Message(UUID.randomUUID(), editMessage.getText().toString(), me.getMac(), mac_adress, formattedDate);

        // Écriture du message dans le fichier
        try {
            this.createMessage(newMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ajout du message sur la view
        mAdapter.add(newMessage);
        mAdapter.notifyDataSetChanged();

        // Suppression du message écrit de la zone de texte
        editMessage.setText(null);

    }
}
