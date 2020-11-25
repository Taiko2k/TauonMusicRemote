package rocks.tauonmusicbox.tauonremote

import okhttp3.*
import java.io.IOException


class Controller(val activity: MainActivity, val settings: Settings) {

    var active_playlist: String = ""

    fun hitApi(text: String, callback: (()->Unit)? = null){

        if (settings.ip_address.isBlank()){
            return
        }

        val base_url = "http:///${settings.ip_address}:7814/api1/$text"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Command failed")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.close()
                println("Command sent")
                //reload_tracks()
                if (callback != null) {
                    callback()
                }

            }
        })
    }

    fun startTrack(track: TauonTrack){
        playPosition(track.position, active_playlist)
    }

    fun playPosition(position: Int, playlist: String){

        println("Start track")
        hitApi("start/${playlist}/${position}") { reload_tracks() }

    }

    fun changePlaylist(playlist: TauonPlaylist){
        active_playlist = playlist.id
        activity.fetchAlbums(active_playlist, true)
    }

    fun loadTracks(track: TauonTrack){
        // click an album to load its tracks
        activity.fetchTrackList(active_playlist, track.position, true)


    }

    fun reload_tracks(){
        activity.got_tracks = false
        activity.fetchStatus()
    }
}