package project.cse.anti;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import project.cse.anti.Utilities.Utilities;

public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 5000;
    private static final int INITIAL_REQUEST=127;
    private static final int PERMISSION_REQUEST=INITIAL_REQUEST+3;

    private static  final String[] PERMS={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        // Check the permissions when the splash screen is displayed and request access to the permissions
        //if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (Utilities.hasPermissions(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(SplashScreen.this, PERMS, PERMISSION_REQUEST);

            }
        //}

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
