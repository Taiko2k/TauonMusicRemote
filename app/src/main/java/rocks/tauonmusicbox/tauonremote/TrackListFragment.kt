package rocks.tauonmusicbox.tauonremote

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val tracks: List<TauonTrack>, private val controller: Controller, private val clickHandler: (TauonTrack) -> Unit) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberView: TextView = view.findViewById(R.id.lineTrackNumber)
        val textView: TextView = view.findViewById(R.id.lineTitleText)
        val artistText: TextView = view.findViewById(R.id.lineArtistText)
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

        if (controller.tauonStatus.track.id == tracks[position].id) {
            viewHolder.itemHolder.setBackgroundColor(
                    ContextCompat.getColor(
                            context,
                            R.color.list_item_playing
                    )
            )
        }

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.numberView.text = tracks[position].track_number + "."
        viewHolder.textView.text = tracks[position].title
        viewHolder.artistText.text = tracks[position].artist
        viewHolder.itemView.setOnClickListener {
            clickHandler(tracks[position])
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = tracks.size

}

class TrackListFragment(val tracks: List<TauonTrack>, val controller: Controller) : Fragment() {


    lateinit var adapter: MyAdapter
    var ready = false


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_track_list, container, false)
        adapter = MyAdapter(tracks, controller) {
            controller.startTrack(it)
        }
        val rcview: RecyclerView = view.findViewById(R.id.trackListRecycler)
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