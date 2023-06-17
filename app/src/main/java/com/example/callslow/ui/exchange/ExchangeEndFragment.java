package com.example.callslow.ui.exchange;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.callslow.MainActivity;
import com.example.callslow.R;
import com.example.callslow.databinding.FragmentEchangeEndBinding;
import com.example.callslow.databinding.FragmentEchangeSynchroBinding;
import com.example.callslow.objects.Comparaison;
import com.example.callslow.objects.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ExchangeEndFragment extends Fragment {

    private FragmentEchangeEndBinding binding;

    ListView mListView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ExchangeViewModel exchangeViewModel = new ViewModelProvider(this).get(ExchangeViewModel.class);
        binding = FragmentEchangeEndBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Comparaison compare = new Comparaison(getContext());
        String json_list_1 = compare.readFiletoJSONFile("messages.json");
        String json_list_2 = compare.readFiletoJSONFile("messages_exchange.json");

        // Comparaison liste de messages
        try {
            JSONObject message_json1 = new JSONObject(json_list_1);
            JSONObject message_json2 = new JSONObject(json_list_2);

            if (message_json1.has("messages") && message_json2.has("messages")) {
                Log.d("Comparaison Objet JSON", String.valueOf(message_json1));
                Log.d("Comparaison Objet JSON 2", String.valueOf(message_json2));

                JSONArray array_json1 = message_json1.getJSONArray("messages");
                JSONArray array_json2 = message_json2.getJSONArray("messages");

                Log.d("Affichage du premier tableau", array_json1.toString());
                Log.d("Affichage du deuxiÃ¨me tableau", array_json2.toString());

                System.out.println(Settings.getInstance().getSettings().get(0));

                JSONArray finalArray = compare.getNewValues(array_json1, array_json2, new String[]{"uuid"});
                Log.d("Affichage du tableau final", finalArray.toString());
                Log.d("Taille tableau final", String.valueOf(finalArray.length()));
                String param = "messages";
                compare.writeJSONArrayToFile(param, array_json1, finalArray, "messages.json");
            }
            else if (!message_json1.has("messages") && message_json2.has("messages")) {
                String param = "messages";
                JSONArray messagesArray = new JSONArray();
                // Ajouter le tableau "messages" à l'objet JSON
                message_json1.put("messages", messagesArray);
                JSONArray array_json = message_json2.getJSONArray("messages");
                compare.writeJSONArrayToFile(param, messagesArray, array_json, "messages.json");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Comparaison fichier carte

        String json_list_3 = compare.readFiletoJSONFile("map.json");
        String json_list_4 = compare.readFiletoJSONFile("map_exchange.json");

        try {
            JSONObject message_json3 = new JSONObject(json_list_3);
            JSONObject message_json4 = new JSONObject(json_list_4);

            Log.d("Comparaison Objet JSON",String.valueOf(message_json3));
            Log.d("Comparaison Objet JSON 2",String.valueOf(message_json4));

            if(message_json3.has("point") && message_json4.has("point")) {
                JSONArray array_json3 = message_json3.getJSONArray("point");
                JSONArray array_json4 = message_json4.getJSONArray("point");

                Log.d("Affichage du premier tableau", array_json3.toString());
                Log.d("Affichage du deuxiÃ¨me tableau", array_json4.toString());

                JSONArray finalArray = compare.getNewValues(array_json3, array_json4, new String[]{"uuid"});
                Log.d("Affichage du tableau final", finalArray.toString());
                Log.d("Taille tableau final", String.valueOf(finalArray.length()));
                String param = "point";

                compare.writeJSONArrayToFile(param, array_json3, finalArray, "map.json");
            } else if (!message_json3.has("point") && message_json4.has("point")) {
                JSONArray messagesArray = new JSONArray();
                // Ajouter le tableau "messages" à l'objet JSON
                message_json3.put("point", messagesArray);
                JSONArray array_json = message_json4.getJSONArray("point");
                String param = "point";
                compare.writeJSONArrayToFile(param, messagesArray, array_json, "map.json");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button mBtnRetour;
        mBtnRetour = root.findViewById(R.id.buttonEnd);
        mBtnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                BottomNavigationView navView = mainActivity.findViewById(R.id.nav_view);
                navView.setEnabled(false);
                navView.setVisibility(View.VISIBLE);

                Fragment exchangeFragment = new ExchangeFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, exchangeFragment);
                transaction.setReorderingAllowed(true);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}