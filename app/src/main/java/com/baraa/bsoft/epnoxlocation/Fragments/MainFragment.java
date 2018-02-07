package com.baraa.bsoft.epnoxlocation.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.baraa.bsoft.epnoxlocation.Model.LocationModel;
import com.baraa.bsoft.epnoxlocation.R;
import com.baraa.bsoft.epnoxlocation.Services.AppLocationsServices;
import com.baraa.bsoft.epnoxlocation.Services.Download;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainFragment extends Fragment implements OnMapReadyCallback,ListSuggestionFragment.ItemListListener {
    private static final String TAG = "MainFragment";
    private GoogleMap mMap;
    private MarkerOptions markerOptions;

    private MarkerOptions markerOptionsFrom;
    private Marker mMarker;

    AppLocationsServices appLocationsServices;
    ListSuggestionFragment suggestionFragment;
    CardView cardViewList;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Goe API>>
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        cardViewList = (CardView)view.findViewById(R.id.cardviewList);
        cardViewList.setVisibility(View.GONE);
        // search section >>
        final EditText edLocate = (EditText)view.findViewById(R.id.edSearch);
        edLocate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN )&& keyEvent.KEYCODE_ENTER == i ){
                    String searchTxt = edLocate.getText().toString();
                    startSearchLocation(searchTxt);
                    Log.d(TAG, "onKey: search text: "+searchTxt);
                    InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(edLocate.getWindowToken(),0);
                    showSuggestionList();
                }
                return false;
            }

        });


        // Calling RecyclerView Fragments for Displaying lists >>
        suggestionFragment = (ListSuggestionFragment)getChildFragmentManager()
                .findFragmentById(R.id.flListContainer);
        if(suggestionFragment ==null){
            suggestionFragment = ListSuggestionFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.flListContainer,suggestionFragment).commit();
            suggestionFragment.setGoToNextTabListener((ListSuggestionFragment.GoToNextTabListener)getActivity());
            suggestionFragment.setItemListListener(this);
            suggestionFragment.setCurrentTap(0);
        }

        // Parsing Json >>
        appLocationsServices = new AppLocationsServices();
        appLocationsServices.setAppLocationsServicesListener(suggestionFragment);

        return view;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    public void setUserMarker(LatLng latLng){
        if(markerOptions == null){
            markerOptions = new MarkerOptions().position(latLng).title("Your current location");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_accessibility_black_36dp));
            mMap.addMarker(markerOptions);
            //Log.d(TAG, "setUserMarker: latitude: "+latLng.latitude+" longitude: "+latLng.longitude);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    public void setMarkerOnSelectedPlace(LocationModel selLocationInfo){
        LatLng latLng = new LatLng(selLocationInfo.getLatitude(),selLocationInfo.getLongitude());
        markerOptionsFrom = new MarkerOptions();
        markerOptionsFrom.position(latLng).title("From location").snippet(selLocationInfo.getAddress());
        markerOptionsFrom.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_36dp));

        if(mMarker != null){
            mMarker.remove();
            mMarker = mMap.addMarker(markerOptionsFrom);
        }else {
            mMarker = mMap.addMarker(markerOptionsFrom);

        }
        Log.d(TAG, "onListItemSelected: "+latLng.latitude+" longitude: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }



    private void startSearchLocation(String searchTxt){
        String text = getString(R.string.search_places_url,searchTxt);
        appLocationsServices.searchNearbyLocationFromAddress(text,new Download());

    }

    public void hideSuggestionList() {
        cardViewList.setVisibility(View.GONE);
        getActivity().getSupportFragmentManager().beginTransaction().hide(suggestionFragment).commit();
    }

    public void showSuggestionList() {
        getActivity().getSupportFragmentManager().beginTransaction().show(suggestionFragment).commit();
        cardViewList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemSelected(LocationModel selLocationInfo) {
        setMarkerOnSelectedPlace(selLocationInfo);
    }
}

