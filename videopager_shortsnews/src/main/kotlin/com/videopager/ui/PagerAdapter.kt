package com.videopager.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.player.models.VideoData
import com.player.ui.AppPlayerView
import com.videopager.R
import com.videopager.databinding.PageItemBinding
import com.videopager.models.PageEffect
import com.videopager.ui.extensions.ClickEvent
import com.videopager.ui.extensions.awaitNextLayout
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive


/**
 * The two main functions of interest here are [attachPlayerView] and [showPlayerFor].
 *
 * [attachPlayerView] attaches a [AppPlayerView] instance to a page's View hierarchy. Once it
 * is on-screen, its ExoPlayer instance will be eligible to start rendering frames.
 *
 * [showPlayerFor] hides the video image preview of a given ViewHolder so that video playback can
 * be visible. It's called when the ExoPlayer instance has started rendering its first frame.
 */
internal class PagerAdapter(
    private val imageLoader: ImageLoader
) : ListAdapter<VideoData, PageViewHolder>(VideoDataDiffCallback) {
    private var recyclerView: RecyclerView? = null
    // Extra buffer capacity so that emissions can be sent outside a coroutine
    private val clicks = MutableSharedFlow<Pair<String, ClickEvent>>(extraBufferCapacity = 1)
    fun clicks() = clicks.asSharedFlow()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return LayoutInflater.from(parent.context)
            .let { inflater -> PageItemBinding.inflate(inflater, parent, false) }
            .let { binding ->
                PageViewHolder(binding, imageLoader) {pair ->
                    clicks.tryEmit(pair)
                }
            }
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        getItem(position).let(holder::bind)
    }

    fun getVideoData(position: Int):VideoData {
        return getItem(position)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
    }

    /**
     * Attach [appPlayerView] to the ViewHolder at [position]. The player won't actually be visible in
     * the UI until [showPlayerFor] is also called.
     */
    suspend fun attachPlayerView(appPlayerView: AppPlayerView, position: Int) {
        awaitViewHolder(position).attach(appPlayerView)
    }

    // Hides the video preview image when the player is ready to be shown.
    suspend fun showPlayerFor(position: Int) {
        awaitViewHolder(position).hidePreviewImage()
    }

    suspend fun renderEffect(position: Int, effect: PageEffect) {
        awaitViewHolder(position).renderEffect(effect)
    }

    suspend fun refreshUI(position: Int) {
        val holder = awaitViewHolder(position)
        var isTextViewClicked = false
        var data  = getItem(position)
        holder.binding.msgCount.text = data.comment_count
        holder.binding.likeTitle.text = data.like_count
        holder.binding.title.text = data.title
        if (data.liking){
            holder.binding.like.setColorFilter(ContextCompat.getColor(holder.binding.like.context, R.color.red))
        } else {
            holder.binding.like.setColorFilter(ContextCompat.getColor(holder.binding.like.context, R.color.white))
        }
        if (data.following){
            holder.binding.following.text = "Following"
        } else{
            holder.binding.following.text = "Follow"
        }
        if (data.channel_image.isNotEmpty()) {
            holder.binding.clientImage.loadSvg(data.channel_image, holder.binding.clientImage.context)
        }
        holder.binding.title.setOnClickListener {
            if (isTextViewClicked){
                holder.binding.title.maxLines = 3
                isTextViewClicked = false
            } else{
                holder.binding.title.maxLines  = Int.MAX_VALUE
                isTextViewClicked = true
            }
        }

    }

    /**
     * The ViewHolder at [position] isn't always immediately available. In those cases, wait for
     * the RecyclerView to be laid out and re-query that ViewHolder.
     */
    private suspend fun awaitViewHolder(position: Int): PageViewHolder {
        if (currentList.isEmpty()) error("Tried to get ViewHolder at position $position, but the list was empty")

        var viewHolder: PageViewHolder?

        do {
            viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? PageViewHolder
        } while (currentCoroutineContext().isActive && viewHolder == null && recyclerView?.awaitNextLayout() == Unit)

        return requireNotNull(viewHolder)
    }

    private object VideoDataDiffCallback : DiffUtil.ItemCallback<VideoData>() {
        override fun areItemsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoData, newItem: VideoData): Boolean {
            return oldItem == newItem
        }
    }

    private fun ImageView.loadSvg(url: String, context:Context) {

        val imageLoader = ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }


}
