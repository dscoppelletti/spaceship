# AndroidX and Jetifier cause some annoying notes

# androidx.databinding:databinding-runtime:3.2.1
# -keep public class * extends android.databinding.DataBinderMapper
#
# Note: the configuration refers to the unknown class
#   'android.databinding.DataBinderMapper' Maybe you meant the fully qualified
#   name 'androidx.databinding.DataBinderMapper'?
-keep public class * extends androidx.databinding.DataBinderMapper

# http://stackoverflow.com/questions/52689607 - October 7, 2018
# Note: kotlin.internal.PlatformImplementationsKt: can't find dynamically
#   referenced class kotlin.internal.JRE8PlatformImplementations
# Note: kotlin.internal.PlatformImplementationsKt: can't find dynamically
#   referenced class kotlin.internal.JRE7PlatformImplementations
# Note: kotlin.jvm.internal.Reflection: can't find dynamically referenced class
#   kotlin.reflect.jvm.internal.ReflectionFactoryImpl
-keep class kotlin.Metadata { *; }
-dontnote kotlin.internal.PlatformImplementationsKt
-dontnote kotlin.reflect.jvm.internal.**

# Note: kotlin.coroutines.jvm.internal.DebugMetadataKt accesses a declared
#   field 'label' dynamically
-keepclassmembers @kotlin.coroutines.jvm.internal.DebugMetadataKt class * {
    *** label;
}

# proguard-android.txt-3.2.1
# -keepclassmembers public class * extends android.view.View {
#    void set*(***);
#    *** get*();
# }
#
# Note: the configuration keeps the entry point
# 'com.google.android.material.chip.Chip { void
# setChipDrawable(com.google.android.material.chip.ChipDrawable); }', but not
# the descriptor class 'com.google.android.material.chip.ChipDrawable'
-keepclassmembers,includedescriptorclasses public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# Note: com.google.android.gms.common.util.WorkSourceUtil: can't find
# dynamically referenced class android.os.WorkSource$WorkChain
-dontnote com.google.android.gms.common.util.WorkSourceUtil

# Note: the configuration keeps the entry point
# 'com.google.android.gms.common.api.internal.LifecycleCallback {
# com.google.android.gms.common.api.internal.LifecycleFragment
# getChimeraLifecycleFragmentImpl(
# com.google.android.gms.common.api.internal.LifecycleActivity); }', but not the
# descriptor class
# 'com.google.android.gms.common.api.internal.LifecycleActivity'
-keepclassmembers,includedescriptorclasses public class com.google.android.gms.common.api.internal.LifecycleCallback {
    *** getChimeraLifecycleFragmentImpl(...);
}

# Note: com.squareup.moshi.ClassFactory accesses a declared field 'theUnsafe'
# dynamically
-keepclassmembers class * {
    *** theUnsafe;
}

# Note: the configuration keeps the entry point
#   'com.squareup.moshi.StandardJsonAdapters$ObjectJsonAdapter {
#   StandardJsonAdapters$ObjectJsonAdapter(com.squareup.moshi.Moshi); }', but
#   not the descriptor class 'com.squareup.moshi.Moshi'
# Note: the configuration keeps the entry point
#   'com.squareup.moshi.ClassJsonAdapter {
#   ClassJsonAdapter(com.squareup.moshi.ClassFactory,java.util.Map); }', but not
#   the descriptor class 'com.squareup.moshi.ClassFactory'
# Note: the configuration keeps the entry point
#   'com.squareup.moshi.CollectionJsonAdapter {
#   CollectionJsonAdapter(com.squareup.moshi.JsonAdapter,
#   com.squareup.moshi.CollectionJsonAdapter$1); }', but not the descriptor
#   class 'com.squareup.moshi.CollectionJsonAdapter$1'
-keepclassmembers,includedescriptorclasses class * extends com.squareup.moshi.JsonAdapter {
    <init>(...);
}

# Note: com.squareup.moshi.ClassFactory: can't find dynamically referenced class
#   sun.misc.Unsafe
-dontnote com.squareup.moshi.ClassFactory
