
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.consent.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ads.consent.ConsentForm
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import it.scoppelletti.spaceship.ads.AdsExt
import it.scoppelletti.spaceship.ads.DefaultAdListener
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.types.trimRaw
import kotlinx.android.synthetic.main.main_activity.*
import mu.KotlinLogging
import java.net.URL

class MainActivity : AppCompatActivity(), OnDialogResultListener,
        ConsentInfoUpdateListener {

    private lateinit var consentInfo: ConsentInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)

        MobileAds.initialize(applicationContext, BuildConfig.ADS_APPID)

        consentInfo = ConsentInformation.getInstance(this)
        consentInfo.requestConsentInfoUpdate(arrayOf(
                BuildConfig.ADS_PUBLISHERID), this)
    }

    override fun onConsentInfoUpdated(consentStatus: ConsentStatus?) {
        val msg: String
        val form: ConsentForm
        val formListener: DefaultConsentFormListener

        if (consentStatus == null) {
            logger.error("Argument consent status is null.")
            msg = resources.getString(R.string.err_consentLoad,
                    "Argument consent status is null.")
            Snackbar.make(contentFrame, msg, Snackbar.LENGTH_LONG).show()
            return
        }

        if (!consentInfo.isRequestLocationInEeaOrUnknown) {
            logger.info(
                    "The user is not located in the European Economic Area.")
            onConsentResult(ConsentStatus.PERSONALIZED)
            return
        }

        logger.debug { """The user is located in the European Economic Area
            |and the consent status is $consentStatus.""".trimRaw() }
        if (consentStatus in arrayOf(ConsentStatus.PERSONALIZED,
                        ConsentStatus.NON_PERSONALIZED)) {
            onConsentResult(consentStatus)
            return
        }

        formListener = DefaultConsentFormListener(::onConsentResult,
                ::onConsentError)
        form = ConsentForm.Builder(this, URL("http://www.scoppelletti.it"))
                .withListener(formListener)
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build()

        formListener.onLoad = {
            form.show()
        }

        form.load()
    }

    private fun onConsentResult(consentStatus: ConsentStatus?)
    {
        val adView: AdView
        val adBuilder: AdRequest.Builder
        val extra: Bundle

        adBuilder = AdRequest.Builder()

        when (consentStatus) {
            ConsentStatus.PERSONALIZED -> {
                // NOP
            }

            ConsentStatus.NON_PERSONALIZED -> {
                extra = Bundle()
                extra.putString(AdsExt.PROP_NPA, AdsExt.NPA_TRUE)
                adBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java,
                        extra)
            }

            else -> { // ConsentStatus.UNKNONW
                return
            }
        }

        adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = BuildConfig.ADS_UNITID
        adFrame.addView(adView)

        adView.adListener = DefaultAdListener()
        adView.loadAd(adBuilder.build())
    }

    private fun onConsentError(reason: String?) {
        val msg: String

        msg = resources.getString(R.string.err_consentForm, reason)
        Snackbar.make(contentFrame, msg, Snackbar.LENGTH_LONG).show()
    }

    override fun onFailedToUpdateConsentInfo(reason: String?) {
        val msg: String

        logger.error { "Failed to load data to adhere the EU GDPR: $reason." }
        msg = resources.getString(R.string.err_consentLoad, reason)
        Snackbar.make(contentFrame, msg, Snackbar.LENGTH_LONG).show()
    }

    override fun onDialogResult(tag: String, which: Int) {
        when (tag) {
            ExceptionDialogFragment.TAG -> {
                tryFinish()
            }
        }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}