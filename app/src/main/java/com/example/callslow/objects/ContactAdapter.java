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

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<Contact> mContactList;
    private List<Contact> mFilteredContactList;

    public ContactAdapter(Context context, List<Contact> contactList) {
        mContext = context;
        mContactList = contactList;
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
            view = inflater.inflate(R.layout.item_contact, parent, false);
        }

        ImageView photoView = view.findViewById(R.id.contact_photo);
        TextView nameView = view.findViewById(R.id.contact_name);
        TextView phoneView = view.findViewById(R.id.contact_phone);

        Contact contact = mFilteredContactList.get(position);

        String uri = "@drawable/placeholder_contact";
        photoView.setImageResource(R.drawable.placeholder_contact);
        //photoView.setImageResource(contact.getPhoto()); // ou utiliser une bibliothèque de chargement d'image
        nameView.setText(contact.getName());
        phoneView.setText(contact.getMac());

        // Ajouter un écouteur de clic long sur l'élément de la liste
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Afficher le menu contextuel flottant ici
                PopupMenu popupMenu = new PopupMenu(mContext, v, Gravity.CENTER);
                popupMenu.getMenuInflater().inflate(R.menu.contacts_ctx_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit:
                                // Code à exécuter lorsque l'utilisateur sélectionne l'option 1
                                return true;
                            case R.id.action_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("Confirmation");
                                builder.setMessage("Êtes-vous sûr de vouloir supprimer cet élément ?");

                                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                });

                                // Ajouter un bouton "OK" qui ferme la boîte de dialogue et effectue l'action de confirmation
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
                return true;
            }
        });

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
                    filteredList.addAll(mContactList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Contact contact : mContactList) {
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
}