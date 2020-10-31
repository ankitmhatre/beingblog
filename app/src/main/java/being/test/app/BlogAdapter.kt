package being.test.app


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import com.local.city.models.BlogItem


class BlogAdapter(val activity: Activity) : RecyclerView.Adapter<BlogAdapter.MyViewHolder>() {

    var BlogItems: List<BlogItem> = listOf()


    internal var previousExpandedPosition = -1
    internal var mExpandedPosition = -1


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        var content: TextView
        var publishedOn: TextView
        var author: TextView
        internal var iv: ImageView
        internal var card_view: CardView

        init {
            title = view.findViewById<View>(R.id.blog_title_text) as TextView
            content = view.findViewById<View>(R.id.blog_content_text) as TextView
            publishedOn = view.findViewById<View>(R.id.blog_time_text) as TextView
            iv = view.findViewById<View>(R.id.blog_image) as ImageView
            author = view.findViewById<View>(R.id.blog_author) as TextView
             card_view = view.findViewById<View>(R.id.card_view) as CardView

        }
    }


    fun update(BlogItems: List<BlogItem>) {
        Log.d("BlogItemResponse", BlogItems.toString() + "ad")
        this.BlogItems = BlogItems
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.blog_item_layout, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val isExpanded = position == mExpandedPosition
        holder.content.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.itemView.isActivated = isExpanded
        //        holder.itemView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                mExpandedPosition = isExpanded ? -1 : position;
        //                notifyItemChanged(position);
        //            }
        //        });


        holder.title.text = BlogItems[position].title
        holder.content.text = BlogItems[position].content
        holder.publishedOn.text = Utilities.getDateampm(BlogItems[position].timestamp)
        holder.author.text = BlogItems[position].author_name

        // Log.d("imageUrl", "${API.BASE_URL}${BlogItems!![position].image_url}")
        holder.iv.scaleType = ImageView.ScaleType.CENTER_CROP
        holder.card_view.setOnClickListener {
            val bundle = Intent(activity, FullViewActivity::class.java)

            bundle.putExtra("title", BlogItems[position].title)
            bundle.putExtra("content", BlogItems[position].content)
            bundle.putExtra("reported_on", BlogItems[position].timestamp)
            bundle.putExtra("author_name", BlogItems[position].author_name)
            bundle.putExtra("img_url", BlogItems[position].image_url)
            bundle.putExtra("blog_id", BlogItems[position].blog_id)
            bundle.putExtra("isPinned", BlogItems[position].isPinned)
            bundle.putExtra("BlogItem", BlogItems[position])
            activity.startActivity(bundle)

        }



        val url = "${(BlogItems[position].image_url)}"




        holder.iv.load(url) {
            placeholder(R.drawable.blogs_default)
            crossfade(true)
        }



    }

    override fun getItemCount(): Int {
        return BlogItems.size
    }
}
