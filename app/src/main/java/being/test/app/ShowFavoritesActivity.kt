package being.test.app


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import being.test.app.firestoreutils.FirebaseFunctions
import being.test.app.firestoreutils.FirebaseFunctionsResponse
import being.test.app.models.BlogItem
import being.test.app.viewmodel.GlobalViewModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type


class ShowFavoritesActivity : AppCompatActivity(), FirebaseFunctionsResponse {
    lateinit var recyclerView: RecyclerView
    lateinit var rabiit: ImageView
    lateinit var newsAdapterOld: BlogAdapter
    lateinit var globalViewModel: GlobalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        globalViewModel = ViewModelProviders.of(this).get(GlobalViewModel::class.java)


        setContentView(R.layout.pinned_layout)
        recyclerView = findViewById<View>(R.id.pinned_recycler) as RecyclerView
        rabiit = findViewById<View>(R.id.no_item) as ImageView
        newsAdapterOld = BlogAdapter(this)


        val mLayoutManager = LinearLayoutManager(this )
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = newsAdapterOld


        populateListArray()


    }


    fun populateListArray() {

        FirebaseFunctions().getFavoritesFromMyAccount(this@ShowFavoritesActivity)

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

                newsAdapterOld.update(mutableListOf<BlogItem>())


                if (Blogs.isNotEmpty()) {

                    recyclerView.visibility = View.VISIBLE
                    Log.d("blogs from fbstore", "${Blogs}")

                    newsAdapterOld.update(Blogs)
                    //verticlePagerAdapter!!.update(Blogs)
                    rabiit.visibility = View.GONE
                }else{
                    newsAdapterOld.update(mutableListOf<BlogItem>())
                    recyclerView.visibility = View.VISIBLE
                    rabiit.visibility = View.VISIBLE

                }
            } catch (e: Exception) {
                newsAdapterOld.update(mutableListOf<BlogItem>())
                e.printStackTrace()
                rabiit.visibility = View.VISIBLE

            }
        }else{
            rabiit.visibility = View.VISIBLE
        }
    }

    override fun dataAddSuccess(successful: Boolean) {

    }

    override fun dataUpdateSuccess(successful: Boolean) {

    }

    override fun checkFavorite(isFavorite: Boolean) {

    }

    override fun toggleFavorite(isFavorite: Boolean) {
    }

    class RecyclerTouchListener(
        context: Context,
        recyclerView: RecyclerView,
        private val clickListener: ShowFavoritesActivity.ClickListener?
    ) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent): Boolean {
                        return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                    }
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child))
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)

        fun onLongClick(view: View?, position: Int)
    }
}
