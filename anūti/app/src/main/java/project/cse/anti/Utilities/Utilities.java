package project.cse.anti.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by akshay on 16/10/16.
 */
public class Utilities {

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try{
            if(connMgr == null){

            }
            if(connMgr.getActiveNetworkInfo() != null
                    && connMgr.getActiveNetworkInfo().isAvailable()
                    && connMgr.getActiveNetworkInfo().isConnected()){
                return true;
            }
            else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
