package rocks.tauonmusicbox.tauonremote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.*
import org.w3c.dom.Text
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val picasso = Picasso.get()

    var tracks = mutableListOf<TauonTrack>()
    var playlists = mutableListOf<TauonPlaylist>()
    var albums = mutableListOf<TauonTrack>()

    val settings = Settings(this)
    val controller = Controller(this, settings)


    var got_playlists = false
    var got_albums = false
    var got_tracks = false


    lateinit var playButton: ImageView
    lateinit var nextButton: ImageView
    lateinit var backButton: ImageView
    lateinit var repeatButton: ImageView
    lateinit var seekBar: SeekBar
    lateinit var timeProgress: TextView

    lateinit var pager: ViewPager
    lateinit var welcomeFragment: WelcomeFragment
    lateinit var trackListFragment: TrackListFragment
    lateinit var playlistListFragment: PlaylistFragment
    lateinit var albumListFragment: AlbumListFragment

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
        setContentView(R.layout.activity_main)

        settings.load_settings()

        playlists = mutableListOf()
        albums = mutableListOf()

        // Setup control buttons
        playButton = findViewById(R.id.playButton)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)
        repeatButton = findViewById(R.id.repeatButton)
        timeProgress = findViewById(R.id.progressTime)

        repeatButton.setOnClickListener {
            controller.clickRepeat()
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
            //playPauseSend()
            controller.playButtonClick()
        }
        nextButton.setOnClickListener {
            controller.nextButtonClick()
        }
        backButton.setOnClickListener {
            backSend()
        }

        //picasso.setIndicatorsEnabled(true)

        mainHandler = Handler(Looper.getMainLooper())

        // Pager view ------------------------
        pager = findViewById(R.id.mainSlider)

        val adapter = ViewPagerAdapter(supportFragmentManager)

        welcomeFragment = WelcomeFragment(settings, controller, this)
        playlistListFragment = PlaylistFragment(playlists, controller)
        albumListFragment = AlbumListFragment(albums, controller, settings)
        trackListFragment = TrackListFragment(tracks, controller)


        adapter.addFragment(welcomeFragment)
        adapter.addFragment(playlistListFragment)
        adapter.addFragment(albumListFragment)
        adapter.addFragment(trackListFragment)

        pager.adapter = adapter
        if (settings.ip_address.isBlank()){
            pager.setCurrentItem(0, false)
        } else {
            pager.setCurrentItem(2, false)
        }
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

        val picture: ImageView = findViewById(R.id.mainAlbumArt)

        if (settings.ip_address.isNotBlank()) {
            picasso.load("http://${settings.ip_address}:7814/api1/pic/medium/" + controller.tauonStatus.track.id)
                .into(picture)
        }

        if (controller.tauonStatus.status == "playing") {
            playButton.setBackgroundResource(R.drawable.ic_pause)
        } else {
            playButton.setBackgroundResource(R.drawable.ic_play_arrow)
        }


    }


    fun backSend() {
        controller.hitApi("back")
        fetchStatus()
    }


    fun fetchTrackList(playlistId: String, album_id: Int, go_to_tracks: Boolean = false) {

        if (settings.ip_address.isBlank()){
            return
        }

        // Get albums
        println("Attempt fetch TRACKLIST")

        val base_url = "http://${settings.ip_address}:7814/api1/albumtracks/$playlistId/$album_id"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Request tracks failed")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                response.body?.close()
                println("Got request tracks")
                println(body)


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
        println("Attempt fetch albums")
        println(playlistId)

        val base_url = "http://${settings.ip_address}:7814/api1/albums/$playlistId"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Request albums failed")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                response.body?.close()
                println("Got request albums")
                println(body)

                val gson = GsonBuilder().create()

                runOnUiThread {
                    albums.clear()
                    albums.addAll(gson.fromJson(body, AlbumData::class.java).albums)
                    //println(albums[0].title)
                    albumListFragment.update()
                    got_albums = true
                    if (go_to_albums){
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
                    println(controller.tauonStatus.progress)
                    println(controller.getFormattedProgress())
                    timeProgress.text = controller.getFormattedProgress()
                    seekBar.setProgress(
                            controller.getProgress1k(),
                            false)
                }
            }

        }

        if (settings.ip_address.isBlank() || controller.mode != 1){
            return
        }
        // Get playlists
        if (!got_playlists) {

            println("Attempt fetch playlists")
            val base_url = "http://${settings.ip_address}:7814/api1/playlists"
            val request = Request.Builder().url(base_url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Request playlists failed")
                    println(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    response.body?.close()
                    println("Got request playlists")
                    println(body)

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



        println("Attempt get now playing json")
        val base_url = "http://${settings.ip_address}:7814/api1/status"
        val request = Request.Builder().url(base_url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Request failed")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                response.body?.close()
                println("Got request")
                println(body)

                val gson = GsonBuilder().create()
                controller.tauonStatus = gson.fromJson(body, controller.tauonStatus::class.java)


                if (!got_albums && controller.tauonStatus.playlist.isNotEmpty()) {
                    fetchAlbums(controller.tauonStatus.playlist)
                    got_albums = true
                    controller.activePlaylistViewing = controller.tauonStatus.playlist

                }
                if (!got_tracks) {
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








