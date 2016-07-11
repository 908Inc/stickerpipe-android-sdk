-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.**{*;}
-keep public class vc908.stickerpipe.jpushintegration.JpushManager{*;}
# ================== Gson ==========================
-dontwarn com.google.**
-keep class com.google.gson.**{*;}

# ================== Protobuf ======================
-dontwarn com.google.**
-keep class com.google.protobuf.**{*;}
