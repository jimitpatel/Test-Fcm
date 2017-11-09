package jimit.testfcm.utils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Created by jimit on 15-06-2017.
 */

public class NetworkUtils {

    public static boolean haveNetworkConnection(@NonNull Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                        haveConnectedWifi = true;
                        if (haveConnectedMobile)
                            break;
                    } else if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                        haveConnectedMobile = true;
                        if (haveConnectedWifi)
                            break;
                    }
                }
            }
        }else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                                haveConnectedWifi = true;
                                if (haveConnectedMobile)
                                    break;
                            } else if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                                haveConnectedMobile = true;
                                if (haveConnectedWifi)
                                    break;
                            }
                        }
                    }
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
