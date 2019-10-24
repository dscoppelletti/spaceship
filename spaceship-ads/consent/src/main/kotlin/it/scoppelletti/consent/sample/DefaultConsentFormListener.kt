package it.scoppelletti.consent.sample

import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.types.joinLines
import mu.KLogger
import mu.KotlinLogging

class DefaultConsentFormListener(
        private val onResult: (ConsentStatus?) -> Unit,
        private val onError: (String?) -> Unit
) : ConsentFormListener() {

    var onLoad: (() -> Unit)? = null

    override fun onConsentFormLoaded() {
        logger.debug("Consent form loaded successfully.")
        onLoad?.invoke()
    }

    override fun onConsentFormOpened() {
        logger.debug("Consent form was displayed.")
    }

    override fun onConsentFormClosed(
            consentStatus: ConsentStatus?,
            userPrefersAdFree: Boolean?)
    {
        logger.info {
            """Consent form was closed (consentStatus: $consentStatus,
            |userPrefersAdFree: $userPrefersAdFree).""".trimMargin().joinLines()
        }

        onResult(consentStatus)
    }

    override fun onConsentFormError(reason: String?) {
        logger.error { "Consent form error: $reason."}
        onError(reason)
    }

    private companion object {
        val logger: KLogger = KotlinLogging.logger {}
    }
}
