package thekolo.de.widgetsforikeatradfri.ui.onboarding

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.paolorotolo.appintro.AppIntro


class OnboardingActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(WelcomeFragment.newInstance())
        addSlide(GatewayScanFragment.newInstance())
        addSlide(SecurityIdFragment.newInstance())

        setBarColor(Color.parseColor("#3F51B5"));

        showSkipButton(false)
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        println("onDonePressed")
    }
}