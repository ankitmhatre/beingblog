package being.test.app

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
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


import com.local.city.models.BlogItem

import com.wang.avi.AVLoadingIndicatorView

class BlogsFragment : Fragment() {

    internal var query = ""

    private lateinit var thats_embarassing: TextView
    private lateinit var avi: AVLoadingIndicatorView

    private var globalViewModel: GlobalViewModel? = null
    private lateinit var homeScreenOverflowIcon: ImageView

    //  private var verticlePagerAdapter: VerticlePagerAdapter? = null
    lateinit var blogAdapter: BlogAdapter
    lateinit var verticalViewPager: RecyclerView
    lateinit var navController: NavController
    lateinit var refreshView: SwipeRefreshLayout
    lateinit var apiInterface: ApiInterface
    lateinit var rootView: View
    private val TAG = BlogsFragment::class.java.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.blogs_fragment_layout, container, false)
        rootView = v
        settingUpIds(v)
        initSwipePager(v)
     
        return v
    }


    private fun initSwipePager(v: View) {

        //  verticlePagerAdapter = VerticlePagerAdapter(context!!, activity.application!!)
        verticalViewPager = v.findViewById<View>(R.id.vPager) as RecyclerView

        //   verticalViewPager.adapter = verticlePagerAdapter
        verticalViewPager.layoutManager = LinearLayoutManager(context)
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
        apiInterface = API.getClient().create(ApiInterface::class.java)


        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
       
        globalViewModel = ViewModelProviders.of(this).get(GlobalViewModel::class.java)
        blogAdapter = BlogAdapter(activity?.parent!!)
        Log.d("BlogsItemResponse", "$blogAdapter  ")

    }


    private fun settingUpIds(v: View) {
        thats_embarassing = v.findViewById<View>(R.id.thats_embarassing) as TextView
        refreshView = v.findViewById<View>(R.id.swipeRefreshBlogs) as SwipeRefreshLayout

        refreshView.setOnRefreshListener {
            refreshListCall()
        }
        thats_embarassing.visibility = View.GONE
        avi = v.findViewById<View>(R.id.avi) as AVLoadingIndicatorView
        avi.show()
        avi.visibility = View.VISIBLE
        //   homeScreenOverflowIcon = v.findViewById<View>(R.id.homeScreenOverflowIcon) as ImageView
//show_pinned_fragment


    }

    private fun refreshListCall() {
        if (Utilities.isNetworkAvailable(context)) {
            refreshView.isRefreshing = true
            val firestoreImplBase = FirebaseFirestore.getInstance()
            firestoreImplBase.collection("data/v1/news")
                .get()
                .addOnSuccessListener { result ->
                    if (result.size() > 0) {

                        for (obj in result) {

                            //Log.d(TAG, "timestamp check ${obj.data.get("reported_on")} ${Utilities.getTimestampFrom(10)}")
                            val tempMap: Map<String, Any> = obj.data
                            if (tempMap.get("isVerified") as Boolean && ((tempMap.get("reported_on") as Long) > Utilities.getTimestampFrom(10))) {

                                Log.d(TAG, " Verified")

                                var fullUrl: String? = null
                                val storage = FirebaseStorage.getInstance()
                                val listRef = storage.getReference(tempMap.get("media_url") as String)
                                listRef.downloadUrl.addOnSuccessListener {
                                    fullUrl = "${it.scheme}://${it.host}${it.encodedPath}?alt=media"


                                    val BlogsItem = BlogItem(tempMap.get("nid") as Long,
                                        fullUrl as String,
                                        tempMap.get("content") as String,
                                        tempMap.get("title") as String,
                                        tempMap.get("author_name") as String,
                                        tempMap.get("reported_on") as Long,
                                        tempMap.get("availability") as String,
                                        "${tempMap.get("category_id")}",
                                        tempMap.get("media_type") as String,
                                        0)

                                    if ((tempMap.get("availability") as String).equals("available")) {
                                        try {
                                            var fullUrl: String? = null
                                            val storage = FirebaseStorage.getInstance()
                                            val listRef = storage.getReference(tempMap.get("media_url") as String)
                                            listRef.downloadUrl.addOnSuccessListener {
                                                fullUrl = "${it.scheme}://${it.host}${it.encodedPath}?alt=media"


                                                val BlogsItem = BlogItem(tempMap.get("nid") as Long,
                                                    fullUrl as String,
                                                    tempMap.get("content") as String,
                                                    tempMap.get("title") as String,
                                                    tempMap.get("author_name") as String,
                                                    tempMap.get("reported_on") as Long,
                                                    tempMap.get("availability") as String,
                                                    "${tempMap.get("category_id")}",
                                                    tempMap.get("media_type") as String,
                                                    0)

                                                if ((tempMap.get("availability") as String).equals("available")) {
                                                    try {


                                                        GlobalRepository(activity?.application).insertBlog(BlogsItem)
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }

                                                } else {

                                                    try {
                                                        GlobalRepository(activity?.application).deleteSpecificBlog(BlogsItem.nid)
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
                                            GlobalRepository(activity?.application).deleteSpecificBlog(BlogsItem.nid)
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
                        val snackbar = Snackbar.make(activity?.parent!!.findViewById<View>(android.R.id.content), R.string.successfully_updated, Snackbar.LENGTH_SHORT)
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
                        val snackbar = Snackbar.make(activity?.parent!!.findViewById<View>(android.R.id.content), R.string.some_error, Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(activity?.parent!!.resources.getColor(android.R.color.holo_red_light))
                        snackbar.show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        } else {
            refreshView.isRefreshing = false
            try {
                val snackbar = Snackbar.make(activity?.parent!!.findViewById<View>(android.R.id.content), R.string.no_internet_conn, Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(activity?.parent!!.resources.getColor(android.R.color.holo_red_light))
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
                Log.d("Blogsss", Blogs[0].availability + "")
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