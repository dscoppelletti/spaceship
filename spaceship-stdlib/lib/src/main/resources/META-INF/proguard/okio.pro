# Source: http://github.com/square/okio/blob/master/okio/jvm/src/main/resources/
#         META-INF/proguard/okio.pro
# Commit: b080ca7bf9436dd7fcc7a593c8845c0919ab80d2 - September 7, 2018

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*