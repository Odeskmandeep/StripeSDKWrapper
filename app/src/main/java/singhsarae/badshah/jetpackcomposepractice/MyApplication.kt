package singhsarae.badshah.jetpackcomposepractice

import android.app.Application
import singhsarae.badshah.stripem2.creepySteam.StripeManager

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        //Initialization Stripe:
        StripeManager.init(this)
    }

}