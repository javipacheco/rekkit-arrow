package rekkit.services

import akme.toDeferredK
import arrow.effects.DeferredK
import arrow.effects.IO
import rekkit.api.RedditApi
import rekkit.models.States
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiService {

    private val redditApi: RedditApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        redditApi = retrofit.create(RedditApi::class.java)
    }

    fun getNews(limit: Int, before: String? = null): DeferredK<List<States.NewsItemState>> =
            redditApi.getTop(
                    before ?: "",
                    limit.toString()).toDeferredK().map { it.data.children.map { it.data.toNewsItemState() } }

    fun empty(): IO<List<States.NewsItemState>> = IO.just(emptyList())

}