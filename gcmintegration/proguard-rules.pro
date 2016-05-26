-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

-keep public class vc908.stickerpipe.gcmintegration.GcmManager{*;}
-keep public class vc908.stickerpipe.gcmintegration.GcmManager$PushNotificationListener{*;}
-keep public class vc908.stickerpipe.gcmintegration.NotificationManager{*;}

-keep public class vc908.stickerpipe.gcmintegration.GcmManager$PushNotificationListener{*;}

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}
