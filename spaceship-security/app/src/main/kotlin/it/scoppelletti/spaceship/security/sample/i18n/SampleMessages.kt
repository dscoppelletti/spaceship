package it.scoppelletti.spaceship.security.sample.i18n

import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.security.sample.R

object SampleMessages {

    fun errorCipherTextCorrupted(): MessageSpec =
            AndroidResourceMessageSpec(R.string.err_ciphertext_corrupted)
}
