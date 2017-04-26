package project.cse.anti;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import project.cse.anti.Utilities.Utilities;
import project.cse.anti.fragments.OneFragment;
import project.cse.anti.fragments.ThreeFragment;
import project.cse.anti.fragments.TwoFragment;




public class MainActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {


    LocationRequest mLocationRequest;

    GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    String latitude,longitude,url;

    private SensorManager mSensorManager;
    private float mAccel;// acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast;// last acceleration including gravity

    private DBHelper db;

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;




            if (mAccel > 12) {

                try {
                    url = "https://www.google.com/maps/dir/Current+Location/" + latitude + "," + longitude;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");

                    sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.emergencyMessage) + "\n" + url);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);

                } catch (Exception e) {

                    Toast.makeText(MainActivity.this, "Whatsapp is not available to send emergency messages.", Toast.LENGTH_SHORT).show();
                }




                if (Utilities.signalExists(getApplicationContext())) {
                    try {

                        String SENT = "SMS_SENT";
                        String DELIVERED = "SMS_DELIVERED";

                        SmsManager sms = SmsManager.getDefault();
                        PendingIntent sendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
                        PendingIntent deliveryPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);


                        Cursor cursor = db.getData();
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            Log.d("cursor", cursor.getString(0));
                            Log.d("cursor", cursor.getString(1));
                            Log.d("cursor", cursor.getString(2));
                            Log.d("cursor", cursor.getString(3));

                            sms.sendTextMessage(cursor.getString(2), null, cursor.getString(3) + "\n" + url, sendPI, deliveryPI);
                            Toast.makeText(getApplicationContext(), "Sms sent successfully to " + cursor.getString(1), Toast.LENGTH_SHORT).show();


                            cursor.moveToNext();
                        }



                    } catch (Exception a) {
                        Toast.makeText(getApplicationContext(), "Sms failed to send: " + a.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Mobile Network is not available", Toast.LENGTH_SHORT).show();
                }
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    /*The accelometer should be deactivated onPause and activated onResume to save resources (CPU, Battery).*/


    @Override
    protected void onResume(){
        super.onResume();
       mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);

    }
    @Override
    protected void onStop(){
        super.onStop();
        if(mGoogleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
            buildGoogleApiClient();
            mGoogleApiClient.connect();





    }
    protected synchronized void buildGoogleApiClient(){
        if(mGoogleApiClient==null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }

    protected void onSaveInstanceState(Bundle outstate){
        super.onSaveInstanceState(outstate);

    }

    protected void onRestoreInstanceState(Bundle savedState){

    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      AppRate.with(this)
               .setInstallDays(0)
               .setLaunchTimes(3)
               .setRemindInterval(1)
               .setShowLaterButton(true)

               .setOnClickButtonListener(new OnClickButtonListener() {
                   @Override
                   public void onClickButton(int which) {
                       Log.d(MainActivity.class.getName(),Integer.toString(which));
                   }
               })
               .monitor();

            AppRate.showRateDialogIfMeetsConditions(this);

        db=DBHelper.getInstance(this);
        setContentView(R.layout.activity_main);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart",true);

                if(isFirstStart){

                    Intent i = new Intent(MainActivity.this,Intro.class);
                    startActivity(i);

                    SharedPreferences.Editor e = getPrefs.edit();

                    e.putBoolean("firstStart",false);

                    e.apply();
                }
            }
        });

        t.start();

        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        }else{

            buildGoogleApiClient();
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(new LocationRequest());

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient,builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();

                    switch (status.getStatusCode()){
                        case LocationSettingsStatusCodes.SUCCESS:

                            /*
                                All location settings are satisfied. The client can initialize location
                                here.
                             */
                            Log.i("MainActivity","All location settins are satisfied.");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            /*
                                Location settings are not satisfied. But could be fixed by showing
                                the user a dialogue
                             */
                            Log.i("MainActivity","Location settings are not satified. Show the user a dialog to upgrade location settings");
                            try{
                                /*
                                    Show the dialogue by calling startResolutionForResult(), and check
                                    the result in onActivityResult().
                                 */
                                status.startResolutionForResult(MainActivity.this,1000);
                            }catch (IntentSender.SendIntentException e){
                                //Ignore the error
                                Log.d("MainActivity","PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        /*
                            Location settings are not satisfied. However, we have no way to fix the
                            settings so we won't show the dialog.
                         */
                            Log.d("MainActivity","Location settings are inadequate and cannot be fixed here. Dialog not created.");
                            break;
                    }
                }
            });


        }





        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){

        }
        else{
        }

        // Code for accelerating the accelerometer

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent=SensorManager.GRAVITY_EARTH;
        mAccelLast=SensorManager.GRAVITY_EARTH;



        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


          getSupportActionBar().setIcon(R.drawable.logo);
          getSupportActionBar().setDisplayShowTitleEnabled(false);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setUpViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setUpViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new OneFragment(),"Contacts");
        adapter.addFragment(new TwoFragment(),"Location");
        adapter.addFragment(new ThreeFragment(),"Numbers");
        viewPager.setAdapter(adapter);
    }




    @Override
    public void onLocationChanged(Location location) {

        latitude= String.valueOf(location.getLatitude());
        longitude=String.valueOf(location.getLongitude());
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){


            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }





    }



}
