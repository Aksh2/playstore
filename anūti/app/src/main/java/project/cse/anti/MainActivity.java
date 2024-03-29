package project.cse.anti;



import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionBarPolicy;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.realtime.internal.event.ObjectChangedDetails;
import com.google.android.gms.games.quest.Quests;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.squareup.seismic.ShakeDetector;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import project.cse.anti.R;
import project.cse.anti.Utilities.Utilities;
import project.cse.anti.fragments.OneFragment;
import project.cse.anti.fragments.ThreeFragment;
import project.cse.anti.fragments.TwoFragment;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class MainActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private ParseQueryAdapter<ContactsDB> ContactsAdapter;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ContactsDB contacts = new ContactsDB();
    LocationManager mLocationManager;
    String latitude,longitude,url;

    private SensorManager mSensorManager;
    private float mAccel;// acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast;// last acceleration including gravity

    private static  final String[] LOCATION_PERMS={Manifest.permission.ACCESS_FINE_LOCATION};




    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast= mAccelCurrent;
            mAccelCurrent =(float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel*0.9f+delta;
            ArrayList<String> listNames = new ArrayList<String>();
            ArrayList<String> listNumbers = new ArrayList<String>();

            //TODO: remove this after testing
            //Toast.makeText(MainActivity.this, "shake value: " + Utilities.signalExists(getApplicationContext()), Toast.LENGTH_SHORT).show();

            if(mAccel > 12) {
               // Toast.makeText(MainActivity.this, "shake value: " + Utilities.signalExists(getApplicationContext()), Toast.LENGTH_SHORT).show();
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
                    ParseQuery<ContactsDB> query = ContactsDB.getQuery();
                    query.fromLocalDatastore();
                    query.whereNotEqualTo("Message", " ");
                    query.findInBackground(new FindCallback<ContactsDB>() {
                        @Override
                        public void done(List<ContactsDB> objects, ParseException e) {
                            if (!isFinishing()) {
                                for (int i = 0; i < objects.size(); i++) {
                                    contacts = objects.get(i);
                                    try {
                                        String SENT = "SMS_SENT";
                                        String DELIVERED = "SMS_DELIVERED";

                                        SmsManager sms = SmsManager.getDefault();
                                        PendingIntent sendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
                                        PendingIntent deliveryPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

                                        sms.sendTextMessage(contacts.getNumber(), null, contacts.getMessage() + "\n" + url, sendPI, deliveryPI);

                                        //sendSms(MainActivity.this, getDefaultSim(MainActivity.this),contacts.getNumber(),null,contacts.getMessage(),sendPI,deliveryPI);
                                        Toast.makeText(getApplicationContext(), "Sms sent successfully to " + contacts.getName(), Toast.LENGTH_SHORT).show();

                                    } catch (Exception a) {
                                        Toast.makeText(getApplicationContext(), "Sms failed to send: " + a.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }

                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
                                toast.show();


                            }
                        }

                    });


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
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();

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
        mGoogleApiClient.connect();

    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    protected void onSaveInstanceState(Bundle outstate){
        super.onSaveInstanceState(outstate);

    }

    protected void onRestoreInstanceState(Bundle savedState){

    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

        buildGoogleApiClient();


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){

        }
        else{

            //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST);
        }

        // Code for accelerating the accelerometer

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent=SensorManager.GRAVITY_EARTH;
        mAccelLast=SensorManager.GRAVITY_EARTH;



        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        //Toast.makeText(getApplicationContext(),
        //        "Location changed:Lat:" + location.getLatitude() + "Lng" + location.getLongitude(),Toast.LENGTH_SHORT).show();
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


    private void setSupportActionBar(Toolbar toolbar) {

    }



    }

    public boolean sendSms(Context ctx,int simID, String toNum, String centerNum,String smsText, PendingIntent sentIntent, PendingIntent deliveryIntent){
        String name;

        try{
            if(simID == 0){
                name="isms";
            }
            else if(simID ==1){
                name="isms2";
            }
            else{
                throw new Exception("can not get service for the sim '" + simID + "',only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService",String.class);
            method.setAccessible(true);
            Object param = method.invoke(null,name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null,param);
            if(Build.VERSION.SDK_INT < 18){
                method = stubObj.getClass().getMethod("sendText",String.class,String.class,String.class,PendingIntent.class,PendingIntent.class);
                method.invoke(stubObj, toNum, centerNum, smsText, sentIntent, deliveryIntent);

            }
            else{
                method = stubObj.getClass().getMethod("sendText",String.class,String.class,String.class,PendingIntent.class,PendingIntent.class);
                method.invoke(stubObj,ctx.getPackageName(),toNum,centerNum,smsText,sentIntent,deliveryIntent);
            }
            return true;
        } catch (ClassNotFoundException e){
            Log.e("SendSms","ClassNotFoundException: " + e.getMessage());
        } catch (NoSuchMethodException e){
            Log.e("SendSms","NoSuchMethodException: " + e.getMessage());
        }
        catch (InvocationTargetException e){
            Log.e("SendSms","InvocationTargetException: " + e.getMessage());
        }
        catch (IllegalAccessException e){
            Log.e("SendSms","IllegalAccessException: " + e.getMessage());
        }
        catch (Exception e){
            Log.e("SendSms","Exception: " + e.getMessage());
        }
        return false;

    }

    public int getDefaultSim(Context context){

        int defaultSimm = -1;

        Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method_getDefaultSim;


        try{
            method_getDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
            method_getDefaultSim.setAccessible(true);
            defaultSimm = (Integer) method_getDefaultSim.invoke(tm);
        }catch (Exception e){
            e.printStackTrace();
        }
        Method method_getSmsDefaultSim;
        int smsDefaultSim=-1;
        try{
            method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getSmsDefaultSim");
            smsDefaultSim = (Integer) method_getSmsDefaultSim.invoke(tm);
        }catch (Exception e){
            e.printStackTrace();
        }
        return smsDefaultSim;
    }

}
