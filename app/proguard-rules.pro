# Add project specific ProGuard rules here.
-keep class com.diabeticmonitor.app.data.db.entity.** { *; }
-keep class com.diabeticmonitor.app.data.db.dao.** { *; }
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.google.gson.annotations.SerializedName <fields>;
}
