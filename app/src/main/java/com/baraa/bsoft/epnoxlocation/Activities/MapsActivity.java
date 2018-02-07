package com.baraa.bsoft.epnoxlocation.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.baraa.bsoft.epnoxlocation.Fragments.ListSuggestionFragment;
import com.baraa.bsoft.epnoxlocation.Fragments.MainFragment;
import com.baraa.bsoft.epnoxlocation.Fragments.PlacesFragmentPagerAdapter;
import com.baraa.bsoft.epnoxlocation.Fragments.ToPlaceFragment;
import com.baraa.bsoft.epnoxlocation.Model.LocationModel;
import com.baraa.bsoft.epnoxlocation.R;
import com.baraa.bsoft.epnoxlocation.Services.AppLocationsServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,AppLocationsServices.AppLocationsServicesListener
,ListSuggestionFragment.GoToNextTabListener{

    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    private static final String TAG = "MapsActivity";
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProvider;
    private PlacesFragmentPagerAdapter pagerAdapter;
    private Fragment mFragment;
    private ViewPager viewPager;
    private TabLayout.Tab toTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new PlacesFragmentPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tbSlides);
        tabLayout.setupWithViewPager(viewPager);
        toTab = tabLayout.getTabAt(1);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startLocationServices();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this).addApi(LocationServices.API)
                .build();

        fusedLocationProvider = new FusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "onLocationResult: latitude >>"+locationResult.getLastLocation().getLatitude()+
                        "longitude >>"+locationResult.getLastLocation().getLongitude());
                LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude()
                        ,locationResult.getLastLocation().getLongitude());
                mFragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
                // based on the current position you can then cast the page to the correct Fragment class and call some method inside that mFragment to reload the data:
                if (0 == viewPager.getCurrentItem() && null != mFragment) {
                    ((MainFragment)mFragment).setUserMarker(latLng);
                } else if (1 == viewPager.getCurrentItem() && null != mFragment){
                    ((ToPlaceFragment)mFragment).setUserMarker(latLng);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }




    @SuppressLint("NewApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(checkLocationPermissions()==true){
            startLocationServices();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    public void startLocationServices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkLocationPermissions() == true){
                LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
                fusedLocationProvider.requestLocationUpdates(locationRequest,locationCallback,null);
            }
        }else {
            LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            fusedLocationProvider.requestLocationUpdates(locationRequest,locationCallback,null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationServices();
                else
                    Snackbar.make(findViewById(R.id.llMain),"Please enable location services!",Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        switch (viewPager.getCurrentItem()){
            case 0:
                ((MainFragment)mFragment).hideSuggestionList();
                break;
            case 1:
                ((ToPlaceFragment)mFragment).hideSuggestionList();
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    @Override
    public void onLocationServiceReady(ArrayList<LocationModel> placesList) {
        for (LocationModel item: placesList) {
            Log.d(TAG, "onCreate: >>>>> "+item.getImgUrl()+"\n");
        }
    }

    public TabLayout.Tab getToTab() {
        return toTab;
    }

    @Override
    public void OnReadyToDisplayNextTab() {
        if(viewPager.getCurrentItem() ==0){
            //toTab.select();
        }
    }
}
