package jimit.testfcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import jimit.testfcm.utils.Toaster;
import jimit.testfcm.utils.storage.Prefs;

public class FcmActivity extends AppCompatActivity {

    private static final String TAG = FcmActivity.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);

        txtRegId = findViewById(R.id.txt_reg_id);
        txtMessage = findViewById(R.id.txt_push_message);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra(Config.KEY_MESSAGE);
                    Toaster.shortToast("Push Notification=" + message);
                    txtMessage.setText(message);
                }
            }
        };
        displayFirebaseRegId();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void displayFirebaseRegId() {
        String regId = Prefs.getString(getApplicationContext(), Config.KEY_REG_ID, null);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "displayFirebaseRegId: regId=" + regId);
        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg ID=" + regId);
        else
            txtRegId.setText("Firebase Reg ID not received yet");
    }
}
