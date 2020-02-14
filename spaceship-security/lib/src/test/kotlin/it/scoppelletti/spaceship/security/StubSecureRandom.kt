@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import java.security.SecureRandom

object StubSecureRandom {

    fun create(): SecureRandom {
        val mock = Mockito.mock(SecureRandom::class.java)
        Mockito.doAnswer(::nextBytes)
                .`when`(mock).nextBytes(Mockito.isA(ByteArray::class.java))

        return mock
    }

    private fun nextBytes(invocation: InvocationOnMock): Any? {
        val bytes: ByteArray
        val args: Array<out Any?>

        args = invocation.arguments
        bytes = args[0] as ByteArray
        bytes.fill(9)
        return null
    }
}
