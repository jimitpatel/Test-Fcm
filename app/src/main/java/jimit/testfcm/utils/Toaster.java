package jimit.testfcm.utils;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    private static Toaster mInstance = null;
    private Context context;
    private Toast mToast;

    private Toaster(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        mInstance = new Toaster(context);
    }

    public static void longToast(String message) {
        if (mInstance.mToast != null) mInstance.mToast.cancel();
        mInstance.mToast = Toast.makeText(mInstance.context, message, Toast.LENGTH_LONG);
        mInstance.mToast.show();
    }

    public static void shortToast(String message) {
        if (mInstance.mToast != null) mInstance.mToast.cancel();
        mInstance.mToast = Toast.makeText(mInstance.context, message, Toast.LENGTH_SHORT);
        mInstance.mToast.show();
    }
}