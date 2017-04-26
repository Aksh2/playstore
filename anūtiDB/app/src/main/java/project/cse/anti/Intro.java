package project.cse.anti;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Intro extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Whatsapp and SMS Alerts","Shake the phone after the emergency contacts are added to send Whatsapp and SMS alerts.",R.drawable.shake,getResources().getColor(R.color.green)));
        addSlide(AppIntroFragment.newInstance("Tap to Call","Just touch on the predefined emergency numbers to call.",R.drawable.touch,getResources().getColor(R.color.colorPrimary)));
        addSlide(AppIntroFragment.newInstance("Map","Find your current accurate location on the map with live location tracking.",R.drawable.location,getResources().getColor(R.color.blue)));

    }
    @Override
    public void onSkipPressed(Fragment currentFragment){
        super.onSkipPressed(currentFragment);
          finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment){
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment,@Nullable Fragment newFragment){
        super.onSlideChanged(oldFragment,newFragment);

    }

}
