package it.scoppelletti.spaceship.ads.sample

import android.content.Intent
import it.scoppelletti.spaceship.ads.app.AbstractConsentActivity

class AdConsentActivity : AbstractConsentActivity() {

    override fun onComplete() {
        val intent: Intent

        intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }
}