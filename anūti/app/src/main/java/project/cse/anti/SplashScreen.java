package project.cse.anti;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 5000;
    private static final int INITIAL_REQUEST=127;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;

    private static  final String[] LOCATION_PERMS={Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(SplashScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            }
        }
        else{
                ActivityCompat.requestPermissions(SplashScreen.this,LOCATION_PERMS,LOCATION_REQUEST);
               // Toast.makeText(SplashScreen.this, "Please grant the location permission for this application.", Toast.LENGTH_SHORT).show();

            }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                finish();

            }
        },SPLASH_TIME_OUT);

    }

}
