package rocks.tauonmusicbox.tauonremote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso

class NowPlayingFragment(val settings: Settings, val controller: Controller) : Fragment() {

    lateinit var albumArt: ImageView
    var ready = false
    val picasso = Picasso.get()
    var loaded_album_id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)

        albumArt = view.findViewById(R.id.mainAlbumArt)
        ready = true
        loaded_album_id = -1
        updateMainArt()

        return view
    }

    fun updateMainArt(){
        if (ready && settings.ip_address.isNotBlank()){ //&& controller.tauonStatus.album_id >= 0 && loaded_album_id != controller.tauonStatus.album_id) {
                println("WANT LOAD ALBUM ART")
                loaded_album_id = controller.tauonStatus.album_id
                picasso.load("http://${settings.ip_address}:7814/api1/pic/medium/" + controller.tauonStatus.track.id)
                        .into(albumArt)
        }
    }

}