package being.test.app


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import being.test.app.repository.GlobalRepository
import being.test.app.viewmodel.GlobalViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import being.test.app.models.BlogItem
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.wang.avi.AVLoadingIndicatorView

class ListAllBlogs : AppCompatActivity() {

    internal var query = ""

    private lateinit var thats_embarassing: TextView
    private lateinit var search_blogs_et: EditText
    private lateinit var avi: AVLoadingIndicatorView

    private var globalViewModel: GlobalViewModel? = null
    private lateinit var homeScreenOverflowIcon: ImageView

    //  private var verticlePagerAdapter: VerticlePagerAdapter? = null
    lateinit var blogAdapter: BlogAdapter
    lateinit var verticalViewPager: RecyclerView
    lateinit var navController: NavController
    lateinit var refreshView: SwipeRefreshLayout
    lateinit var favoritesIcon: ImageButton
    lateinit var logoutButton: ImageButton
    lateinit var apiInterface: ApiInterface
    lateinit var rootView: View
    private val TAG = ListAllBlogs::class.java.simpleName


    private fun initSwipePager() {

        //  verticlePagerAdapter = VerticlePagerAdapter(context!!, activity.application!!)
        verticalViewPager = findViewById<View>(R.id.vPager) as RecyclerView

        //   verticalViewPager.adapter = verticlePagerAdapter
        verticalViewPager.layoutManager = LinearLayoutManager(this)
        verticalViewPager.adapter = blogAdapter
        Log.d("BlogsItemResponse", "$blogAdapter  ")

    }

    override fun onResume() {
        super.onResume()
        populateListArray("")
        refreshListCall()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blogs_fragment_layout)



        apiInterface = API.getClient().create(ApiInterface::class.java)
        globalViewModel = ViewModelProviders.of(this).get(GlobalViewModel::class.java)
        blogAdapter = BlogAdapter(this@ListAllBlogs)
        Log.d("BlogsItemResponse", "$blogAdapter  ")
        settingUpIds()
        initSwipePager()
    }


    private fun settingUpIds() {
        thats_embarassing = findViewById<View>(R.id.thats_embarassing) as TextView
        search_blogs_et = findViewById<View>(R.id.search_blogs_et) as EditText
        refreshView = findViewById<View>(R.id.swipeRefreshBlogs) as SwipeRefreshLayout
        favoritesIcon =  findViewById<ImageButton>(R.id.favoritesIcon) as ImageButton
        logoutButton =  findViewById<ImageButton>(R.id.logoutButton) as ImageButton
        favoritesIcon.setOnClickListener {
            startActivity(Intent(this@ListAllBlogs, ShowFavoritesActivity::class.java))
        }
        logoutButton.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.Logout)
                message(R.string.are_your_sure_logout)
                positiveButton(R.string.logout) { dialog ->
                   val a =  FirebaseAuth.getInstance()
                a.signOut()
                    PrefUtils.setString(this@ListAllBlogs, PrefKeys.USER_ACC_TYPE, null)
                    startActivity(Intent(this@ListAllBlogs, LoginActivity::class.java))
                }
                negativeButton(R.string.cancel) { dialog ->
                    dismiss()
                }
            }

        }
search_blogs_et.addTextChangedListener(object : TextWatcher{
    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        populateListArray(p0.toString())
    }
})
        search_blogs_et.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(p0: View?, event: MotionEvent): Boolean {

                val DRAWABLE_RIGHT = 2

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search_blogs_et.getRight() - search_blogs_et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        search_blogs_et.text.clear()

                        return true;
                    }
                }
                return false;

            }
        })
        refreshView.setOnRefreshListener {
            refreshListCall()
        }
        thats_embarassing.visibility = View.GONE
        avi = findViewById<View>(R.id.avi) as AVLoadingIndicatorView
        avi.show()
        avi.visibility = View.VISIBLE
        //   homeScreenOverflowIcon = v.findViewById<View>(R.id.homeScreenOverflowIcon) as ImageView
//show_pinned_fragment


    }

    private fun refreshListCall() {
        if (Utilities.isNetworkAvailable(this)) {
            refreshView.isRefreshing = true
            val firestoreImplBase = FirebaseFirestore.getInstance()
            firestoreImplBase.collection("data/v1/blogs")
                .get()
                .addOnSuccessListener { result ->
                    if (result.size() > 0) {

                        for (obj in result) {

                            //Log.d(TAG, "timestamp check ${obj.data.get("reported_on")} ${Utilities.getTimestampFrom(10)}")
                            val tempMap: Map<String, Any> = obj.data
                            if ((tempMap.get("reported_on") as Long) > Utilities.getTimestampFrom(100)
                            ) {

                                Log.d(TAG, " Verified")

                                var fullUrl: String? = null
                                val storage = FirebaseStorage.getInstance()
                                val listRef =
                                    storage.getReference(tempMap.get("media_url") as String)
                                listRef.downloadUrl.addOnSuccessListener {
                                    fullUrl = "${it.scheme}://${it.host}${it.encodedPath}?alt=media"


                                    val blogItem = BlogItem(
                                        tempMap.get("blog_id") as Long,
                                        fullUrl as String,
                                        tempMap.get("content") as String,
                                        tempMap.get("title") as String,
                                        tempMap.get("author_name") as String,
                                        tempMap.get("reported_on") as Long,
                                        tempMap.get("availability") as Boolean,
                                        0
                                    )

                                    if (tempMap.get("availability") as Boolean) {
                                        try {
                                            var fullUrl: String? = null
                                            val storage = FirebaseStorage.getInstance()
                                            val listRef =
                                                storage.getReference(tempMap.get("media_url") as String)
                                            listRef.downloadUrl.addOnSuccessListener {
                                                fullUrl =
                                                    "${it.scheme}://${it.host}${it.encodedPath}?alt=media"


                                                val blogItem = BlogItem(
                                                    tempMap.get("blog_id") as Long,
                                                    fullUrl as String,
                                                    tempMap.get("content") as String,
                                                    tempMap.get("title") as String,
                                                    tempMap.get("author_name") as String,
                                                    tempMap.get("reported_on") as Long,
                                                    tempMap.get("availability") as Boolean,
                                                    0
                                                )

                                                if (tempMap.get("availability") as Boolean) {
                                                    try {


                                                        GlobalRepository(application).insertBlog(
                                                            blogItem
                                                        )
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }

                                                } else {

                                                    try {
                                                        GlobalRepository(application).deleteSpecificBlog(
                                                            blogItem.blog_id
                                                        )
                                                    } catch (e: Exception) {
                                                        Log.d("Blogss", e.toString())
                                                    }

                                                }


                                            }.addOnFailureListener {

                                            }


                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    } else {

                                        try {
                                            GlobalRepository(application).deleteSpecificBlog(
                                                blogItem.blog_id
                                            )
                                        } catch (e: Exception) {
                                            Log.d("Blogss", e.toString())
                                        }

                                    }


                                }.addOnFailureListener {
                                    Log.d("ERROR", "${it.message}")
                                }
                                //GlobalRepository(application).insertContact(c)
                            } else {
                                Log.d(TAG, "Not Verified")
                            }
                        }
                    }
                    refreshView.isRefreshing = false
                    try {
                        val snackbar = Snackbar.make(
                            findViewById<View>(android.R.id.content),
                            R.string.successfully_updated,
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                        snackbar.show()
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                }
                .addOnFailureListener { error ->
                    Log.d(TAG, "${error.message}")
                    refreshView.isRefreshing = false
                    try {
                        val snackbar = Snackbar.make(
                            findViewById<View>(android.R.id.content),
                            R.string.some_error,
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_red_light))
                        snackbar.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        } else {
            refreshView.isRefreshing = false
            try {
                val snackbar = Snackbar.make(
                    findViewById<View>(android.R.id.content),
                    R.string.no_internet_conn,
                    Snackbar.LENGTH_LONG
                )
                snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_red_light))
                snackbar.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun populateListArray(filter: String) {

        globalViewModel!!.getBlogLiveList(filter).observeForever { Blogs ->
            if (Blogs.isNotEmpty()) {
                thats_embarassing.visibility = View.GONE
                avi.hide()
                avi.visibility = View.GONE
                verticalViewPager.visibility = View.VISIBLE
                Log.d("Blogsss", "${Blogs[0].availability}")
                blogAdapter.update(Blogs)
                //verticlePagerAdapter!!.update(Blogs)

            } else {
                avi.hide()
                avi.visibility = View.GONE
                thats_embarassing.visibility = View.VISIBLE
            }
        }


    }


    companion object {
        private val HIDE_THRESHOLD = 20
    }

}
