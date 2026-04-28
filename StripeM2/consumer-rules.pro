# Keep only public SDK entry point
-keep class singhsarae.badshah.stripem2.creepySteam.StripeManager {
    public *;
}

# Keep interfaces used by app
-keep interface singhsarae.badshah.stripem2.interfaces.** { *; }

# Keep enums used by Stripe callbacks
-keepclassmembers enum * { *; }

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Obfuscate everything else in SDK
-keep,allowobfuscation class singhsarae.badshah.stripem2.** { *; }