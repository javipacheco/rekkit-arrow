package rekkit.models

sealed class States {

    data class NewsState(val items: List<NewsItemState>) : States()

    data class NewsItemState(
            val id: String,
            val name: String,
            val author: String,
            val title: String,
            val num_comments: Int,
            val created: Long,
            val thumbnailUrl: String,
            val imageUrl: String?,
            val url: String
    ) : States()

}
