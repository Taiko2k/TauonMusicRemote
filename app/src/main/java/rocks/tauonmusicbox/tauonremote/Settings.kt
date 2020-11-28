package rocks.tauonmusicbox.tauonremote

import android.content.Context
import android.content.SharedPreferences
import java.util.prefs.Preferences

class Settings(val activity: MainActivity) {

    lateinit var sharedPref: SharedPreferences

    var ip_address: String = ""

    fun load_settings(){
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        ip_address = sharedPref.getString("hostname", "").toString()

    }

    fun set_ip(text: String){
        sharedPref.edit().putString("hostname", text).apply()
        ip_address = text
    }

    fun setMode(mode: Int){
        sharedPref.edit().putInt("mode", mode).apply()
    }

    fun getMode(): Int {
        return sharedPref.getInt("mode", 1)
    }

}