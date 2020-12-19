

package rocks.tauonmusicbox.tauonremote

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*

class AlbumListAdapter(private val items: List<TauonTrack>, val settings: Settings, val controller: Controller, private val clickHandler: (TauonTrack) -> Unit) :
        RecyclerView.Adapter<AlbumListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    private val filterList = mutableListOf<TauonTrack>()
    private var filterActive = false

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

        var item = items[position]
        if (filterActive) {
            item = filterList[position]
        }

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

        if (controller.tauonStatus.album_id == item.album_id && (controller.activePlaylistPlaying == controller.activePlaylistViewing)) {
            viewHolder.itemHolder.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.list_item_playing
                    )
            )
        }


        // Set playlist name text
        viewHolder.textView.text = item.album
        viewHolder.albumArtistView.text = item.album_artist

        picasso.load("http://${settings.ip_address}:7814/api1/pic/small/" + item.id)
                .into(viewHolder.albumArt)

        viewHolder.itemView.setOnClickListener {
            clickHandler(item)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (filterActive) {
            return filterList.size
        } else {
            return items.size
        }
    }

    fun filter(text: String) {
        if (text.isEmpty()) {
            filterActive = false
        } else {
            filterActive = true
            filterList.clear()
            for (track in items) {
                if (track.artist.toLowerCase(Locale.ROOT).contains(text) ||
                        track.album.toLowerCase(Locale.ROOT).contains(text) ||
                        track.album_artist.toLowerCase(Locale.ROOT).contains(text)) {
                    filterList.add(track)
                }
            }
        }
        notifyDataSetChanged()

    }
}

class AlbumListFragment(val items: List<TauonTrack>, val controller: Controller, val settings: Settings) : Fragment() {

    var ready = false
    lateinit var adapter: AlbumListAdapter
    lateinit var searchField: SearchView
    lateinit var rcview: RecyclerView

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_album_list, container, false)
        adapter = AlbumListAdapter(items, settings, controller) {
            //controller.startTrack(it)
            controller.loadTracks(it)
        }
        rcview = view.findViewById(R.id.albumListRecycler)
        rcview.layoutManager = LinearLayoutManager(view.context)
        rcview.adapter = adapter
        ready = true


        searchField = view.findViewById(R.id.searchField)
        searchField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    adapter.filter(query.toLowerCase(Locale.ROOT))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    adapter.filter(newText.toLowerCase(Locale.ROOT))
                }
                return true
            }
        })

        return view
    }

    fun update(){
        if (ready) {
            adapter.notifyDataSetChanged()
        }
    }

}