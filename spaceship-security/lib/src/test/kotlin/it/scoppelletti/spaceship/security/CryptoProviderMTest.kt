package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.security.i18n.FakeSecurityMessages
import kotlin.test.BeforeTest
import kotlin.test.Test

class CryptoProviderMTest : AbstractCryptoProviderTest() {

    @BeforeTest
    fun setUp() {
        onSetUp()
        cryptoProvider = CryptoProviderMarshmallow(random, securityBridge,
                FakeSecurityMessages())
    }

    @Test
    fun noExpirationTest() {
        onNoExpirationTest()
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
