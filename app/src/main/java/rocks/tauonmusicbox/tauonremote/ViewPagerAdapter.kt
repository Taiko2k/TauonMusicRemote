package rocks.tauonmusicbox.tauonremote

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager): FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragmentList = ArrayList<Fragment>()

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return "Welcome"
            1 -> return "Playlists"
            2 -> return "Albums"
            3 -> return "Tracks"
            4 -> return "Now Playing"
        }

        return "Test" //"OBJECT ${(position + 1)}"
    }


    fun addFragment(fragment: Fragment){
        mFragmentList.add(fragment)
    }
}