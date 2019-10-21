package it.scoppelletti.spaceship.security

import kotlin.test.BeforeTest
import kotlin.test.Test

class CryptoProviderMTest : AbstractCryptoProviderTest() {

    @BeforeTest
    fun setUp() {
        onSetUp()
        cryptoProvider = CryptoProviderMarshmallow(random, securityBridge)
    }

    @Test
    fun validTest() {
        onValidTest()
    }

    @Test
    fun expiredTest() {
        onExpiredTest()
    }
}
