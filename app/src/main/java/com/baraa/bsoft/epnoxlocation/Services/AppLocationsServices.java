package com.baraa.bsoft.epnoxlocation.Services;

import com.baraa.bsoft.epnoxlocation.Model.LocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by baraa on 04/01/2018.
 */

public class AppLocationsServices implements Download.DownloadListener {
    private static final String TAG = "AppLocationsServices";
    private ArrayList<LocationModel> locationsList;
    private AppLocationsServicesListener mAppLocationsServicesListener;

    public interface AppLocationsServicesListener{
        void onLocationServiceReady(ArrayList<LocationModel> placesList);
    }
    public AppLocationsServices() {
        locationsList = new ArrayList<>();
    }

    public AppLocationsServices searchNearbyLocationFromAddress(String search,Download download){
        if(search != null && search.length() > 5){
            Download downloading = download;
            downloading.setDownloadListener(this);
            downloading.execute(search);
        }
        return this;
    }
    public void setAppLocationsServicesListener(AppLocationsServicesListener listener){
        mAppLocationsServicesListener = listener;
    }
    @Override
    public void onDownloadComplete(String dataText) {
        locationsList.clear();
        try {
            JSONObject json = new JSONObject(dataText);
            JSONArray jsonArray = json.getJSONArray("results");
            for (int i=0;i < jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONObject objLoc = obj.getJSONObject("geometry").getJSONObject("location");
                LocationModel locationModel = new LocationModel(Float.parseFloat(objLoc.getString("lat")),
                        Float.parseFloat(objLoc.getString("lng")),obj.getString("name")
                        ,obj.getString("formatted_address"),obj.getString("icon"));
                locationsList.add(locationModel);
            }
            mAppLocationsServicesListener.onLocationServiceReady(locationsList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
