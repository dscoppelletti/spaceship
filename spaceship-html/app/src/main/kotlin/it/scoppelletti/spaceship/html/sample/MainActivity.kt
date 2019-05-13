
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.html.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.html.app.HtmlViewerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val intent: Intent

        super.onCreate(savedInstanceState)

        intent = Intent(applicationContext, HtmlViewerActivity::class.java)
        intent.putExtra(HtmlViewerActivity.PROP_TEXT, R.string.html_text)
        intent.putExtra(HtmlViewerActivity.PROP_TITLE, R.string.html_title)
        startActivity(intent)
        tryFinish()
    }
}