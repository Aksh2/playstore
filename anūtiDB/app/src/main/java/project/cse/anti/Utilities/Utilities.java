package project.cse.anti.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;

import project.cse.anti.R;

/**
 * Created by akshay on 16/10/16.
 */
public class Utilities {

    public static Context utilityContext;
    public Utilities(Context context){
        utilityContext=context;
    }



    public static void createShowCaseView(Activity a, Target v, String title, String content){


            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            // This aligns button to the bottom left side of screen
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            // Adding 16dp margins to the button
            int margin = ((Number)(utilityContext.getResources().getDisplayMetrics().density * 16))
                    .intValue();
            layoutParams.setMargins(margin,margin,margin,margin);

            try {
                ShowcaseView showcaseView= new ShowcaseView.Builder(a)
                        .setTarget(v)
                        .setContentTitle(title)
                        .setContentText(content)
                        .hideOnTouchOutside()
                        .setStyle(R.style.CustomShowcaseTheme)
                        .build();

                showcaseView.setTitleTextAlignment(Layout.Alignment.ALIGN_NORMAL);
                showcaseView.setButtonPosition(layoutParams);
            }
            catch (Exception e){
                e.printStackTrace();
            }




    }

    public static boolean hasPermissions(Context context, String... permissions){
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions !=null){
            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean signalExists(Context appcontext){
            TelephonyManager tel = (TelephonyManager) appcontext.getSystemService(Context.TELEPHONY_SERVICE);
            return ((tel.getNetworkOperator() !=null && tel.getNetworkOperator().equals(""))? false:true);
            }


}
