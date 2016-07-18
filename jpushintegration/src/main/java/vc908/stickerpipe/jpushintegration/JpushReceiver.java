package vc908.stickerpipe.jpushintegration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import vc908.stickerfactory.NetworkService;
import vc908.stickerfactory.StickersManager;
import vc908.stickerpipe.gcmintegration.GcmManager;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */

public class JpushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            StickersManager.sendGcmToken(intent.getExtras().getString(JPushInterface.EXTRA_REGISTRATION_ID), NetworkService.TOKEN_TYPE_JPUSH);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String extraData = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> data = new Gson().fromJson(extraData, type);
            if (data != null && !data.isEmpty()) {
                Bundle bundle = new Bundle();
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    bundle.putString(entry.getKey(), entry.getValue());
                }
                GcmManager.processPush(context, bundle);
            }
        }
    }
}