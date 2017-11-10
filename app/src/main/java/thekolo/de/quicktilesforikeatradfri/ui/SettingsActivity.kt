package thekolo.de.quicktilesforikeatradfri.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import thekolo.de.quicktilesforikeatradfri.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .commit()
    }
}
