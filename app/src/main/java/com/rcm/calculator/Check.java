package com.rcm.calculator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Check {
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager c = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if (c!= null){
            NetworkInfo[] info = c.getAllNetworkInfo();
            if(info != null){
                for(int i = 0; i<info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
