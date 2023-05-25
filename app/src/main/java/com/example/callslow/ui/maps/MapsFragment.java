package com.example.callslow.ui.maps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.callslow.BuildConfig;
import com.example.callslow.R;
import com.example.callslow.objects.PointMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnClickListener {

    //Déclaration de mes variables
    private MapView map;
    private PopupWindow popupWindow;
    private View popupView;
    private final String MAP_FILE = "map.json";
    private Context context;
    private ArrayList<PointMap> points_map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Création d'une vue liée à fragment_maps
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        context = getContext();

        //Récupération de l'id de la carte dans fragment_maps
        map = view.findViewById(R.id.id_map);

        //Choix du style de carte (ici on utilise un rendu MAPNIK)
        map.setTileSource(TileSourceFactory.MAPNIK);

        //Affichage des boutons de zoom et dézoom
        map.setBuiltInZoomControls(true);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        //Définition du point de départ lors du lancement de onCreate
        GeoPoint startPoint = new GeoPoint(50.43520447493158, 2.823631946017646); //Point de départ

        //Personnalisation de la carte
        IMapController mapController = map.getController();

        //Zoom par défaut de la carte
        mapController.setZoom(18.0);

        //On applique le point de départ défini au dessus comme le point au centre de la carte
        mapController.setCenter(startPoint);

        //Création d'une liste de points
        ArrayList<OverlayItem> items = new ArrayList<>();


        init();
        for(PointMap p : points_map){
            items.add(new OverlayItem(p.getName(),p.getDescription(),p.toGeopoint()));
        }

        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(requireContext().getApplicationContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return true;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });

        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);

        //Référence au bouton + sur la vue
        Button myButton = (Button) view.findViewById(R.id.button_form_BAL);

        //Listener sur le bouton : lancement de showPopup() lors du clic sur le bouton
        myButton.setOnClickListener(this);

        final IGeoPoint centre = map.getMapCenter();
        System.out.println("Latitude : " + centre.getLatitude() + " - Longitude : "+ centre.getLongitude());
        //retourne 0 et 0


        return view;
    }

    private void createPopup() {
        // Créer une vue qui contient le formulaire
        popupView = LayoutInflater.from(requireContext()).inflate(R.layout.maps_popup, null);

        // Créer la PopupWindow
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button sendPoint = (Button) popupView.findViewById(R.id.button_submit);
        sendPoint.setOnClickListener(this);

        //Fond de la popup blanc
        int color = Color.parseColor("#FFFFFFFF");
        ColorDrawable background = new ColorDrawable(color);

        //niveau d'opacité (255 = totalement opaque)
        background.setAlpha(240);
        popupWindow.setBackgroundDrawable(background);
        popupWindow.setFocusable(true);
    }

    public void showPopup() {
        // Créer la PopupWindow si elle n'existe pas encore
        if (popupWindow == null) {
            createPopup();
        }
        // Afficher la PopupWindow
        popupWindow.showAtLocation(map, Gravity.CENTER, 0, 0);
    }

    public void init() {
        points_map = new ArrayList<>();
        String json = readFile();

        try {
            JSONObject pointsJSON = new JSONObject(json);

            try {
                JSONArray array = pointsJSON.getJSONArray("point");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject pm = array.getJSONObject(i);
                    try {
                        points_map.add(new PointMap(pm));
                    } catch (Exception e) {
                        System.err.println("Impossible d'importer le point : " + e.getMessage());
                    }
                }

            } catch (Exception e) {}

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPoint() {
        EditText _name = popupView.findViewById(R.id.editText_name);
        String name = _name.getText().toString();

        EditText _description = popupView.findViewById(R.id.editText_description);
        String description = _description.getText().toString();

        EditText _latitude = popupView.findViewById(R.id.editText_latitude);
        String latitude = _latitude.getText().toString();

        EditText _longitude = popupView.findViewById(R.id.editText_longitude);
        String longitude = _longitude.getText().toString();

        PointMap newPoint = new PointMap(name,description,latitude,longitude);
        points_map.add(newPoint);
        try {
            writeFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String readFile() {
        String json = "";

        try {
            FileInputStream fileInputStream = context.openFileInput(MAP_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
            json = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(MAP_FILE, Context.MODE_PRIVATE);
                JSONObject jsonObject = new JSONObject();
                String jsonString = jsonObject.toString();
                fileOutputStream.write(jsonString.getBytes());
                fileOutputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public void writeFile() throws Exception {
        // préparation de l'array json
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        for (PointMap p : points_map) {
            array.put(p.toJSON());
        }

        obj.put("point",array);
        FileOutputStream fileOutputStream = context.openFileOutput(MAP_FILE, Context.MODE_PRIVATE);
        String jsonString = obj.toString();
        fileOutputStream.write(jsonString.getBytes());
        fileOutputStream.close();
    }

    @Override
    //Lors du clic sur le bouton +, affichage de la popup
    public void onClick(View v) {
        System.out.println("on a cliqué sur un btn");
        switch(v.getId()){
            case R.id.button_form_BAL :
                showPopup();
                break;
            case R.id.button_submit :
                sendPoint();
                break;
        }

    }

}
