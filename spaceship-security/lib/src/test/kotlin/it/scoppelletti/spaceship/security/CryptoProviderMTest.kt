package it.scoppelletti.spaceship.security

import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class CryptoProviderMTest : AbstractCipherFactoryTest() {

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