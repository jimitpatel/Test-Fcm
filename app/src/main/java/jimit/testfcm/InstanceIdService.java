package jimit.testfcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import jimit.testfcm.utils.storage.Prefs;

/**
 * Created by jimit on 06-11-2017.
 */

public class InstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = InstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String newToken = FirebaseInstanceId.getInstance().getToken();
        Prefs.setString(getApplicationContext(), Config.KEY_REG_ID, newToken);

        sendTokenToServer(newToken);

        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra(Config.KEY_TOKEN, newToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendTokenToServer(final String token) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "sendTokenToServer: token=" + token);
        // send this token to server
    }
}
