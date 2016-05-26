package vc908.stickersample;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import vc908.stickersample.ui.MainActivity;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
class NotificationManager extends vc908.stickerpipe.gcmintegration.NotificationManager {
    @Override
    public int getColorNotificationIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public int getBwNotificationIcon() {
        return R.drawable.ic_launcher;
    }

    @NonNull
    @Override
    public Intent createNotificationIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
