package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.io.FakeIOProvider
import it.scoppelletti.spaceship.io.IOProvider
import kotlin.test.BeforeTest
import kotlin.test.Test

class CryptoProviderJB2Test : AbstractCryptoProviderTest() {

    private lateinit var ioProvider: IOProvider

    @BeforeTest
    fun setUp() {
        onSetUp()

        ioProvider = FakeIOProvider()
        cryptoProvider = CryptoProviderJellyBeanMR2(ioProvider, clock, random,
                securityBridge)
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
