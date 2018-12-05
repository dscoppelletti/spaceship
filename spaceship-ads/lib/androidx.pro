# AndroidX and Jetifier cause some annoying notes

# Note: the configuration refers to the unknown class
# 'com.google.android.gms.ads.mediation.rtb.RtbAdapter'
-dontnote com.google.android.gms.ads.mediation.rtb.RtbAdapter

# Note: the configuration refers to the unknown field
# 'com.google.android.gms.common.api.internal.BasePendingResult$ReleasableResultGuardian
# mResultGuardian' in class
# 'com.google.android.gms.common.api.internal.BasePendingResult'
-dontnote com.google.android.gms.common.api.internal.BasePendingResult

# Note: the configuration refers to the unknown class
# 'com.google.android.gms.common.api.internal.BasePendingResult$ReleasableResultGuardian'
-dontnote com.google.android.gms.common.api.internal.BasePendingResult$ReleasableResultGuardian

# Note: the configuration keeps the entry point '***' { *** *(...); }', but not
# the descriptor class '***'
-keepclassmembers,includedescriptorclasses public class com.google.firebase.iid.FirebaseInstanceId {
    *** getInstance(...);
}

-keepclassmembers,includedescriptorclasses public class com.google.android.gms.common.api.internal.LifecycleCallback {
    *** getChimeraLifecycleFragmentImpl(...);
}

-keepclassmembers,includedescriptorclasses public class com.google.firebase.analytics.connector.internal.AnalyticsConnectorRegistrar {
    *** *(com.google.firebase.components.ComponentContainer);
}

-keepclassmembers,includedescriptorclasses public class com.google.ads.mediation.** {
    void initialize(...);
    void loadAd(...);
    void request*(...);
    com.google.android.gms.ads.AdRequest *(...);
    com.google.android.gms.ads.InterstitialAd *(...);
}

-keepclassmembers,includedescriptorclasses public class com.google.android.gms.ads.** {
    void request*(...);
}

-keepclassmembers,includedescriptorclasses public class com.google.android.gms.internal.ads.** {
    void requestInterstitialAd(...);
}

-keepclassmembers,includedescriptorclasses public class com.google.android.gms.ads.internal.ClientApi {
    *** create*(...);
    *** get*(...);
}

-keepclassmembers,includedescriptorclasses public class com.google.android.gms.ads.internal.gmsg.HttpClient {
    <init>(...);
    void initialize(...);
    void loadAd(...);
    org.json.JSONObject *(...);
    com.google.android.gms.ads.internal.gmsg.HttpClient$* *(com.google.android.gms.ads.internal.gmsg.HttpClient$*);
}