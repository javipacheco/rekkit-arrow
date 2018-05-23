package rekkit.ui.commons

import akme.*
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import arrow.effects.DeferredK
import kotlinx.coroutines.experimental.async
import rekkit.R
import rekkit.ui.news.NewsFragment

interface NavigationService {

    fun getActivity(): Activity?

    fun goToWeb(url: String): DeferredK<Unit> = {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        getActivity()?.startActivity(i)
        Unit
    }.orException(AkmeException.NavigationException("Error GoToWeb"))

    fun loadNews(): DeferredK<Int> = {
        val fragmentTransaction = (getActivity() as AppCompatActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_activity_fragment_container, NewsFragment())
        fragmentTransaction.commitAllowingStateLoss()
    }.orException(AkmeException.NavigationException("Error loading news"))

}