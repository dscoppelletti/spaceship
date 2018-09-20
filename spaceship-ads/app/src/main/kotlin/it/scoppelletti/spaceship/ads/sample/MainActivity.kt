package it.scoppelletti.spaceship.ads.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import it.scoppelletti.spaceship.ads.DefaultAdListener
import it.scoppelletti.spaceship.ads.createAdRequestBuilder
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        val adRequest: AdRequest

        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)

        MobileAds.initialize(applicationContext, BuildConfig.ADS_APPID)

        adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = BuildConfig.ADS_UNITID
        adFrame.addView(adView)

        adView.adListener = DefaultAdListener()
        adRequest = createAdRequestBuilder().build()
        adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}
