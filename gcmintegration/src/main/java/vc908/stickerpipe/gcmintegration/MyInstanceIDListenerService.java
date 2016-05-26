package vc908.stickerpipe.gcmintegration;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import vc908.stickerfactory.StorageManager;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        StorageManager.getInstance().storeIsGcmTokenSent(false);
        startService(new Intent(this, GcmRegistrationIntentService.class));
    }
}
