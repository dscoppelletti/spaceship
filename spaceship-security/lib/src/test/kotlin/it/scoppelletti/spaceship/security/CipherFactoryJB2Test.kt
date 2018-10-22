package it.scoppelletti.spaceship.security

import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class CipherFactoryJB2Test : AbstractCipherFactoryTest() {

    @BeforeTest
    fun before() {
        onBefore()

        Mockito.`when`(resources.getInteger(
                R.integer.it_scoppelletti_security_keyLen)).thenReturn(16)

        Mockito.`when`(resources.getString(
                R.string.it_scoppelletti_security_aesMode)).thenReturn(
                "AES/ECB/PKCS7Padding")

        Mockito.`when`(resources.getString(
                R.string.it_scoppelletti_security_rsaMode)).thenReturn(
                "RSA/ECB/PKCS1Padding")

        cipherFactory = CipherFactoryJellyBeanMR2(resources, ioProvider,
                timeProvider, random, cipherProvider, DefaultCipherIOProvider())
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
