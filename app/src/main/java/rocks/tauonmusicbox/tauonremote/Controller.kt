package rocks.tauonmusicbox.tauonremote

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import com.google.gson.GsonBuilder
import okhttp3.*
import okio.BufferedSink
import okio.Okio
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class Controller(val activity: MainActivity, val settings: Settings) {

    var activePlaylistViewing: String = ""
    var activePlaylistPlaying: String = ""
    var mode = 1

    var repeat = 0
    //var cachedFile: ByteString

    var cachedFileID = -1

    val mediaPlayer = MediaPlayer()
    val dummy_track = TauonTrack()
    var tauonStatus = TauonStatus("stopped",
            false, false, 0, "", 0, 0, dummy_track)
    var playerPaused = false


    fun setStreamMode(){
        if (mode != 2) {
            mode = 2
            settings.setMode(mode)
            resetStatus()
        }
    }

    fun setRemoteMode(){
        if (mode != 1){
            mode = 1
            settings.setMode(mode)
            resetStatus()
        }
    }

    fun resetStatus(){
        tauonStatus.track = dummy_track
        activity.seekBar.progress = 0
        activity.timeProgress.text = "00:00"
        activity.updateStatus()
    }

    fun getProgress1k(): Int {
        if (tauonStatus.track.duration < 1) {
            return 0
        }
        return ((tauonStatus.progress / tauonStatus.track.duration.toDouble()) * 1000).toInt()
    }

    fun getFormattedProgress(): String {
        val millis = tauonStatus.progress.toLong()
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )
    }

    fun clickRepeat() {

        if (mode == 1) {
            Toast.makeText(activity, "Sorry, not yet implemented for REMOTE mode", Toast.LENGTH_SHORT).show()
        } else {
            toggleRepeat()
        }
    }

    fun toggleRepeat(){
        if (repeat == 0){
            repeat = 1
            activity.repeatButton.setBackgroundResource(R.drawable.ic_repeat)
        } else if (repeat == 1) {
            repeat = 0
            activity.repeatButton.setBackgroundResource(R.drawable.ic_repeat_off)
        }
    }

    fun seekClick(progress: Int) {
        if (mode == 1) {
            hitApi("seek1k/$progress")
            activity.fetchStatus()
        } else if (mode == 2){
            if (tauonStatus.track.duration > 1) {

                if (cachedFileID != tauonStatus.track.id && false){
                    // Re-download entire file
//                    mediaPlayer.reset()
//                    val base_url = "http://${settings.ip_address}:7814/api1/file/${tauonStatus.track.id}"
//                    val request = Request.Builder().url(base_url).build()
//                    val client = OkHttpClient()
//                    client.newCall(request).enqueue(object : Callback {
//                        override fun onFailure(call: Call, e: IOException) {
//                            println("Command failed")
//                        }
//                        override fun onResponse(call: Call, response: Response) {
//
//                            activity.runOnUiThread {
//                                Toast.makeText(activity, "Buffering... Please wait", Toast.LENGTH_SHORT).show()
//                            }
//
//                            val downloadedFile = File(activity.cacheDir, "audiofile")
//                            val sink: BufferedSink = downloadedFile.sink().buffer()
//                            response.body?.source()?.let { sink.writeAll(it) }
//                            sink.close()
//                            println("download DONE")
//                            println(downloadedFile.length())
//                            cachedFileID = tauonStatus.track.id
//
//
//                            println("Command sent")
//                            //cachedFile = response.body.byteString()
//                            response.body?.close()
//                            //reload_tracks()
//                            activity.runOnUiThread {
//
//
//                                mediaPlayer.setDataSource(downloadedFile.absolutePath)
//                                mediaPlayer.setOnCompletionListener {  }
//                                mediaPlayer.prepare()
//                                mediaPlayer.seekTo((tauonStatus.track.duration * (progress / 1000f)).toInt())
//                                mediaPlayer.start()
//                            }
//
//                        }
//                    })

                } else {
                    mediaPlayer.seekTo((tauonStatus.track.duration * (progress / 1000f)).toInt())
                    activity.fetchStatus()
                }

            }
        }
    }

    fun hitApi(text: String, callback: (() -> Unit)? = null){

        if (settings.ip_address.isBlank()){
            return
        }

        val base_url = "http:///${settings.ip_address}:7814/api1/$text"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
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

    fun syncTauonStatus(track: TauonTrack){
        tauonStatus.track = track
        tauonStatus.position = track.position
        tauonStatus.progress = 0
        activity.trackListFragment.update()

    }

    fun startTrack(track: TauonTrack){
        //println("Want start track: ${track.title}")
        if (mode == 1) {
            playPosition(track.position, activePlaylistViewing)
        } else if (mode == 2) {

            mediaPlayer.setOnPreparedListener {
                it.start()
                activity.playButton.setBackgroundResource(R.drawable.ic_pause)
                tauonStatus.status = "playing"
            }
            playerPaused = false
            mediaPlayer.reset()
            val url: Uri = Uri.parse("http://${settings.ip_address}:7814/api1/file/${track.id}")
            //val url: Uri = Uri.parse("http://${settings.ip_address}:7814/api1/stream/${track.id}")
            mediaPlayer.setDataSource(activity, url)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnCompletionListener {
                endOfTrackAction()
            }
            activePlaylistPlaying = activePlaylistViewing
            syncTauonStatus(track)
            if (activity.tracks.size > 0){
                tauonStatus.album_id = activity.tracks[0].position
            }

            activity.seekBar.progress = 0

            activity.updateStatus()
            //println("DONE play")
        }
    }

    fun endOfTrackAction(){
        if (repeat == 1) {
            startTrack(tauonStatus.track)
            return
        }

        next()
    }

    fun playButtonClick(){
        if (mode == 1){
            playPauseSend()
        } else if (mode == 2){
            playPause()
        }
    }

    fun nextButtonClick(){
        if (mode == 1){
            nextSend()
        } else if (mode == 2) {
            next()
        }
    }
    fun backButtonClick(){
        if (mode == 1){
            backSend()
        } else if (mode == 2) {
            back()
        }
    }

    fun backSend(){
        hitApi("back")
        activity.fetchStatus()
    }


    fun back() {
        tauonStatus.position -= 1
        if (tauonStatus.position < 0) {
            tauonStatus.position = 0
            Toast.makeText(activity, "Already at start of playlist!", Toast.LENGTH_SHORT).show()
        }
        startByPosition()
    }


    fun next() {
        tauonStatus.position += 1
        startByPosition()
    }

    fun startByPosition(){
        var newTrack: TauonTrack
        val base_url = "http:///${settings.ip_address}:7814/api1/trackposition/$activePlaylistPlaying/${tauonStatus.position}"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Request track failed")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                response.body?.close()
                val gson = GsonBuilder().create()

                activity.runOnUiThread {
                    //println("NEXT TRACK...")
                    newTrack = gson.fromJson(body, TauonTrack::class.java)
                    tauonStatus.status = "playing"
                    startTrack(newTrack)
                }
            }
        })

    }

    fun nextSend() {
        hitApi("next")
        activity.fetchStatus()
    }

    fun playPause() {

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            playerPaused = true
            activity.playButton.setBackgroundResource(R.drawable.ic_play_arrow)
        } else if (playerPaused) {
            mediaPlayer.start()
            playerPaused = false
            activity.playButton.setBackgroundResource(R.drawable.ic_pause)
        }

    }

    fun playPauseSend() {

        if (tauonStatus.status == "playing") {
            tauonStatus.status = "paused"
            activity.playButton.setBackgroundResource(R.drawable.ic_play_arrow)
            hitApi("pause")

        } else {
            tauonStatus.status = "playing"
            activity.trackListFragment.update()
            activity.playButton.setBackgroundResource(R.drawable.ic_pause)
            hitApi("play")
        }
    }

    fun playPosition(position: Int, playlist: String){

        //println("Start track")
        hitApi("start/${playlist}/${position}") { reload_tracks() }

    }

    fun changePlaylist(playlist: TauonPlaylist){
        activePlaylistViewing = playlist.id
        activity.fetchAlbums(activePlaylistViewing, true)
    }

    fun loadTracks(track: TauonTrack){
        // click an album to load its tracks
        activity.fetchTrackList(activePlaylistViewing, track.position, true)


    }

    fun reload_tracks(){
        activity.got_tracks = false
        activity.fetchStatus()

    }
}