package vc908.stickerpipe.gcmintegration;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import vc908.stickerfactory.StickersManager;
import vc908.stickerfactory.StorageManager;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
public class GcmRegistrationIntentService extends IntentService {
    private static final String[] TOPICS = {"global"};

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmRegistrationIntentService(String name) {
        super(name);
    }

    public GcmRegistrationIntentService() {
        super(GcmRegistrationIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(GcmManager.gcmClientId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            StickersManager.sendGcmToken(token);
            subscribeTopics(token);
        } catch (IOException e) {
            StorageManager.getInstance().storeIsGcmTokenSent(false);
        }

    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}
