package com.example.callslow.objects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.callslow.R;
import com.example.callslow.ui.contact.ContactFragment;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<Contact> mChatList;
    private List<Contact> mFilteredContactList;

    public ChatAdapter(Context context, List<Contact> contactList) {
        mContext = context;
        mChatList = contactList;
        mFilteredContactList = contactList;
    }

    @Override
    public int getCount() {
        return mFilteredContactList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilteredContactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_chat, parent, false);
        }

        ImageView photoView = view.findViewById(R.id.contact_chat_photo);
        TextView nameView = view.findViewById(R.id.contact_chat_name);

        Contact contact = mFilteredContactList.get(position);

        String uri = "@drawable/placeholder_contact";
        photoView.setImageResource(R.drawable.placeholder_contact);
        //photoView.setImageResource(contact.getPhoto()); // ou utiliser une bibliothèque de chargement d'image
        nameView.setText(contact.getName());

        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Contact> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mChatList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Contact contact : mChatList) {
                        if (contact.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(contact);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredContactList = (List<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateContactList(List<Contact> contactList) {
        mChatList = contactList;
        mFilteredContactList = contactList;
        notifyDataSetChanged();
    }
}