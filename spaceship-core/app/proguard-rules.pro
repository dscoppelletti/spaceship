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

# Note: the configuration refers to the unknown class
# 'com.squareup.moshi.JsonAdapter'
-dontnote com.squareup.moshi.JsonAdapter

# Note: the configuration refers to the unknown class
# 'com.squareup.moshi.JsonClass'
-dontnote com.squareup.moshi.JsonClass

# Note: the configuration refers to the unknown class
# 'com.squareup.moshi.JsonQualifier'
-dontnote com.squareup.moshi.JsonQualifier

# Note: the configuration refers to the unknown class
#   'android.databinding.DataBinderMapper' Maybe you meant the fully qualified
#   name 'androidx.databinding.DataBinderMapper'?
-dontnote android.databinding.DataBinderMapper
