# Keep SDK public entry
-keep class singhsarae.badshah.stripem2.creepySteam.StripeManager{ *; }

# Keep all callbacks
-keep interface singhsarae.badshah.stripem2.interfaces.** { *; }

# Keep all data models (VERY IMPORTANT)
-keep class singhsarae.badshah.stripem2.utilities.** { *; }
-keep class singhsarae.badshah.stripem2.models.** { *; }

# Keep Gson fields
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Prevent stripping of data classes
-keep class * extends java.io.Serializable { *; }

# Keep SDK public API WITHOUT obfuscation
-keep class singhsarae.badshah.stripem2.customModels.** { *; }
-keep class singhsarae.badshah.stripem2.utilities.** { *; }

# Keep Kotlin getters (important)
-keepclassmembers class * {
    *** get*();
}