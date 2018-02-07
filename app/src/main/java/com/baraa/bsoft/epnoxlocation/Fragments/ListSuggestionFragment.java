package com.baraa.bsoft.epnoxlocation.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baraa.bsoft.epnoxlocation.Model.LocationModel;
import com.baraa.bsoft.epnoxlocation.R;
import com.baraa.bsoft.epnoxlocation.Services.AppLocationsServices;
import com.baraa.bsoft.epnoxlocation.Views.PlacesRecyclerViewAdapter;
import com.baraa.bsoft.epnoxlocation.Views.PlacesRecyclerViewListener;

import java.util.ArrayList;


public class ListSuggestionFragment extends Fragment implements AppLocationsServices.AppLocationsServicesListener,PlacesRecyclerViewListener.OnPlacesRecyclerViewListener {
    private ArrayList<LocationModel> lstSuggetedLocations;
    PlacesRecyclerViewAdapter adapter;
    private int currentTap;

    public int getCurrentTap() {
        return currentTap;
    }

    public void setCurrentTap(int currentTap) {
        this.currentTap = currentTap;
    }

    private GoToNextTabListener goToNextTabListener;
    private ItemListListener itemListListener;

    public interface GoToNextTabListener{
        void OnReadyToDisplayNextTab();
    }
    public interface ItemListListener{
        void onListItemSelected(LocationModel selLocationInfo);
    }

    public void setGoToNextTabListener(GoToNextTabListener goToNextTabListener) {
        this.goToNextTabListener = goToNextTabListener;
    }
    public void setItemListListener(ItemListListener itemListListener) {
        this.itemListListener = itemListListener;
    }

    public ListSuggestionFragment() {
        // Required empty public constructor
    }

    public static ListSuggestionFragment newInstance() {
        ListSuggestionFragment fragment = new ListSuggestionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lstSuggetedLocations = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_suggestion, container, false);
        RecyclerView rcSuggestions = view.findViewById(R.id.rcSuggestion);
        adapter = new PlacesRecyclerViewAdapter(lstSuggetedLocations,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcSuggestions.setAdapter(adapter);
        rcSuggestions.addOnItemTouchListener(new PlacesRecyclerViewListener(getContext(),rcSuggestions,this));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcSuggestions.setLayoutManager(linearLayoutManager);
        rcSuggestions.setHasFixedSize(true);
        return view;
    }


    @Override
    public void onLocationServiceReady(ArrayList<LocationModel> placesList) {
        lstSuggetedLocations = placesList;
        adapter.loadNewLocations(placesList);
    }

    @Override
    public void onItemPressed(View view, int position) {
       LocationModel selectedLocationInfo = adapter.getSelectedModelChecked(position,view);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        itemListListener.onListItemSelected(selectedLocationInfo);
        if(goToNextTabListener != null && getCurrentTap() == 0){
            goToNextTabListener.OnReadyToDisplayNextTab();
            editor.putString(getString(R.string.main_fragment_title),String.valueOf(selectedLocationInfo.getLatitude())+","+String.valueOf(selectedLocationInfo.getLongitude()));

        }else if(getCurrentTap() == 1) {
            editor.putString(getString(R.string.secondary_fragment_title),String.valueOf(selectedLocationInfo.getLatitude())+","+String.valueOf(selectedLocationInfo.getLongitude()));
        }
        editor.commit();
    }

    @Override
    public void onItemLongPress(View view, int position) {

    }
}
