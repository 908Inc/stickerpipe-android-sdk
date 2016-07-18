package vc908.stickerpipe.jpushintegration;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import cn.jpush.android.api.JPushInterface;

public class JpushManager {
    public static void init(Context context) {
        // do not use jspuh when gcm allowed
        if (!isGooglePlayServicesAvailable(context)) {
            JPushInterface.init(context);
        }
    }


    /**
     * Check, is google play services available for current device
     *
     * @param context Context
     * @return Is play services available
     */
    private static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }
}