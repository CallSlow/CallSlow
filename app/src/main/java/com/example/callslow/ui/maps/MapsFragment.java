package com.example.callslow.ui.maps;

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
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;

import com.example.callslow.BuildConfig;
import com.example.callslow.R;

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

import java.util.ArrayList;

public class MapsFragment extends Fragment implements OnClickListener {

    //Déclaration de mes variables
    private MapView map;
    private PopupWindow popupWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Création d'une vue liée à fragment_maps
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

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

        //Création d'un premier point
        OverlayItem home = new OverlayItem("IG2I", "2I", new GeoPoint(50.43520447493158, 2.823631946017646));
        Drawable m = home.getMarker(0);

        //Ajout du point dans la liste
        items.add(home);

        //Création et ajout d'un autre point dans la liste
        items.add(new OverlayItem("Appartement", "Mon petit chez moi", new GeoPoint(51.02844401448275, 2.375003122033972)));

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

    /**
     *
     */
    private void createPopup() {
        // Créer une vue qui contient le formulaire
        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.maps_popup, null);

        // Créer la PopupWindow
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Fond de la popup blanc
        int color = Color.parseColor("#FFFFFFFF");
        ColorDrawable background = new ColorDrawable(color);

        //niveau d'opacité (255 = totalement opaque)
        background.setAlpha(240);
        popupWindow.setBackgroundDrawable(background);
        popupWindow.setFocusable(true);
    }

    /**
     *
     */
    public void showPopup() {
        // Créer la PopupWindow si elle n'existe pas encore
        if (popupWindow == null) {
            createPopup();
        }
        // Afficher la PopupWindow
        popupWindow.showAtLocation(map, Gravity.CENTER, 0, 0);
    }

    @Override
    //Lors du clic sur le bouton +, affichage de la popup
    public void onClick(View v) {showPopup();}

}
