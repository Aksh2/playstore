package project.cse.anti.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.ConnectionRequest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.ServiceConfigurationError;
import java.util.concurrent.TimeUnit;

import project.cse.anti.MyCustomAdapter;
import project.cse.anti.R;
import project.cse.anti.Utilities.Utilities;

public class ThreeFragment extends Fragment{

    private static final String[] CALL_PERMS={Manifest.permission.CALL_PHONE};

    private static final int INITIAL_REQUEST=128;
    private static final int CALL_REQUEST=INITIAL_REQUEST+3;


    public ThreeFragment(){
        //Required empty constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] names={"Helpline for Women","Police Control Room","Railway Helpline","Emergency Services","Train Accident","Air Accident"};
        final String[] numbers={"1091","100","1512","108","1072","1071"};
        View view = inflater.inflate(R.layout.activity_three_fragment, null, false);

        ListView lv =(ListView) view.findViewById(R.id.numberList);


        MyCustomAdapter adapter= new MyCustomAdapter(getActivity(),names,numbers);


        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             String number=numbers[position];
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        }
        else
        {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
            }catch (SecurityException e) {
                ActivityCompat.requestPermissions(getActivity(),CALL_PERMS,CALL_REQUEST);
                Toast.makeText(getActivity(), "Please grant permission to make calls", Toast.LENGTH_SHORT).show();
            }
        }
         }
     });

    return view;
    }




}
