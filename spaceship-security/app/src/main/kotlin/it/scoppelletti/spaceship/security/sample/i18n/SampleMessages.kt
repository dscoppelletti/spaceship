package it.scoppelletti.spaceship.security.sample.i18n

import android.content.res.Resources
import it.scoppelletti.spaceship.i18n.AndroidResourceMessageSpec
import it.scoppelletti.spaceship.i18n.MessageSpec
import it.scoppelletti.spaceship.security.sample.R
import javax.inject.Inject

class SampleMessages @Inject constructor(
        private val resources: Resources
) {

    fun errorCipherTextCorrupted(): MessageSpec =
            AndroidResourceMessageSpec(resources,
                    R.string.err_ciphertext_corrupted)
}
