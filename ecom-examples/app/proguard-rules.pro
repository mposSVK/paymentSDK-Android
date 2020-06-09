-keepattributes Signature
-keepattributes Exceptions
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes RuntimeVisibleAnnotations
-keepattributes AnnotationDefault

-renamesourcefileattribute SourceFile

# Preference objects are inflated via reflection
-keep public class android.support.v7.preference.Preference {
  public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends android.support.v7.preference.Preference {
  public <init>(android.content.Context, android.util.AttributeSet);
}

-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-dontwarn org.xmlpull.v1.**

# ok http
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# retrofit 2
-dontwarn javax.annotation.**
-dontwarn retrofit2.Platform$Java8

-keep class org.simpleframework.** { *; }
-keep interface org.simpleframework.** { *; }

-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}

-dontwarn org.kobjects.**
-dontwarn org.ksoap2.**
-dontwarn org.kxml2.**
-dontwarn org.xmlpull.v1.**

-keep class org.kobjects.** { *; }
-keep class org.ksoap2.** { *; }
-keep class org.kxml2.** { *; }
-keep class org.xmlpull.** { *; }

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-dontpreverify