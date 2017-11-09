package jimit.testfcm;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import jimit.testfcm.utils.Toaster;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION_CODES.HONEYCOMB;

/**
 * Created by jimit on 08-11-2017.
 */

public class Fcm extends Application {

    private static Fcm _mInstance;
    public static synchronized Fcm getInstance() {
        return _mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _mInstance = this;
        Toaster.init(_mInstance);
    }

    public boolean isLowMemoryDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).isLowRamDevice();
        else
            return false;
    }

    static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap/* && SDK_INT >= HONEYCOMB*/) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        // Target ~15% of the available heap.
        return 1024 * 1024 * memoryClass / 2;
    }

    @TargetApi(HONEYCOMB)
    private static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }
}
