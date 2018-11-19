package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.io.FakeIOProvider
import it.scoppelletti.spaceship.io.IOProvider
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class CryptoProviderJB2Test : AbstractCipherFactoryTest() {

    private lateinit var ioProvider: IOProvider

    @BeforeTest
    fun setUp() {
        onSetUp()

        ioProvider = FakeIOProvider()
        cryptoProvider = CryptoProviderJellyBeanMR2(ioProvider,
                timeProvider, random, securityBridge)
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
