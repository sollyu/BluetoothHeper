# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-obfuscationdictionary          proguard-dic-6.txt
#-renamesourcefileattribute      proguard-dic-6.txt
#-classobfuscationdictionary     proguard-dic-6.txt
#-packageobfuscationdictionary   proguard-dic-6.txt
#-renamesourcefileattribute      this-an-open-source-app
-repackageclasses               java.io

-dontwarn **

-keepattributes Signature
-keepattributes *Annotation*

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * { @com.google.gson.annotations.SerializedName <fields>; }
-keepclassmembers,allowobfuscation class * { @org.greenrobot.eventbus.Subscribe <methods>; }

##########################################################################################################
-keep class androidx.fragment.app.FragmentTransaction{ *; }
-keep class androidx.fragment.app.FragmentTransaction$Op{ *; }
-keep class android.support.v4.app.BackStackRecord{ *; }
-keep class android.support.v4.app.BackStackRecord$Op{ *; }
