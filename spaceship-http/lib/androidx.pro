# AndroidX and Jetifier cause some annoying notes

# Note: okhttp3.internal.platform.AndroidPlatform: can't find dynamically
#   referenced class com.android.org.conscrypt.SSLParametersImpl
# Note: okhttp3.internal.platform.AndroidPlatform: can't find dynamically
#    referenced class org.apache.harmony.xnet.provider.jsse.SSLParametersImpl
# Note: okhttp3.internal.platform.AndroidPlatform$CloseGuard: can't find
#   dynamically referenced class dalvik.system.CloseGuard
# Note: okhttp3.internal.platform.ConscryptPlatform: can't find dynamically
#   referenced class org.conscrypt.Conscrypt
# Note: okhttp3.internal.platform.Platform: can't find dynamically referenced
#   class sun.security.ssl.SSLContextImpl
-dontnote okhttp3.internal.platform.**
