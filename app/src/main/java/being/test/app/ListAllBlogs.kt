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
import being.test.app.firestoreutils.FirebaseFunctions
import being.test.app.firestoreutils.FirebaseFunctionsResponse
import being.test.app.models.BlogItem
import being.test.app.viewmodel.GlobalViewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.wang.avi.AVLoadingIndicatorView
import org.json.JSONObject
import java.lang.reflect.Type

class ListAllBlogs : AppCompatActivity(), FirebaseFunctionsResponse {

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
    lateinit var createBlogButton: FloatingActionButton
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blogs_fragment_layout)



        apiInterface = API.getClient().create(ApiInterface::class.java)
        globalViewModel = ViewModelProviders.of(this).get(GlobalViewModel::class.java)
        blogAdapter = BlogAdapter(this@ListAllBlogs)
        Log.d("BlogsItemResponse", "$blogAdapter  ")
        settingUpIds()
        initSwipePager()

        FirebaseFunctions().getDataFromFirestoreDatabase(
            this@ListAllBlogs,
            "data/v1/blogs"
            )
    }



    private fun settingUpIds() {
        thats_embarassing = findViewById<View>(R.id.thats_embarassing) as TextView
        search_blogs_et = findViewById<View>(R.id.search_blogs_et) as EditText
        refreshView = findViewById<View>(R.id.swipeRefreshBlogs) as SwipeRefreshLayout
        favoritesIcon = findViewById<ImageButton>(R.id.favoritesIcon) as ImageButton
        logoutButton = findViewById<ImageButton>(R.id.logoutButton) as ImageButton
        createBlogButton =
            findViewById<FloatingActionButton>(R.id.createBlogButton) as FloatingActionButton
        createBlogButton.setOnClickListener {
            startActivity(Intent(this@ListAllBlogs, WriteBlog::class.java))
        }
        if (PrefUtils.getString(this@ListAllBlogs, PrefKeys.USER_ACC_TYPE, null).equals("admin")) {
            createBlogButton.visibility = View.VISIBLE
            favoritesIcon.visibility = View.GONE

        } else {
            createBlogButton.visibility = View.GONE
            favoritesIcon.visibility = View.VISIBLE
        }
        favoritesIcon.setOnClickListener {
            startActivity(Intent(this@ListAllBlogs, ShowFavoritesActivity::class.java))
        }
        logoutButton.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.Logout)
                message(R.string.are_your_sure_logout)
                positiveButton(R.string.logout) { dialog ->
                    val a = FirebaseAuth.getInstance()
                    a.signOut()
                    PrefUtils.setString(this@ListAllBlogs, PrefKeys.USER_ACC_TYPE, null)
                    startActivity(Intent(this@ListAllBlogs, LoginActivity::class.java))
                    finish()
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
//        populateListArray(p0.toString())
        thats_embarassing.visibility = View.GONE
        avi = findViewById<View>(R.id.avi) as AVLoadingIndicatorView
        avi.show()
        avi.visibility = View.VISIBLE

        FirebaseFunctions().searchDataFromFirestoreDatabase(
            this@ListAllBlogs,
            "data/v1/blogs",
            p0.toString()
        )

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
            thats_embarassing.visibility = View.GONE
            avi = findViewById<View>(R.id.avi) as AVLoadingIndicatorView
            avi.show()
            avi.visibility = View.VISIBLE

            FirebaseFunctions().getDataFromFirestoreDatabase(
                this@ListAllBlogs,
                "data/v1/blogs"
            )
        }
        thats_embarassing.visibility = View.GONE
        avi = findViewById<View>(R.id.avi) as AVLoadingIndicatorView
        avi.show()
        avi.visibility = View.VISIBLE
        //   homeScreenOverflowIcon = v.findViewById<View>(R.id.homeScreenOverflowIcon) as ImageView
//show_pinned_fragment


    }





    companion object {
        private val HIDE_THRESHOLD = 20
    }

    override fun firebaseFunctionsResponse(jsonObject: JSONObject?, type: String?) {
        if (jsonObject != null) {

            try {
                val gson = GsonBuilder().create()
                var Blogs = mutableListOf<BlogItem>()
                val groupListType: Type = object : TypeToken<ArrayList<BlogItem?>?>() {}.getType()
                val model = gson.fromJson(
                    jsonObject.getJSONArray("result").toString(),
                    groupListType
                ) as ArrayList<BlogItem>;
                Blogs.addAll(model)

                blogAdapter.update(mutableListOf<BlogItem>())


                if (Blogs.isNotEmpty()) {
                    thats_embarassing.visibility = View.GONE
                    avi.hide()
                    avi.visibility = View.GONE
                    verticalViewPager.visibility = View.VISIBLE
                    Log.d("blogs from fbstore", "${Blogs}")

                    blogAdapter.update(Blogs)
                    //verticlePagerAdapter!!.update(Blogs)
                }else{
                    blogAdapter.update(mutableListOf<BlogItem>())
                    thats_embarassing.visibility = View.VISIBLE
                    verticalViewPager.visibility = View.VISIBLE
                    avi.visibility = View.GONE
                    avi.hide()
                }
            } catch (e: Exception) {
                blogAdapter.update(mutableListOf<BlogItem>())
                e.printStackTrace()
                avi.hide()
                verticalViewPager.visibility = View.GONE
                avi.visibility = View.GONE
                thats_embarassing.visibility = View.VISIBLE
            }
        } else {
            avi.hide()
            avi.visibility = View.GONE
            thats_embarassing.visibility = View.VISIBLE
        }

        refreshView.isRefreshing = false
    }

    override fun dataAddSuccess(successful: Boolean) {

    }

    override fun dataUpdateSuccess(successful: Boolean) {

    }

    override fun checkFavorite(isFavorite: Boolean) {

    }

    override fun toggleFavorite(isFavorite: Boolean) {

    }
}
