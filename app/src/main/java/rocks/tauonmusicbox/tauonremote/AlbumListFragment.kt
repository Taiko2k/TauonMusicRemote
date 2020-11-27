

package rocks.tauonmusicbox.tauonremote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AlbumListAdapter(private val items: List<TauonTrack>, val settings: Settings, private val clickHandler: (TauonTrack) -> Unit) :
        RecyclerView.Adapter<AlbumListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    lateinit var context: Context
    val picasso: Picasso = Picasso.get()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.albumTitleText)
        val albumArtistView: TextView = view.findViewById(R.id.albumArtistText)
        val albumArt: ImageView = view.findViewById(R.id.albumLineArt)
        val itemHolder: ConstraintLayout = view.findViewById(R.id.albumLineHolder)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        context = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.album_line, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        // Set alt colour patters
        if (position % 2 == 0) {
            viewHolder.itemHolder.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.list_item_alt
                    )
            )
        } else {
            viewHolder.itemHolder.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.list_item_primary
                    )
            )
        }
        // Set playlist name text
        viewHolder.textView.text = items[position].album
        viewHolder.albumArtistView.text = items[position].album_artist

        picasso.load("http://${settings.ip_address}:7814/api1/pic/small/" + items[position].id)
            .into(viewHolder.albumArt)

        viewHolder.itemView.setOnClickListener {
            clickHandler(items[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size

}

class AlbumListFragment(val items: List<TauonTrack>, val controller: Controller, val settings: Settings) : Fragment() {

    var ready = false
    lateinit var adapter: AlbumListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_album_list, container, false)
        adapter = AlbumListAdapter(items, settings) {
            //controller.startTrack(it)
            controller.loadTracks(it)
        }
        val rcview: RecyclerView = view.findViewById(R.id.albumListRecycler)
        rcview.layoutManager = LinearLayoutManager(view.context)
        rcview.adapter = adapter
        ready = true

        return view
    }

    fun update(){
        if (ready) {
            adapter.notifyDataSetChanged()
        }
    }

}