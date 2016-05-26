package vc908.stickerpipe.gcmintegration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import vc908.stickerfactory.StorageManager;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
public class GcmManager {

    static String gcmClientId;
    private static NotificationManager pushNotificationManager;

    /**
     * Set GCM client id for push messages
     *
     * @param clientId GCM client id
     */
    public static void setGcmSenderId(Context context, String clientId) {
        gcmClientId = clientId;
        if (!StorageManager.getInstance().isGcmTokenSent()) {
            context.startService(new Intent(context, GcmRegistrationIntentService.class));
        }
    }

    public static void setPushNotificationManager(NotificationManager manager) {
        pushNotificationManager = manager;
    }

    public static boolean processPush(Context context, Bundle data) {
        return pushNotificationManager != null && pushNotificationManager.processPush(context, data);
    }
}
