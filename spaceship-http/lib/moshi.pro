# Source: http://github.com/square/moshi/blob/master/README.md
# Commit: 83f60d6bd7b2f0ef9ea54791ef8cc5c57dfee1a6 - Aug 15, 2018

-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *

# Using the codegen API
-keep class **JsonAdapter {
    <init>(...);
    <fields>;
}
-keepnames @com.squareup.moshi.JsonClass class *