package rocks.tauonmusicbox.tauonremote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class WelcomeFragment(private val settings: Settings, val activity: MainActivity) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weclome, container, false)

        val ipEntry: EditText = view.findViewById(R.id.ipAddressText)
        val ipButton: Button = view.findViewById(R.id.setIPButton)
        ipEntry.setText(settings.ip_address)
        ipButton.setOnClickListener {
            settings.set_ip(ipEntry.text.toString())
            Toast.makeText(activity, "Saved setting", Toast.LENGTH_SHORT).show()
        }

        return view
    }

}