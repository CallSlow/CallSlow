package com.example.callslow.ui.contact;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.R;
import com.example.callslow.databinding.FragmentContactBinding;
import com.example.callslow.objects.Contact;
import com.example.callslow.objects.ContactAdapter;
import com.example.callslow.objects.Contacts;
import com.example.callslow.objects.Messages;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment implements SearchView.OnQueryTextListener , View.OnClickListener {

    private SearchView mSearchView;
    private ContactAdapter mAdapter;
    private ArrayList<Contact> mContactList;
    private ArrayList<Messages> mMessagesList;

    private Button addContactBtn, createContactBtn;
    private EditText nameInput, macInput;

    private PopupWindow popupWindow;
    private View popupView;

    private View view;

    private Contact editing = null;


    private FragmentContactBinding binding;

    @SuppressLint("StaticFieldLeak")
    private static ContactFragment instance;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        instance = this;

        ContactViewModel contactViewModel =
                new ViewModelProvider(this).get(ContactViewModel.class);

        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView mListView = root.findViewById(R.id.contact_list);
        mSearchView = root.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(this);

        Contacts.getInstance().init(getContext());
        mContactList = Contacts.getInstance().getContacts();

        Messages.getInstance().init(getContext());
        //mContactList = new ArrayList<>();


        mAdapter = new ContactAdapter(getActivity(), mContactList, this);;
        mListView.setAdapter(mAdapter);

        addContactBtn = (Button) root.findViewById(R.id.addBtn);
        addContactBtn.setOnClickListener(this);

        view = root;

        return root;
    }

    public void refresh() {
        /*mAdapter = new ContactAdapter(getActivity(), mContactList);
        mListView.setAdapter(mAdapter);*/
        mAdapter.updateContactList(Contacts.getInstance().getContacts());
    }

    public static ContactFragment getInstance() {
        return instance;
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

    public void createContact() {
        String answer = macInput.getText().toString();

        if (!answer.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
            alertBox("Adrese MAC Invalide", "Le format de l'adresse MAC saisie est invalide.");
            return;
        }

        Contact newContact = new Contact(nameInput.getText().toString(), answer);
        try {
            if (editing == null) {
                if (!Contacts.getInstance().addContact(newContact)) {
                    alertBox("Contact Existant", "Le contact que vous essayez d'entrer existe déjà !");
                    return;
                }
            } else {
                Contacts.getInstance().replaceContact(editing, newContact);
                Messages.getInstance().setMessagesContact(editing.getMac(), newContact.getMac());
                editing = null;
            }

            popupWindow.dismiss();
            refresh();
        } catch (Exception e) {
            alertBox("Une erreur est survenue", e.getMessage());
        }
    }


    private void createAddPopup() {
        // Créer une vue qui contient le formulaire
        popupView = LayoutInflater.from(requireContext()).inflate(R.layout.contacts_popup, null);

        // Créer la PopupWindow
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Fond de la popup blanc
        int color = Color.parseColor("#FFFFFFFF");
        ColorDrawable background = new ColorDrawable(color);

        TextView popTitle = (TextView)  popupView.findViewById(R.id.popup_title);
        popTitle.setText("\uD83E\uDDD1 Ajout d'un nouveau contact");

        createContactBtn = (Button) popupView.findViewById(R.id.button_submitCt);
        createContactBtn.setOnClickListener(this);

        nameInput = (EditText) popupView.findViewById(R.id.editText_ContactName);
        macInput = (EditText) popupView.findViewById(R.id.editText_mac);


        //niveau d'opacité (255 = totalement opaque)
        background.setAlpha(240);
        popupWindow.setBackgroundDrawable(background);
        popupWindow.setFocusable(true);
    }

    public void showPopup() {

        // Créer la PopupWindow si elle n'existe pas encore
        if (popupWindow == null) {
            createAddPopup();
        }
        TextView popTitle = (TextView)  popupWindow.getContentView().findViewById(R.id.popup_title);
        popTitle.setText("\uD83E\uDDD1 Ajout d'un nouveau contact");

        nameInput.setText("");
        macInput.setText("");

        editing = null;
        // Afficher la PopupWindow
        popupWindow.showAtLocation(mSearchView, Gravity.CENTER, 0, 0);
    }

    public void showEditPopup(String mac) {
        if (popupWindow == null) {
            createAddPopup();
        }


        Contact contact = null;
        for (Contact c : Contacts.getInstance().getContacts()) {
            if (c.getMac().equals(mac)) {
                contact = c;
                break;
            }
        }

        if (contact == null) {
            alertBox("Contact Indisponible", "Le contact que vous cherchez n'existe pas");
            return;
        }

        TextView popTitle = (TextView)  popupView.findViewById(R.id.popup_title);
        popTitle.setText("\uD83E\uDDD1 Modification d'un contact");

        nameInput.setText(contact.getName());
        macInput.setText(contact.getMac());

        editing = contact;

        popupWindow.showAtLocation(mSearchView, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addBtn:
                showPopup();
                break;
            case R.id.button_submitCt:
                createContact();
                break;

            default:
                break;
        }
    }
}