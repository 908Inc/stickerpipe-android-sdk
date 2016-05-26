package vc908.stickerpipe.gcmintegration;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
public class MyGcmListenerService extends GcmListenerService {
    public void onMessageReceived(String from, Bundle data) {
        GcmManager.processPush(this, data);
    }
}
