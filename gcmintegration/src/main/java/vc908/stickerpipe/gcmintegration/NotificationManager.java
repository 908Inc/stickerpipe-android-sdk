package vc908.stickerpipe.gcmintegration;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vc908.stickerfactory.ManagerFacade;
import vc908.stickerfactory.StickersKeyboardController;
import vc908.stickerfactory.StickersManager;
import vc908.stickerfactory.utils.Utils;


/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
public abstract class NotificationManager {

    public NotificationManager() {
        mOkHttpClient = new OkHttpClient();
    }

    private OkHttpClient mOkHttpClient;

    private static final String ACTION_CONTENT_UPDATE = "content.update";
    private static final String ACTION_PACK_SHOW = "pack.show";
    private static final String ACTION_SHOP_SHOW = "shop.show";
    private static final String ACTION_OPEN_TAB_SEARCH = "tab.search";
    private static final String ACTION_OPEN_TAB_PACK = "tab.pack";

    private static final String SOURCE_STICKERPIPE = "stickerpipe";

    private static final String BUNDLE_KEY_SOURCE = "source";
    private static final String BUNDLE_KEY_ACTION = "action";
    private static final String BUNDLE_KEY_TITLE = "title";
    private static final String BUNDLE_KEY_MESSAGE = "message";
    private static final String BUNDLE_KEY_PACK = "pack";
    private static final String BUNDLE_KEY_IMAGE = "image";
    private static final String BUNDLE_KEY_ID = "push_id";

    public static final String ARG_OPEN_SHOP_PACK_NAME = "sp_arg_open_shop_pack_name";
    public static final String ARG_OPEN_SHOP = "sp_arg_open_shop";
    public static final String ARG_PUSH_ID = "sp_arg_push_id";
    public static final String ARG_OPEN_STICKERS_KEYBOARD = "sp_arg_open_stickers_keyboard";


    public void showNotification(Context context, Bundle data) {
        String title = data.getString(BUNDLE_KEY_TITLE);
        String message = data.getString(BUNDLE_KEY_MESSAGE);
        String pack = data.getString(BUNDLE_KEY_PACK);
        String imageLink = data.getString(BUNDLE_KEY_IMAGE);
        String pushId = data.getString(BUNDLE_KEY_ID);
        String action = data.getString(BUNDLE_KEY_ACTION);
        Intent resultIntent = createNotificationIntent(context);
        if (!TextUtils.isEmpty(pushId)) {
            resultIntent.putExtra(ARG_PUSH_ID, pushId);
        }
        if (!TextUtils.isEmpty(pack) && ACTION_PACK_SHOW.equals(action)) {
            resultIntent.putExtra(ARG_OPEN_SHOP_PACK_NAME, pack);
        }
        if (ACTION_SHOP_SHOW.equals(action)) {
            resultIntent.putExtra(ARG_OPEN_SHOP, true);
        }
        if (ACTION_OPEN_TAB_PACK.equals(action)
                || ACTION_OPEN_TAB_SEARCH.equals(action)) {
            resultIntent.putExtra(ARG_OPEN_STICKERS_KEYBOARD, true);
        }
        showNotification(context, title, message, imageLink, resultIntent);
    }

    /**
     * Show plain notification with given params
     *
     * @param context      Context of notification
     * @param title        Notification title
     * @param message      Notification message
     * @param resultIntent Result intent for click event
     */
    private void showNotification(final Context context, String title, String message, final String imageLink, Intent resultIntent) {
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(getNotificationIcon())
                        .setColor(NotificationCompat.COLOR_DEFAULT)
                        .setContentTitle(title)
                        .setContentText(message);
        if (resultIntent != null) {
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, Utils.atomicInteger.incrementAndGet(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);
        }
        builder.setAutoCancel(true);
        if (!TextUtils.isEmpty(imageLink)) {
            loadImage(imageLink)
                    .subscribe(new Subscriber<Bitmap>() {
                        @Override
                        public void onNext(Bitmap bitmap) {
                            builder.setLargeIcon(bitmap);
                            showNotification(context, builder.build());
                        }

                        @Override
                        public void onError(Throwable e) {
                            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), getColorNotificationIcon()));
                            showNotification(context, builder.build());
                        }

                        @Override
                        public void onCompleted() {

                        }
                    });
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), getColorNotificationIcon()));
            showNotification(context, builder.build());
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? getBwNotificationIcon() : getColorNotificationIcon();
    }

    private void showNotification(Context context, Notification notification) {
        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Utils.atomicInteger.incrementAndGet(), notification);
    }

    /**
     * Check, is given data contains stickers info
     *
     * @param data Data for check
     * @return Result of check
     */
    public static boolean isStickersData(Bundle data) {
        return SOURCE_STICKERPIPE.equals(data.getString(BUNDLE_KEY_SOURCE));
    }

    /**
     * Try to process push data
     *
     * @param context Data context
     * @param data    Data for process
     * @return True, is data from stickerpipe source
     */
    public boolean processPush(Context context, Bundle data) {
        if (isStickersData(data)) {
            String action = data.getString(BUNDLE_KEY_ACTION);
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case ACTION_CONTENT_UPDATE:
                        ManagerFacade.checkPackUpdates();
                        break;
                    case ACTION_OPEN_TAB_PACK:
                        ManagerFacade.setOpenTab(data.getString(BUNDLE_KEY_PACK));
                        showNotification(context, data);
                        break;
                    case ACTION_OPEN_TAB_SEARCH:
                        ManagerFacade.setOpenSearchTab();
                        showNotification(context, data);
                        break;
                    case ACTION_SHOP_SHOW:
                    case ACTION_PACK_SHOW:
                    default:
                        showNotification(context, data);
                }
            } else {
                showNotification(context, data);
            }
            return true;
        } else {
            return false;
        }
    }

    public Observable<Bitmap> loadImage(@NonNull final String imageUrl) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    byte[] imageFile = getFile(imageUrl);
                    subscriber.onNext(BitmapFactory.decodeByteArray(imageFile, 0, imageFile.length));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get file from network
     *
     * @param url Image url
     * @throws IOException
     */
    public byte[] getFile(@NonNull String url) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        Response response = mOkHttpClient.newCall(requestBuilder.url(url).build()).execute();
        return response.body().bytes();
    }

    /**
     * Check intent for stickerpipe data and try to process is
     *
     * @param context            Context
     * @param intent             Input intent
     * @param keyboardController Stickers keyboard controller
     */
    public static boolean processIntent(Context context, Intent intent, StickersKeyboardController keyboardController) {
        String pushId = intent.getStringExtra(ARG_PUSH_ID);
        if (!TextUtils.isEmpty(pushId)) {
            String packShowAtShop = intent.getStringExtra(ARG_OPEN_SHOP_PACK_NAME);
            if (!TextUtils.isEmpty(packShowAtShop)) {
                intent.removeExtra(ARG_OPEN_SHOP_PACK_NAME);
                StickersManager.showPackInfoByPackName(context, packShowAtShop);
            } else if (intent.getBooleanExtra(ARG_OPEN_SHOP, false)) {
                intent.removeExtra(ARG_OPEN_SHOP);
                StickersManager.openShop(context);
            } else if (keyboardController != null
                    && intent.getBooleanExtra(ARG_OPEN_STICKERS_KEYBOARD, false)) {
                intent.removeExtra(ARG_OPEN_STICKERS_KEYBOARD);
                keyboardController.processTabShowIntent();
            }
            ManagerFacade.onAppOpenByPush(pushId);
            return true;
        }
        return false;
    }

    public static boolean processIntent(Context context, Intent intent) {
        return processIntent(context, intent, null);
    }


    /**
     * Resource for for pre Lollipop notification icon
     *
     * @return Drawable res id
     */
    @DrawableRes
    public abstract int getColorNotificationIcon();

    /**
     * Resource for for Lollipop and latest versions notification icon
     *
     * @return Drawable res id
     */
    @DrawableRes
    public abstract int getBwNotificationIcon();

    /**
     * Create notification intent
     *
     * @param context Context
     * @return Intent
     */
    @NonNull
    public abstract Intent createNotificationIntent(Context context);
}
