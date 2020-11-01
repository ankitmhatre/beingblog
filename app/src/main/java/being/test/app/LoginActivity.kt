package being.test.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {

    lateinit var pb: ProgressBar
    lateinit var login_btn: SignInButton
    lateinit var usersRadioGroup: RadioGroup
    lateinit var apiInterface: ApiInterface
    lateinit var link_signup: TextView
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val RC_SIGN_IN = 32
    private val TAG = LoginActivity::class.java.simpleName
    val user = FirebaseAuth.getInstance().currentUser
    var role: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        apiInterface = API.getClient().create(ApiInterface::class.java)


        setContentView(R.layout.login_layout)
        pb = findViewById(R.id.reporters_login_progress_bar)
        usersRadioGroup = findViewById(R.id.usersRadioGroup)
        usersRadioGroup.setOnCheckedChangeListener { radioGroup, i ->

            when (i) {
                R.id.radio0 -> {
role = "admin"
                }
                R.id.radio1 ->{
                    role = "user"
                }
            }
            Log.d("ListenRadio", "" + role)
        }
        login_btn = findViewById(R.id.sign_in_button)


        val providers = arrayListOf(
            // AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        if (user != null) {
            // User is signed in
            toggleVisibility()



                startActivityForResult(
                    Intent(
                        this@LoginActivity,
                        ListAllBlogs::class.java
                    ), 990
                )



        }



        login_btn.setOnClickListener {

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN)

        }

//        if (!PrefUtils.getString(this, "reporter_uname", "").equals("")) {
//            signInUser(PrefUtils.getString(this, "reporter_uname", ""), PrefUtils.getString(this, "reporter_pass", ""))
//        }
    }


    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun signInUser(uname: String, pass: String) {
        toggleVisibility()
        hideKeyboard(this)

  }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            990 -> {
                finish()
            }
            RC_SIGN_IN -> {

                val response = IdpResponse.fromResultIntent(data)

                if (response != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        // Successfully signed in
                        val user = FirebaseAuth.getInstance().currentUser
                        Log.d(TAG, "user = $user")

                        val firestoreUserData = hashMapOf(
                            "uid" to user!!.uid,
                            "name" to user.displayName,
                            "email" to user.email,
                            "role" to role,
                            "status" to "active"
                        )

                        db.collection("users")
                            .document(user.uid)
                            .set(firestoreUserData)
                            .addOnSuccessListener { documentReference ->


                                PrefUtils.setString(this@LoginActivity, PrefKeys.USER_ACC_TYPE,  role)


                                    startActivityForResult(
                                        Intent(
                                            this@LoginActivity,
                                            ListAllBlogs::class.java
                                        ), 990
                                    )




                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }





                        // ...
                    } else {
                        Log.d(TAG, "error ${response.error!!.errorCode}")
                        // Sign in failed. If response is null the user canceled the
                        // sign-in flow using the back button. Otherwise check
                        // response.getError().getErrorCode() and handle the error.
                        // ...
                        finish()
                    }
                }else{
                    Log.d(TAG, "no response")
                    finish()

                }
            }
        }
    }

    fun toggleVisibility() {
        if (pb.visibility != View.VISIBLE) {
            pb.visibility = View.VISIBLE
            login_btn.visibility = View.GONE
        } else {
            pb.visibility = View.GONE
            login_btn.visibility = View.VISIBLE
        }
    }
}
