# Source: http://github.com/square/retrofit/blob/master/retrofit/src/main/
#         resources/META-INF/proguard/retrofit2.pro
# Commit: 940f634e23bc33d01eae4dbf652cc8522c689bdc - Jun 15, 2018

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
