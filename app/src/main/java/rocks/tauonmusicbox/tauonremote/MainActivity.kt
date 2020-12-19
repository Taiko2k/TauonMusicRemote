// Tauon Music Remote
//
// Copyright © 2020, Taiko2k captain(dot)gxj(at)gmail.com
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.


package rocks.tauonmusicbox.tauonremote

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var tracks = mutableListOf<TauonTrack>()
    var playlists = mutableListOf<TauonPlaylist>()
    var albums = mutableListOf<TauonTrack>()

    val settings = Settings(this)
    val controller = Controller(this, settings)

    var got_playlists = false
    var got_albums = false
    var got_tracks = false

    var playingAlbumId = -1

    lateinit var playButton: ImageView
    lateinit var nextButton: ImageView
    lateinit var backButton: ImageView
    lateinit var repeatButton: ImageView
    lateinit var shuffleButton: ImageView
    lateinit var seekBar: SeekBar
    lateinit var timeProgress: TextView
    lateinit var searchField: SearchView

    lateinit var pager: ViewPager
    lateinit var welcomeFragment: WelcomeFragment
    lateinit var trackListFragment: TrackListFragment
    lateinit var playlistListFragment: PlaylistFragment
    lateinit var albumListFragment: AlbumListFragment
    lateinit var nowPlayingFragment: NowPlayingFragment

    // Have function run in background
    lateinit var mainHandler: Handler
    private val backgroundTick = object : Runnable {
        override fun run() {
            fetchStatus()
            mainHandler.postDelayed(this, 2500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        settings.load_settings()

        controller.mode = settings.getMode()

        playlists = mutableListOf()
        albums = mutableListOf()


        // Setup control buttons
        playButton = findViewById(R.id.playButton)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)
        repeatButton = findViewById(R.id.repeatButton)
        shuffleButton = findViewById(R.id.shuffleButton)
        timeProgress = findViewById(R.id.progressTime)

        repeatButton.setOnClickListener {
            controller.clickRepeat()
        }

        shuffleButton.setOnClickListener {
            controller.clickShuffle()
        }

        class SeekListener(): SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    controller.seekClick(seekBar.progress)
                }
            }

        }
        val seekListener = SeekListener()

        seekBar = findViewById(R.id.seekBar)
        seekBar.max = 1000
        seekBar.setOnSeekBarChangeListener(seekListener)

        playButton.setOnClickListener {
            controller.playButtonClick()
        }

        nextButton.setOnClickListener {
            controller.nextButtonClick()
        }
        backButton.setOnClickListener {
            controller.backButtonClick()
        }



        val playControls: ConstraintLayout = findViewById(R.id.playControls)
        val rootView: ConstraintLayout = findViewById(R.id.mainRoot)

        var saveDiff: Int = -1
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            if (saveDiff == -1){
                saveDiff = heightDiff
            }

            if (heightDiff > 100 && heightDiff != saveDiff) {
                playControls.visibility = View.GONE
            } else {
                playControls.visibility = View.VISIBLE
            }
        }


//
//        //picasso.setIndicatorsEnabled(true)

        mainHandler = Handler(Looper.getMainLooper())

        // Pager view ------------------------
        pager = findViewById(R.id.mainSlider)

        val adapter = ViewPagerAdapter(supportFragmentManager)

        welcomeFragment = WelcomeFragment(settings, controller, this)
        playlistListFragment = PlaylistFragment(playlists, controller)
        albumListFragment = AlbumListFragment(albums, controller, settings)
        trackListFragment = TrackListFragment(tracks, controller)
        nowPlayingFragment = NowPlayingFragment(settings, controller)


        adapter.addFragment(welcomeFragment)
        adapter.addFragment(playlistListFragment)
        adapter.addFragment(albumListFragment)
        adapter.addFragment(trackListFragment)
        adapter.addFragment(nowPlayingFragment)

        pager.adapter = adapter
        if (settings.ip_address.isBlank()){
            pager.setCurrentItem(0, false)
        } else {
            pager.setCurrentItem(2, false)
        }

        // Click text to goto playing in list  todo its only for album list
        val metadataText: ConstraintLayout = findViewById(R.id.metadataText)
        metadataText.setOnClickListener {
            if (albumListFragment.ready){
                var p = 0
                for (alb in albums){
                    if (alb.position >= controller.tauonStatus.position){
                        pager.setCurrentItem(2, true)
                        albumListFragment.rcview.scrollToPosition(p - 1)
                        //albumListFragment.rcview.smoothScrollToPosition(p - 1)
                        break
                    }
                    p += 1
                }

            }

        }


//
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)


    // End OnCreate -----------------------------
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when (requestCode) {
//            1 -> {
//
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.isNotEmpty()
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission granted and now can proceed
//                    mymethod() //a sample method called
//                    Toast.makeText(this@MainActivity, "Permission got", Toast.LENGTH_SHORT).show()
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(this@MainActivity, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
//                }
//                return
//            }
//        }
//    }

//    fun mymethod(){
//
//    }

    override fun onAttachFragment(fragment: Fragment) {
        println(fragment)
        super.onAttachFragment(fragment)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(backgroundTick)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(backgroundTick)
    }

    override fun onBackPressed() {

        if (pager.currentItem > 1){
            pager.setCurrentItem(pager.currentItem - 1, true);
        } else {
            super.onBackPressed()
        }
    }

    // ----------------

    // This function updates the UI with current track info
    fun updateStatus() {

        val titleText: TextView = findViewById(R.id.titleTextView)
        titleText.text = controller.tauonStatus.track.title

        val artistText: TextView = findViewById(R.id.artistTextView)
        artistText.text = controller.tauonStatus.track.artist

        //val albumText: TextView = findViewById(R.id.albumTextView)
        //albumText.text = controller.tauonStatus.track.album
        if (controller.mode == 1){
            controller.activePlaylistPlaying = controller.tauonStatus.playlist
        }
        nowPlayingFragment.updateMainArt()


        if (controller.tauonStatus.status == "playing") {
            playButton.setBackgroundResource(R.drawable.ic_pause)
        } else {
            playButton.setBackgroundResource(R.drawable.ic_play_arrow)
        }

        if (controller.tauonStatus.album_id != playingAlbumId) {
            playingAlbumId = controller.tauonStatus.album_id
            runOnUiThread {
                albumListFragment.update()
            }
        }


    }



    fun fetchTrackList(playlistId: String, album_id: Int, go_to_tracks: Boolean = false) {

        if (settings.ip_address.isBlank()){
            return
        }

        // Get albums
        //println("Attempt fetch TRACKLIST")

        val base_url = "http://${settings.ip_address}:7814/api1/albumtracks/$playlistId/$album_id"
        //val base_url = "http://${settings.ip_address}:7814/api1/tracklist/$playlistId"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //println("Request tracks failed")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                response.body?.close()
                //println("Got request tracks")
                //println(body)


                val gson = GsonBuilder().create()

                runOnUiThread {
                    tracks.clear()
                    tracks.addAll(gson.fromJson(body, TrackListData::class.java).tracks)
                    //println(albums[0].title)
                    trackListFragment.update()
                    if (go_to_tracks) {
                        pager.setCurrentItem(3, true)
                    }
                }
            }
        })
    }

    fun fetchAlbums(playlistId: String, go_to_albums: Boolean = false) {

        if (settings.ip_address.isBlank()){
            return
        }
        // Get albums
        //println("Attempt fetch albums")
        //println(playlistId)

        val base_url = "http://${settings.ip_address}:7814/api1/albums/$playlistId"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //println("Request albums failed")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                response.body?.close()
                //println("Got request albums")
                //println(body)

                val gson = GsonBuilder().create()

                runOnUiThread {
                    albums.clear()
                    albums.addAll(gson.fromJson(body, AlbumData::class.java).albums)
                    //println(albums[0].title)
                    albumListFragment.update()
                    playingAlbumId = controller.tauonStatus.album_id
                    got_albums = true
                    if (go_to_albums) {
                        pager.setCurrentItem(2, true)
                    }
                }
            }
        })
    }

    fun fetchStatus() {
        if (controller.mode == 2 && controller.mediaPlayer.isPlaying) {
            if (controller.tauonStatus.track.duration > 1){
                controller.tauonStatus.progress = controller.mediaPlayer.currentPosition

                runOnUiThread {
                    //println(controller.tauonStatus.progress)
                    //println(controller.getFormattedProgress())
                    timeProgress.text = controller.getFormattedProgress()
                    seekBar.setProgress(
                            controller.getProgress1k(),
                            false)
                    if (controller.tauonStatus.track.id >= 0 && controller.mediaPlayer.isPlaying) {
                        controller.hitApi("playinghit/${controller.tauonStatus.track.id}")
                    }
                }
            }

        }

        if (settings.ip_address.isBlank() || (controller.mode != 1 && got_playlists)){
            return
        }
        // Get playlists
        if (!got_playlists) {

            //println("Attempt fetch playlists")
            val base_url = "http://${settings.ip_address}:7814/api1/playlists"
            val request = Request.Builder().url(base_url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //println("Request playlists failed")
                    //println(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    response.body?.close()
                    //println("Got request playlists")
                    //println(body)

                    val gson = GsonBuilder().create()

                    runOnUiThread {
                        playlists.clear()
                        playlists.addAll(gson.fromJson(body, PlaylistData::class.java).playlists)
                        playlistListFragment.update()
                        got_playlists = true
                    }
                }
            })
        }


        //println("Attempt get now playing json")
        val base_url = "http://${settings.ip_address}:7814/api1/status"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //println("Request failed")
                //println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                response.body?.close()
                //println("Got request")
                //println(body)

                val gson = GsonBuilder().create()
                controller.tauonStatus = gson.fromJson(body, controller.tauonStatus::class.java)
                //println("SET STATUS: ${controller.tauonStatus.track.title}")

                controller.syncMode()

                if (!got_albums && controller.tauonStatus.playlist.isNotEmpty()) {
                    fetchAlbums(controller.tauonStatus.playlist)
                    got_albums = true
                    controller.activePlaylistViewing = controller.tauonStatus.playlist

                }
                if (!got_tracks) {
                    //println("FETCH TRACKS")
                    fetchTrackList(controller.tauonStatus.playlist, controller.tauonStatus.album_id)
                    got_tracks = true
                }

                runOnUiThread {
                    timeProgress.text = controller.getFormattedProgress()
                    seekBar.setProgress(
                            controller.getProgress1k(),
                            false)
                    updateStatus()
                }
            }
        })

    }
}








