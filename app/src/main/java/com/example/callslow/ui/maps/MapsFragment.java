package com.example.callslow.ui.maps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

public class MapsFragment extends Fragment implements OnClickListener {

    //Déclaration de mes variables
    private MapView map;
    private PopupWindow popupWindow;
    private String popupType = "";
    private View popupViewForm;
    private View popupViewTuto;
    private final String MAP_FILE = "map.json";
    private Context context;
    private ArrayList<PointMap> points_map;
    private final ArrayList<OverlayItem> items = new ArrayList<>();

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
        map.setMultiTouchControls(true);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        //Définition du point de départ lors du lancement de onCreate
        GeoPoint startPoint = new GeoPoint(50.43520447493158, 2.823631946017646); //Point de départ

        //Personnalisation de la carte
        IMapController mapController = map.getController();

        //Zoom par défaut de la carte
        mapController.setZoom(18.0);

        //On applique le point de départ défini au dessus comme le point au centre de la carte
        mapController.setCenter(startPoint);

        init();
        for(PointMap p : points_map){
            items.add(new OverlayItem(p.getUuid(),p.getName(),p.getDescription(),p.toGeopoint()));
        }

        reloadMap(items);

        //Référence aux boutons sur la vue
        Button boutonFormulaire = (Button) view.findViewById(R.id.button_form_BAL);
        Button boutonTuto = (Button) view.findViewById(R.id.button_tuto_BAL);

        //Listener sur les boutons
        boutonFormulaire.setOnClickListener(this);
        boutonTuto.setOnClickListener(this);

        final IGeoPoint centre = map.getMapCenter();
        System.out.println("Latitude : " + centre.getLatitude() + " - Longitude : "+ centre.getLongitude());

        return view;
    }
    /**
     * Recharge la carte avec une liste d'éléments à afficher.
     * @param items La liste des éléments à afficher sur la carte.
     */
    private void reloadMap(ArrayList<OverlayItem> items){
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(requireContext().getApplicationContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {return true;}

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                map.getOverlays().clear();
                String markerId = item.getUid();
                removeMarkerFromJSON(markerId);
                return true;
            }
        });

        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);
    }

    /**
     * Crée la fenêtre popup pour l'ajout d'un nouveau point.
     */
    private void createPopup(String type) {
        // Fond de la popup blanc
        int color = Color.parseColor("#FFFFFFFF");
        ColorDrawable background = new ColorDrawable(color);
        // Niveau d'opacité (255 = totalement opaque)
        background.setAlpha(240);

        switch (type) {
            case "formulaire":
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                // Créer la vue de la popup pour le formulaire
                popupViewForm = LayoutInflater.from(requireContext()).inflate(R.layout.maps_popup, null);
                // Référence au bouton d'envoi de formulaire
                Button sendPointForm = popupViewForm.findViewById(R.id.button_submit);
                sendPointForm.setOnClickListener(this);
                // Créer la PopupWindow
                popupWindow = new PopupWindow(popupViewForm, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Définir le contenu de la PopupWindow
                popupWindow.setContentView(popupViewForm);
                break;

            case "tutoriel":
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                // Créer la vue de la popup pour le tutoriel
                popupViewTuto = LayoutInflater.from(requireContext()).inflate(R.layout.tuto_maps_popup, null);
                // Créer la PopupWindow
                popupWindow = new PopupWindow(popupViewTuto, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // Définir le contenu de la PopupWindow
                popupWindow.setContentView(popupViewTuto);
                break;
        }

        // Ajouter le OnDismissListener
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow = null; // Réinitialiser la variable popupWindow
            }
        });

        popupWindow.setBackgroundDrawable(background);
        popupWindow.setFocusable(true);
    }

    /**
     * Affiche la fenêtre popup pour l'ajout d'un nouveau point.
     */
// Déclarez la variable popupType dans la classe contenant showPopup()


    public void showPopup(String type) {
        System.out.println("Je passe dans le showpopup");

        // Vérifiez si la popup actuelle correspond au type demandé
        if (popupWindow != null && popupType.equals(type) && popupWindow.isShowing()) {
            // La popup actuelle est déjà affichée, pas besoin de faire quoi que ce soit
            return;
        }

        // Créez une nouvelle popup si nécessaire
        createPopup(type);

        // Affichez la PopupWindow
        popupWindow.showAtLocation(map, Gravity.CENTER, 0, 0);

        // Mettez à jour le type de la popup actuelle
        popupType = type;

        if (type.equals("formulaire")) {
            GeoPoint centerPoint = (GeoPoint) map.getMapCenter();
            EditText latitudeEditText = popupViewForm.findViewById(R.id.editText_latitude);
            EditText longitudeEditText = popupViewForm.findViewById(R.id.editText_longitude);

            // Remplissez les champs avec les valeurs du GeoPoint center
            latitudeEditText.setText(String.valueOf(centerPoint.getLatitude()));
            longitudeEditText.setText(String.valueOf(centerPoint.getLongitude()));
        }
    }


    // Méthode pour supprimer un marker du fichier JSON
// Méthode pour supprimer un marker du fichier JSON
    private void removeMarkerFromJSON(String markerUUID) {
        try {
            for (int i = 0; i < points_map.size(); i++) {
                PointMap point = points_map.get(i);
                if (point.getUuid().equals(markerUUID)) {
                    points_map.remove(i);
                    items.remove(i); // Supprimer l'overlay correspondant à ce point
                    // Mettre à jour la carte avec la nouvelle liste d'éléments
                    reloadMap(items);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        // Mettre à jour le fichier JSON avec les modifications
        try {
            writeFile(); // Méthode pour écrire le contenu mis à jour dans le fichier JSON
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Cette méthode initialise le système en chargeant les points à partir d'un fichier JSON.
     * Les points sont stockés dans une liste points_map.
     * Si une exception se produit lors de l'importation d'un point, un message d'erreur est affiché.
     */
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

    /**
     * Cette méthode envoie un nouveau point à ajouter.
     * Elle récupère les informations du point à partir des champs de texte de la popup.
     * Le nouveau point est ensuite ajouté à la liste points_map.
     * Un nouvel objet OverlayItem est créé avec les informations du point et ajouté à la liste items.
     * La carte est rechargée avec les nouveaux éléments.
     * Enfin, les données sont écrites dans le fichier json.
     */
    public void sendPoint() {
        EditText _name = popupViewForm.findViewById(R.id.editText_name);
        String name = _name.getText().toString();

        EditText _description = popupViewForm.findViewById(R.id.editText_description);
        String description = _description.getText().toString();

        EditText _latitude = popupViewForm.findViewById(R.id.editText_latitude);
        String latitude = _latitude.getText().toString();

        EditText _longitude = popupViewForm.findViewById(R.id.editText_longitude);
        String longitude = _longitude.getText().toString();

        String uuid = UUID.randomUUID().toString(); // Génère un UUID unique

        PointMap newPoint = new PointMap(uuid,name,description,latitude,longitude);
        points_map.add(newPoint);
        items.add(new OverlayItem(newPoint.getUuid(),newPoint.getName(),newPoint.getDescription(),newPoint.toGeopoint()));
        reloadMap(items);
        try {
            writeFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //On vide le name et la description du formulaire popup (la latitude et la longitude seront automatiquement remplacés lors d'un nouveau clic sur le bouton)
        _name.setText("");
        _description.setText("");
        popupWindow.dismiss();
    }
    /**
     * Cette méthode lit le contenu du fichier de données JSON et le retourne sous forme de chaîne de caractères.
     * Si le fichier n'existe pas, un nouveau fichier est créé avec un objet JSON vide.
     * Si une exception se produit lors de la lecture ou de la création du fichier, elle est affichée.
     * @return Le contenu du fichier JSON.
     */

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

    /**
     * Cette méthode permet d'écrire les données des points dans un fichier JSON.
     * Les points sont convertis en objets JSON et stockés dans un tableau JSON.
     * Ensuite, un objet JSON contenant ce tableau est créé.
     * Les données JSON sont ensuite écrites dans un fichier avec le nom spécifié.
     */
    public void writeFile() throws Exception {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    //Lors du clic sur le bouton +, affichage de la popup
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_form_BAL:
                showPopup("formulaire");
                break;
            case R.id.button_submit:
                sendPoint();
                break;
            case R.id.button_tuto_BAL:
                showPopup("tutoriel");
                break;
        }
    }
}
