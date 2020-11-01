package being.test.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_layout)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            /**
             * User is signed in
             */






                Handler().postDelayed({ startActivityForResult(Intent(this@SplashScreen, ListAllBlogs::class.java), loginCode) }, 1500)


        } else {
            /**
             * User is not signed in
             */

            Handler().postDelayed({ startActivityForResult(Intent(this@SplashScreen, LoginActivity::class.java),
                loginCode) }, 2000)


        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {


        when (requestCode) {
            loginCode -> {
                if (resultCode == Activity.RESULT_OK) {
                    finish()
                }
            }
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val loginCode = 8971
    }
}