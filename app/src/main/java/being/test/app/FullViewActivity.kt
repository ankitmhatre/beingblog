package being.test.app


import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import being.test.app.models.BlogItem
import being.test.app.repository.GlobalRepository
import being.test.app.viewmodel.GlobalViewModel
import coil.api.load
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.wang.avi.AVLoadingIndicatorView

class FullViewActivity : AppCompatActivity() {
    private val TAG = "FullViewActivity"
    lateinit var textView7: TextView
    lateinit var apiInterface: ApiInterface
    val db = FirebaseFirestore.getInstance()

    //lateinit var deepLinkApiInterface: DeepLinkApiInterface
    lateinit var globalViewModel: GlobalViewModel


    lateinit var title: String
    internal var content: String? = null
    lateinit var warningFullViewText: TextView
    lateinit var full_title_news: TextView
    lateinit var full_reported_by_news: TextView
    lateinit var contentWebview: WebView
    lateinit var full_reported_on_news: TextView
    lateinit var full_image_news: ImageView
    lateinit var favoriteBLog: FloatingActionButton
    lateinit var editBlog: FloatingActionButton
    lateinit var deleteBLog: FloatingActionButton
    lateinit var parentcontainer: RelativeLayout
    lateinit var progressbar: AVLoadingIndicatorView
    lateinit var rootView: RelativeLayout
    lateinit var bookmarkBlogItem: BlogItem

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        freshData(intent!!)
    }


    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        globalViewModel = ViewModelProviders.of(this).get(GlobalViewModel::class.java)
        apiInterface = API.getClient().create(ApiInterface::class.java)
//        deepLinkApiInterface =
//            DeepLinkAPI.getDeepLinkClient().create(DeepLinkApiInterface::class.java)
        setContentView(R.layout.full_view_blog)
        full_title_news = findViewById<TextView>(R.id.full_title_news)
        full_reported_by_news = findViewById<TextView>(R.id.full_reported_by_news)
        contentWebview = findViewById<WebView>(R.id.contentWebview)
        full_reported_on_news = findViewById<TextView>(R.id.full_reported_on_news)
        full_image_news = findViewById<ImageView>(R.id.full_image_news)
        favoriteBLog = findViewById<View>(R.id.full_news_bookmark) as FloatingActionButton
        editBlog = findViewById<View>(R.id.editBlogFab) as FloatingActionButton
        deleteBLog = findViewById<View>(R.id.deleteBlogFab) as FloatingActionButton

        parentcontainer = findViewById(R.id.fullViewContainer)
        progressbar = findViewById(R.id.fullViewProgressBar)
        warningFullViewText = findViewById<View>(R.id.warningFullViewText) as TextView
        rootView = findViewById<View>(R.id.rootView) as RelativeLayout
        if (PrefUtils.getString(this@FullViewActivity, PrefKeys.USER_ACC_TYPE, null)
                .equals("admin")
        ) {
            favoriteBLog.visibility = View.GONE
            editBlog.visibility = View.VISIBLE
            deleteBLog.visibility = View.VISIBLE

        } else {
            favoriteBLog.visibility = View.VISIBLE
            editBlog.visibility = View.GONE
            deleteBLog.visibility = View.GONE
        }


        //favoriteBLog.


        editBlog.setOnClickListener {

        }

        deleteBLog.setOnClickListener {


            db.collection("data/v1/blogs").document(bookmarkBlogItem.document_key)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.blog_deleted),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    GlobalRepository(application).deleteSpecificBlog(bookmarkBlogItem.blog_id)

                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.failed_blog_deleted),
                        Snackbar.LENGTH_SHORT
                    ).show()

                }
        }

        favoriteBLog.setOnClickListener {
            Log.d(TAG, "BlogItem Pinned : ${bookmarkBlogItem.isPinned}")
            if (bookmarkBlogItem != null)
                if (bookmarkBlogItem.isPinned == 1) {
                    GlobalRepository(application).updatePinned(bookmarkBlogItem.blog_id, 0)
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.removed_from_bookmarks),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    favoriteBLog.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_star_border
                        )
                    );


                } else {
                    GlobalRepository(application).updatePinned(bookmarkBlogItem.blog_id, 1)
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.saved_to_bookmarks),
                        Snackbar.LENGTH_SHORT
                    ).show()

                    favoriteBLog.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_star
                        )
                    );
                }


        }

        freshData(intent)

    }

    fun freshData(i: Intent) {
        progressbar.show()
        showProgress()



        try {

globalViewModel.getBlogItem(i.extras?.get("blog_id") as Long).observeForever {

    if (it != null) {
        bookmarkBlogItem = it
        favoriteBLog.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                when (it.isPinned) {
                    0 -> R.drawable.ic_star_border
                    1 -> R.drawable.ic_star
                    else -> R.drawable.ic_star_border
                }
            )
        );
        thisisthearticle(it)
    }else{
        finish()
    }
}



        } catch (e: Exception) {
            e.printStackTrace()


        }
    }


    private fun thisisthearticle(blogItem: BlogItem) {
        Log.d(TAG, "BlogItem: thisIsTheArticle ${blogItem.content}")

        hideProgress()
        warningFullViewText.visibility = View.GONE
        full_title_news.text = blogItem.title

        val preHtml = "<body style=\"color:"

        val postColorHtml = "\">"
        var lastContent = "</body>"
        var colorhex = "#119DA4"
        var newContent = "$preHtml $colorhex $postColorHtml ${blogItem.content} $lastContent"


        val base64version: String =
            Base64.encodeToString(blogItem.content.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        contentWebview.loadData(base64version, "text/html; charset=UTF-8", "base64")
        contentWebview.loadUrl("javascript:document.body.style.color=\"white\";")


        full_reported_on_news.text = Utilities.getDateampm(blogItem.timestamp)
        full_reported_by_news.text = blogItem.author_name
        //   val img_url = "${API.BASE_URL}${blogItem.image_url}"
        val img_url = "${blogItem.image_url}"






        full_image_news.load(img_url) {
            allowHardware(false)
            target { drawable ->
                val bm = (drawable as BitmapDrawable).bitmap
                full_image_news.setImageBitmap(bm)
                full_image_news.setOnClickListener {
                    // showImage(bm)
                }

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            23 -> {
                progressbar.visibility = View.GONE
            }
        }
    }

    fun showProgress() {
        parentcontainer.visibility = View.GONE
        progressbar.visibility = View.VISIBLE
    }

    fun hideProgress() {
        parentcontainer.visibility = View.VISIBLE
        progressbar.visibility = View.GONE
    }

}