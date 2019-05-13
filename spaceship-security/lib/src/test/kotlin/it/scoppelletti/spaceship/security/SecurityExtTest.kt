package it.scoppelletti.spaceship.security

object SecurityExtTest {

    // - Android SDK 27.3
    // No provider provides the AES algorithm for SecretKeyFactory type
    const val KEY_ALGORITHM_DES = "DES"

    const val KEYSTORE_TYPE = "JCEKS"
    const val PROVIDER_SUN = "SunJCE"
}