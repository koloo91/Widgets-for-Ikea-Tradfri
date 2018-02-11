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
        private val GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg8bTVFK5zIg4FGYkHKKQ/j/iGZQlXU0qkAv2BA6epOX1ihbMz78iD4SmViJlECHN8bKMHxouRNd9pkmQKxwEBHg5/xDC/PHmSCXFx/gcY/xa4etA1CSfXjcsS9i94n+j0gGYUg69rNkp+p/09nO9sgfRTAQppTxtgKaXwpfKe1A8oqmDUfOnPzsEAG6ogQL6Svo6ynYLVKIvRPPhXkq+fp6sJ5YVT5Hr356yCXlM++G56Pk8Z+tPzNjjvGSSs/MsYtgFaqhPCsnKhb55xHkc8GJ9haq8k3PSqwMSeJHnGiDq5lzdmsjdmGkWdQq2jIhKlhMZMm5VQWn0T59+xjjIIwIDAQAB"
        private val GOOGLE_CATALOG = arrayOf("ntpsync.donation.1", "ntpsync.donation.2", "ntpsync.donation.3", "ntpsync.donation.5", "ntpsync.donation.8", "ntpsync.donation.13")
    }
}
