package rocks.tauonmusicbox.tauonremote

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlaylistListAdapter(private val items: List<TauonPlaylist>, private val clickHandler: (TauonPlaylist) -> Unit) :
    RecyclerView.Adapter<PlaylistListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.lineTitleText)
        val itemHolder: ConstraintLayout = view.findViewById(R.id.trackLineHolder)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        context = viewGroup.context
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.track_line, viewGroup, false)

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
        viewHolder.textView.text = items[position].name
        viewHolder.itemView.setOnClickListener {
            clickHandler(items[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size

}

class PlaylistFragment(val items: List<TauonPlaylist>, val controller: Controller) : Fragment() {

    var ready = false
    lateinit var adapter: PlaylistListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        adapter = PlaylistListAdapter(items) {
            controller.changePlaylist(it)
        }
        val rcview: RecyclerView = view.findViewById(R.id.playlistRecycler)
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