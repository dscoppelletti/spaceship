
package it.scoppelletti.spaceship.http.sample

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import it.scoppelletti.spaceship.app.tryFinish
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBar: ActionBar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                tryFinish()
                return true
            }

            R.id.cmd_network_check -> {
                onNetworkCheckClick()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onNetworkCheckClick() {
        val msgId: Int
        val connMgr: ConnectivityManager
        val networkInfo: NetworkInfo?

        connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager
        networkInfo = connMgr.activeNetworkInfo
        msgId = if (networkInfo != null && networkInfo.isConnected)
            R.string.msg_network_connected
        else R.string.msg_network_notConnected

        Snackbar.make(contentFrame, msgId, Snackbar.LENGTH_SHORT).show()
    }
}