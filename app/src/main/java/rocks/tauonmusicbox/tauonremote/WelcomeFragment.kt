package rocks.tauonmusicbox.tauonremote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast

class WelcomeFragment(private val settings: Settings, val controller: Controller, val activity: MainActivity) : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weclome, container, false)

        val ipEntry: EditText = view.findViewById(R.id.ipAddressText)
        val ipButton: Button = view.findViewById(R.id.setIPButton)
        val streamSwitch: Switch = view.findViewById(R.id.streamModeSwitch)
        if (controller.mode == 2){
            streamSwitch.isChecked = true
        }

        ipEntry.setText(settings.ip_address)
        ipButton.setOnClickListener {
            settings.set_ip(ipEntry.text.toString())
            Toast.makeText(activity, "Saved setting", Toast.LENGTH_SHORT).show()
        }

        streamSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                controller.setStreamMode()
            } else {
                controller.setRemoteMode()
            }
        }

        val reloadButton: Button = view.findViewById(R.id.reloadButton)
        reloadButton.setOnClickListener {
            activity.got_albums = false
            activity.got_playlists = false
            activity.got_tracks = false
            Toast.makeText(activity, "Flushing playlist data", Toast.LENGTH_SHORT).show()
        }

        return view
    }

}