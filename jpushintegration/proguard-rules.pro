-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-dontwarn com.google.**

-keep class cn.jpush.**{*;}
-keep class com.google.gson.**{*;}
-keep class com.google.protobuf.**{*;}

-keep public class vc908.stickerpipe.jpushintegration.JpushManager{*;}
