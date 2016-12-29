package project.cse.anti.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telecom.ConnectionRequest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.location.LocationRequest;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;

import java.util.concurrent.TimeUnit;

import project.cse.anti.R;
import project.cse.anti.Utilities.Utilities;

public class TwoFragment extends Fragment implements LocationListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mGoogleMap;
    LatLng latLng;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    Marker mCurrLocationMarker;
    SupportMapFragment mapFragment;

    View view,mView;

    private static final int INITIAL_REQUEST=127;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;

    private static  final String[] LOCATION_PERMS={Manifest.permission.ACCESS_FINE_LOCATION};



    public TwoFragment() {
        //Required empty constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.activity_two_fragment,null,false);

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }



    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        // move to camera to that location
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
       // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//        CameraUpdate cameraUpdateFactory = CameraUpdateFactory.newLatLngZoom(latLng,10);
//        mGoogleMap.animateCamera(cameraUpdateFactory);
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(latLng)
                //.tilt(30)
                //.bearing(90)
                .zoom(18)
                .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

        //stop location updates
        if(mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        // if the android version in Marshmellow
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else{
            try {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);

            }
            catch (SecurityException e){
                ActivityCompat.requestPermissions(getActivity(),LOCATION_PERMS,LOCATION_REQUEST);
                Toast.makeText(getActivity(), "Please grant the location permission for this application.", Toast.LENGTH_SHORT).show();

            }
        }

    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
                mGoogleApiClient.connect();
    }
    @Override
    public void onPause(){
        super.onPause();
        if(mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

       // Toast.makeText(getActivity(), "Internet is not available", Toast.LENGTH_SHORT).show();
    }


    /*@Override
    public void onLocationChanged(Location location) {

        // Getting Latitude of the current location
        double longitude=location.getLongitude();
        // Getting longitude of the current location
        double latitude=location.getLatitude();

        // Creating a LatLng object for the current location
        latLng = new LatLng(latitude,longitude);

//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

  //      googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        gMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Marker")
        );
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.moveCamera(CameraUpdateFactory.zoomTo(18));





    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;



    }        */

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);



    }




}
