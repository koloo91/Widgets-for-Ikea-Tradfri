package thekolo.de.widgetsforikeatradfri

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_donations.*
import org.sufficientlysecure.donations.DonationsFragment
import thekolo.de.quicktilesforikeatradfri.BuildConfig
import thekolo.de.quicktilesforikeatradfri.R

class DonationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donations)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        displayDonationsFragment()
    }

    private fun displayDonationsFragment() {
        val ft = supportFragmentManager.beginTransaction()

        val donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG, true, GOOGLE_PUBKEY, GOOGLE_CATALOG,
                resources.getStringArray(R.array.donation_google_catalog_values), false, null, null,
                null, false, null, null, false, null)

        ft.replace(R.id.fragment_container, donationsFragment, "donationsFragment")
        ft.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag("donationsFragment")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiCym60cI3MrIUpXiIcTudnFTOwc8c53CadvIQQkDAXeExysKiRF1rUvp567k7bAJKEuN6E1EuE4BW5fyKi2ydo0NrVGxgXj2tW4hh9Z1VWMh6nIeUJtJGxFI0/9n50QGEzUjOSQvaN/3T+7vGH0HBA0mOZ37QO0RZa08srK+VTWvzFNLilgM3/VeSEGfsc3zHJgm+HU+SiBSxl4M1HbgX0+T/qdBJvFYHWCXVC8GWKdaq/RI3k7l9+2h1Htq1vrGmRQKOzW7xo0urNCxCktfPlByWYMMKya1wX7FSuzQmvwR7fbxCvwcDhleTwFhvzRBKnXn+MQExse37P+XHuzGvwIDAQAB"
        private val GOOGLE_CATALOG = arrayOf("ntpsync.donation.1", "ntpsync.donation.2", "ntpsync.donation.3", "ntpsync.donation.5", "ntpsync.donation.8", "ntpsync.donation.13")
    }
}
