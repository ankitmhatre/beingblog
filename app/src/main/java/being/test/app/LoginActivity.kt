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
            startActivityForResult(Intent(this@LoginActivity, DashboardHome::class.java), 990)
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


//        val rplogin = API.getClient().create(ApiInterface::class.java).reporterLogin(uname, pass)
//        rplogin.enqueue(object : Callback<ReportersLoginReponse> {
//            override fun onFailure(call: Call<ReportersLoginReponse>, t: Throwable) {
//                Log.d("reportersResponse", t.message)
//                toggleVisibility()
//            }
//
//            override fun onResponse(call: Call<ReportersLoginReponse>, response: Response<ReportersLoginReponse>) {
//                toggleVisibility()
//                if (response.body()!!.status.equals("success")) {
//                    //success
//                    val b = Intent(this@LoginActivity, ReportersArticleActivity::class.java)
//                    b.putExtra("status", response.body()!!.status)
//                    b.putExtra("username", response.body()!!.username)
//                    b.putExtra("id", response.body()!!.id)
//                    b.putExtra("acc_type", response.body()!!.acc_type)
//                    b.putExtra("full_name", response.body()!!.full_name)
//                    b.putExtra("token", response.body()!!.token)
//                    b.putExtra("valid_till", response.body()!!.valid_till)
//
//                    PrefUtils.setString(this@LoginActivity, "reporter_uname", uname)
//                    PrefUtils.setString(this@LoginActivity, "reporter_pass", pass)
//
//
//                    startActivityForResult(b, 990)
//                } else {
//                    //fail
//                    Snackbar.make(findViewById(android.R.id.content),
//                            getString(R.string.failed), Snackbar.LENGTH_SHORT).show()
//                    PrefUtils.setString(this@LoginActivity, "reporter_uname", "")
//                    PrefUtils.setString(this@LoginActivity, "reporter_pass", "")
//
//
//                }
//
//            }
//        })
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
                        // Create a new user with a first and last name

                        // Create a new user with a first and last name
                        // Create a new user with a first and last name
                        val firestoreUserData = hashMapOf(
                            "uid" to user!!.uid,
                            "name" to user.displayName,
                            "email" to user.email,
                            "role" to role
                        )


// Add a new document with a generated ID

// Add a new document with a generated ID
                        db.collection("users")
                            .add(firestoreUserData)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: " + documentReference.id
                                )
//TODO("Handle all scenarios here")
                                startActivityForResult(
                                    Intent(
                                        this@LoginActivity,
                                        DashboardHome::class.java
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
