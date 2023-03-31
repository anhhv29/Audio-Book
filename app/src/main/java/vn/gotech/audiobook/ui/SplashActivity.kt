package vn.gotech.audiobook.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import vn.gotech.audiobook.databinding.ActivitySplashBinding
import vn.gotech.audiobook.ui.home.HomeActivity

class SplashActivity : vn.gotech.audiobook.base.BaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 5000)
    }

    override fun inflateLayout(layoutInflater: LayoutInflater) =
        ActivitySplashBinding.inflate(layoutInflater)
}
