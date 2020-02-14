@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security

import it.scoppelletti.spaceship.types.joinLines
import mu.KotlinLogging
import java.lang.UnsupportedOperationException
import java.security.AlgorithmParameters
import java.security.Key
import java.security.SecureRandom
import java.security.Security
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.CipherSpi
import javax.crypto.NullCipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.SecretKeySpec

private val PROVIDER = Security.getProvider(SecurityExtTest.PROVIDER_SUN)
private val logger = KotlinLogging.logger {}

class FakeCipher(
        transformation: String
) : Cipher(FakeCipherSpi(), PROVIDER, transformation)

class FakeCipherSpi : CipherSpi() {
    private val cipher: Cipher = NullCipher()

    override fun engineInit(opmode: Int, key: Key?, random: SecureRandom?) {
        cipher.init(opmode, key, random)
    }

    override fun engineInit(
            opmode: Int,
            key: Key?,
            params: AlgorithmParameterSpec?,
            random: SecureRandom?
    ) {
        cipher.init(opmode, key, params, random)
    }

    override fun engineInit(
            opmode: Int,
            key: Key?,
            params: AlgorithmParameters?,
            random: SecureRandom?
    ) {
        cipher.init(opmode, key, params, random)
    }

    override fun engineSetMode(mode: String?) {
        logger.debug { "engineSetMode(mode=$mode)" }
    }

    override fun engineSetPadding(padding: String?) {
        logger.debug { "engineSetPadding(padding=$padding)" }
    }

    override fun engineGetParameters(): AlgorithmParameters = cipher.parameters

    override fun engineGetBlockSize(): Int = cipher.blockSize

    override fun engineGetIV(): ByteArray = cipher.iv

    override fun engineGetOutputSize(inputLen: Int): Int = inputLen

    override fun engineUpdate(
            input: ByteArray?,
            inputOffset: Int,
            inputLen: Int
    ): ByteArray = input?.copyOfRange(inputOffset, inputOffset + inputLen) ?:
            ByteArray(0)

    override fun engineUpdate(
            input: ByteArray?,
            inputOffset: Int,
            inputLen: Int,
            output: ByteArray?,
            outputOffset: Int
    ): Int {
        if (input == null || output == null) {
            return 0
        }

        for (i in 0 until inputLen) {
            output[outputOffset + i] = input[inputOffset + i]
        }

        return inputLen
    }

    override fun engineDoFinal(
            input: ByteArray?,
            inputOffset: Int,
            inputLen: Int
    ): ByteArray = input?.copyOfRange(inputOffset, inputOffset + inputLen) ?:
            ByteArray(0)

    override fun engineDoFinal(
            input: ByteArray?,
            inputOffset: Int,
            inputLen: Int,
            output: ByteArray?,
            outputOffset: Int
    ): Int {
        if (input == null || output == null) {
            return 0
        }

        for (i in 0 until inputLen) {
            output[outputOffset + i] = input[inputOffset + i]
        }

        return inputLen
    }

    override fun engineWrap(key: Key?): ByteArray {
        // NullCipher does not implement wrap

        if (key !is SecretKey) {
            throw UnsupportedOperationException("""Wrapped key of type
                |${key?.javaClass?.simpleName} not supported.""".trimMargin()
                    .joinLines())
        }

        return key.encoded
    }

    override fun engineUnwrap(
            wrappedKey: ByteArray?,
            wrappedKeyAlgorithm: String?,
            wrappedKeyType: Int): Key {
        // NullCipher does not implement unwrap
        val keyFactory: SecretKeyFactory
        val keySpec: KeySpec

        if (wrappedKeyType != Cipher.SECRET_KEY) {
            throw UnsupportedOperationException(
                    "Wrapped key of type $wrappedKeyType not supported.")
        }

        keyFactory = SecretKeyFactory.getInstance(
                SecurityExtTest.KEY_ALGORITHM_DES, PROVIDER)
        keySpec = SecretKeySpec(wrappedKey, SecurityExtTest.KEY_ALGORITHM_DES)
        return keyFactory.generateSecret(keySpec)
    }
}
