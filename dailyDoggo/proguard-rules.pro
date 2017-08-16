# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/kyler/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-dontwarn org.apache.**
-keep class com.google.**
-dontwarn com.google.**
-keep public class com.android.vending.licensing.ILicensingService

-optimizationpasses 5
 -dontpreverify
 -verbose
 -dump class_files.txt
 -printseeds seeds.txt
 -printusage unused.txt
 -printmapping mapping.txt
 -keepattributes Signature
 
 -dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.**
-dontwarn org.apache.**
-keep class com.google.**
-dontwarn com.google.**
-keep public class com.android.vending.licensing.ILicensingService

# Lambdas
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

# Keep tabs class name
-keepnames class projekt.substratum.tabs.*

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Welcome-android
-keepclassmembers class * extends com.stephentuso.welcome.WelcomeActivity {
    public static java.lang.String welcomeKey();
}
-keep class com.stephentuso.welcome.** { *; }

# About libraries
-keep class .R
-keep class **.R$* {
    <fields>;
}

# Material sheet FAB
-keep class io.codetail.animation.arcanimator.** { *; }

# AVLoadingIndicatorView
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

# OM Library
-keep class android.content.** { *; }
-keep class com.android.server.theme.** { *; }
-dontwarn android.os.**
-dontwarn android.content.**
-dontwarn com.android.server.theme.**

# APK Signer
-dontwarn sun.security.**
-dontwarn javax.naming.**
-dontwarn org.slf4j.impl.**
-dontwarn junit.textui.TestRunner

# Android support libraries
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep class android.support.v7.widget.RoundRectDrawable { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
