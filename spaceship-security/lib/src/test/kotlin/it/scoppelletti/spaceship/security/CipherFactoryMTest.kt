package it.scoppelletti.spaceship.security

import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class CipherFactoryMTest : AbstractCipherFactoryTest() {

    @BeforeTest
    fun before() {
        onBefore()

        Mockito.`when`(resources.getInteger(
                R.integer.it_scoppelletti_security_ivLen)).thenReturn(12)

        Mockito.`when`(resources.getInteger(
                R.integer.it_scoppelletti_security_keyLen)).thenReturn(128)

        Mockito.`when`(resources.getString(
                R.string.it_scoppelletti_security_aesMode)).thenReturn(
                "AES/GCM/NoPadding")

        cipherFactory = CipherFactoryMarshmallow(resources, ioProvider, random,
                cipherProvider, DefaultCipherIOProvider())
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