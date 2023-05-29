package com.videopager.ui

import android.graphics.drawable.BitmapDrawable
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.size.Scale
import com.player.ui.AppPlayerView
import com.videopager.databinding.PageItemBinding
import com.videopager.models.AnimationEffect
import com.videopager.models.PageEffect
import com.videopager.models.ResetAnimationsEffect
import com.player.models.VideoData
import com.videopager.ui.extensions.*

internal class PageViewHolder(
    val binding: PageItemBinding,
    private val imageLoader: ImageLoader,
    private val click: (pair: Pair<String, ClickEvent>) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private val animationEffect = FadeInThenOutAnimationEffect(binding.playPause)

    init {
        binding.root.setOnClickListener {
            click(Pair("", PlayPauseClick))
        }
    }

    fun bind(videoData: VideoData) {
        binding.previewImage.load(videoData.previewImageUri,imageLoader) {
            scale(Scale.FILL)
        }

        /*binding.previewImage.load(videoData.mediaUri) {
            decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
        }*/

        binding.share.setOnClickListener{
            click(Pair(videoData.id, ShareClick))
        }
        binding.save.setOnClickListener{
            click(Pair(videoData.id, BookmarkClick))
        }
        binding.comment.setOnClickListener{
            click(Pair(videoData.id, CommentClick))
        }
        binding.like.setOnClickListener{
            click(Pair(videoData.id, LikeClick))
        }

        binding.msgCount.text = videoData.comment_count
        binding.likeTitle.text = videoData.like_count
        binding.videoIdText.text = videoData.id
        ConstraintSet().apply {
            clone(binding.root)
            // Optimize video preview / container size if aspect ratio is available. This can avoid
            // a flicker when ExoPlayer renders its first frame but hasn't yet adjusted the video size.
            /*val ratio = videoData.aspectRatio?.let { "$it:0" }
            setDimensionRatio(binding.playerContainer.id, "")
            setDimensionRatio(binding.previewImage.id, "")*/

            val ratio = videoData.aspectRatio?.let { "$it:0" }
            setDimensionRatio(binding.playerContainer.id, ratio)
            setDimensionRatio(binding.previewImage.id, ratio)

            applyTo(binding.root)
        }
    }

    fun attach(appPlayerView: AppPlayerView) {
        if (binding.playerContainer == appPlayerView.view.parent) {
            // Already attached
            return
        }

        /**
         * Since effectively only one [AppPlayerView] instance is used in the app, it might currently
         * be attached to a View from a previous page. In that case, remove it from that parent
         * before adding it to this ViewHolder's View, and cleanup state from the previous ViewHolder.
         */
        appPlayerView.view.findParentById(binding.root.id)
            ?.let(PageItemBinding::bind)
            ?.apply {
                playerContainer.removeView(appPlayerView.view)
                previewImage.isVisible = true
            }

        binding.playerContainer.addView(appPlayerView.view)
    }

    fun renderEffect(effect: PageEffect) {
        when (effect) {
            is ResetAnimationsEffect -> animationEffect.reset()
            is AnimationEffect -> {
                binding.playPause.setImageResource(effect.drawable)
                animationEffect.go()
            }
        }
    }

    fun hidePreviewImage() {
        binding.previewImage.isVisible = false
    }
}
