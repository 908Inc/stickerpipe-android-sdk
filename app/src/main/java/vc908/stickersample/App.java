package vc908.stickersample;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import vc908.stickerfactory.StickersManager;
import vc908.stickerfactory.User;
import vc908.stickerfactory.utils.Utils;
import vc908.stickerpipe.gcmintegration.GcmManager;
import vc908.stickerpipe.jpushintegration.JpushManager;
import vc908.stickersample.ui.ShopActivity;

/**
 * Created by Dmitry Nezhydenko
 * Date 4/7/15
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        // initialize stickerpipe sdk with api key
        StickersManager.initialize(BuildConfig.DEBUG
                        ? BuildConfig.API_KEY_DEBUG
                        : BuildConfig.API_KEY_PROD, this,
                BuildConfig.DEBUG);
        // hide recent tab when empty
        StickersManager.setHideEmptyRecentTab(true);
        // set licence key for in app purchases
        StickersManager.setLicenseKey(BuildConfig.PURCHASE_LICENCE_KEY);
        // Set test user properties
        StickersManager.setUserSubscribed(false);
        Map<String, String> userData = new HashMap<>();
        userData.put(User.KEY_GENDER, User.GENDER_MALE);
        userData.put(User.KEY_AGE, String.valueOf(30));
        // Set user id
        StickersManager.setUser(Utils.getDeviceId(this) + Utils.getVersionCode(this) + "g", userData);
        // Set shop class with internal currency charging functionality
        StickersManager.setShopClass(ShopActivity.class);
        // Set sender id for GCM
        GcmManager.setGcmSenderId(this, BuildConfig.GCM_SENDER_ID);
        // Set push notification manager for handling Stickerpipe notifications
        GcmManager.setPushNotificationManager(new NotificationManager());
        // Set prices for packs
//        StickersManager.setPrices(new Prices()
//                .setPricePointB("$0.99", 0.99f)
//                .setPricePointC("$1.99", 1.99f)
//        );
        JpushManager.init(this);

        LeakCanary.install(this);
    }
}