package project.cse.anti.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import project.cse.anti.R;

public class TwoFragment extends Fragment implements LocationListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Marker mCurrLocationMarker;
    SupportMapFragment mapFragment;

    View view;

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
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(latLng)
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
                //buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
        else{
            try {
               // buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);

            }
            catch (SecurityException e){
                ActivityCompat.requestPermissions(getActivity(),LOCATION_PERMS,LOCATION_REQUEST);
                Toast.makeText(getActivity(), "Please grant the location permission for this application.", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient(){
        if(mGoogleApiClient==null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }
    @Override
    public void onStop(){
        super.onStop();
        if(mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }



    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);



    }




}
