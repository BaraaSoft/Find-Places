package com.baraa.bsoft.epnoxlocation.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

public class ToPlaceFragment extends Fragment implements OnMapReadyCallback,ListSuggestionFragment.ItemListListener {
    private static final String TAG = "ToPlaceFragment";
    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private MarkerOptions markerOptionsTo;
    private Marker mMarker;

    AppLocationsServices appLocationsServices;
    ListSuggestionFragment suggestionFragment;
    CardView cardViewList;
    public ToPlaceFragment() {
        // Required empty public constructor
    }

    public static ToPlaceFragment newInstance() {
        ToPlaceFragment fragment = new ToPlaceFragment();

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
        View view = inflater.inflate(R.layout.fragment_to_place, container, false);
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
            suggestionFragment.setItemListListener(this);
            suggestionFragment.setCurrentTap(1);
        }

        // Parsing Json >>
        appLocationsServices = new AppLocationsServices();
        appLocationsServices.setAppLocationsServicesListener(suggestionFragment);

        // Locate >>
        Button btnLocate = (Button)view.findViewById(R.id.btnLocate);
        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                String from = sharedPref.getString(getString(R.string.main_fragment_title),"none");
                String to = sharedPref.getString(getString(R.string.secondary_fragment_title),"none");

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/maps/dir/"+from+"/"+to));
                startActivity(intent);

                //Snackbar.make(view,"Invalid Locations!",Snackbar.LENGTH_LONG).show();

                Log.d(TAG, "onClick: Final Selected Location: From >>"+from+" To >>"+to);
            }
        });

        return view;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    public void setUserMarker(LatLng latLng){
        if(markerOptions == null){
            markerOptions = new MarkerOptions().position(latLng).title("Your current location");
            mMap.addMarker(markerOptions);
            Log.d(TAG, "setUserMarker: latitude: "+latLng.latitude+" longitude: "+latLng.longitude);
        }
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


    public void setMarkerOnSelectedPlace(LocationModel selLocationInfo){
        LatLng latLng = new LatLng(selLocationInfo.getLatitude(),selLocationInfo.getLongitude());
        markerOptionsTo = new MarkerOptions();
        markerOptionsTo.position(latLng).title("Destination location").snippet(selLocationInfo.getAddress());
        markerOptionsTo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_add_location_black_36dp));

        if(mMarker != null){
            mMarker.remove();
            mMarker = mMap.addMarker(markerOptionsTo);
        }else {
            mMarker = mMap.addMarker(markerOptionsTo);

        }
        Log.d(TAG, "onListItemSelected: "+latLng.latitude+" longitude: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    @Override
    public void onListItemSelected(LocationModel selLocationInfo) {
        setMarkerOnSelectedPlace(selLocationInfo);
    }
}
