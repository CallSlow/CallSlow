package com.example.callslow.ui.conversation;

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
import com.example.callslow.objects.Message;
import com.example.callslow.objects.MessageAdapter;
import com.example.callslow.objects.Messages;

import java.util.ArrayList;
import java.util.Date;

public class ConversationFragment extends Fragment implements View.OnClickListener {

    private FragmentConversationBinding binding;
    private ListView mListView;
    private MessageAdapter mAdapter;
    private ArrayList<Message> mMessageList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConversationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mListView = root.findViewById(R.id.conversation_list);
        Messages messages = Messages.getInstance();

        // Lecture des messages existants
        messages.readFile();
        mMessageList = messages.getMessages();

        // Ajout des messages sur la view
        mAdapter = new MessageAdapter(getActivity(), mMessageList);
        mListView.setAdapter(mAdapter);

        // Bouton Envoyer
        Button sendMessageButton = root.findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(this);

        return root;
    }

    //TODO : Récupérer le destinataire depuis la conversation en cours
    @Override
    public void onClick(View v) {
        Contact me = new Contact("Mathieu Maes", "555-1234");
        Contact receiver = new Contact("Basile Chevalier", "555-1233");

        // Initialisation du message à envoyer
        EditText editMessage = binding.getRoot().findViewById(R.id.editMessage);
        Messages messages = Messages.getInstance();
        Message newMessage = new Message(editMessage.getText().toString(), me.getMac(), receiver.getMac(), new Date());

        // Ajout du message dans le fichier
        ArrayList<Message> messageList = messages.getMessages();
        messageList.add(newMessage);
        messages.setMessages(messageList);

        // Écriture du message dans le fichier
        try {
            messages.writeFile();
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
