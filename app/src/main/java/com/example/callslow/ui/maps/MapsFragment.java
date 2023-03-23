package com.example.callslow.ui.maps;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.callslow.BuildConfig;
import com.example.callslow.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MapsFragment extends Fragment {
    private MapView map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        //Gestion de la carte
        map = view.findViewById(R.id.id_map);
        map.setTileSource(TileSourceFactory.MAPNIK);//render
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        GeoPoint startPoint = new GeoPoint(50.43520447493158, 2.823631946017646); //Point de départ
        IMapController mapController = map.getController();
        mapController.setZoom(18.0); //Zoom par défaut de la carte
        mapController.setCenter(startPoint);

        //Création d'une liste de points
        ArrayList<OverlayItem> items = new ArrayList<>();
        OverlayItem home = new OverlayItem("IG2I", "2I", new GeoPoint(50.43520447493158, 2.823631946017646));
        Drawable m = home.getMarker(0);
        items.add(home);
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
        return view;
    }






}