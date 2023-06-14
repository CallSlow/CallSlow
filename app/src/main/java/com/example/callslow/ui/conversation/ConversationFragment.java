package com.example.callslow.ui.conversation;

import android.annotation.SuppressLint;
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
import com.example.callslow.objects.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ConversationFragment extends Fragment implements View.OnClickListener {

    private FragmentConversationBinding binding;
    private ListView mListView;
    private MessageAdapter mAdapter;
    private ArrayList<Message> mMessageList;
    private ArrayList<String> settingslist;
    private String name;
    private String mac_adress;
    private View view;

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

        Settings.getInstance().init(getContext());
        settingslist = Settings.getInstance().getSettings();
        settingslist = Settings.getInstance().getSettings();
        for (Message msg: messages.getMessages()) {
            if (msg.getSenderMac().equals(settingslist.get(0)) || msg.getReceiverMac().equals(settingslist.get(0))) {// TODO : Récupérer la MAC locale depuis les settings
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

        view = root;

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
        Contact me = new Contact(Settings.getInstance().getSettings().get(1), Settings.getInstance().getSettings().get(0));
        Date date = new Date();

        // Initialisation du message à envoyer
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        EditText editMessage = binding.getRoot().findViewById(R.id.editMessage);
        Message newMessage = new Message(UUID.randomUUID(), editMessage.getText().toString(), me.getMac(), mac_adress, formattedDate);

        // Écriture du message dans le fichier
        try {
            if (editMessage.getText().toString().equals("")) {
                alertBox("Message invalide", "Veuillez saisir un message.");
                return;
            } else {
                this.createMessage(newMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ajout du message sur la view
        mAdapter.add(newMessage);
        mAdapter.notifyDataSetChanged();

        // Suppression du message écrit de la zone de texte
        editMessage.setText(null);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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
