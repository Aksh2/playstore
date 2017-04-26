# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/akshay/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
# Keep source file names, line numbers, and Parse class/method names for easier debugging
-keepattributes SourceFile,LineNumberTable
-keepnames class com.parse.** { *; }

# Required for Parse
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.**
-dontwarn okio.**
-dontwarn android.net.SSLCertificateSocketFactory
-dontwarn android.app.Notification
-dontwarn com.facebook.*
-dontwarn com.parse.*
-dontwarn com.parse.ui.*