package com.exo.ui

import android.view.LayoutInflater
import android.view.View
import com.exo.databinding.PlayerViewBinding
import com.exo.players.ExoAppPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.player.players.AppPlayer
import com.player.ui.AppPlayerView

/**
 * An implementation of AppPlayerView that uses ExoPlayer APIs,
 * namely [com.google.android.exoplayer2.ui.PlayerView]
 */
class ExoAppPlayerView(layoutInflater: LayoutInflater) : AppPlayerView {
    val binding = PlayerViewBinding.inflate(layoutInflater)
    override val view: View = binding.root
    override fun getPlayerView(): StyledPlayerView {
        return binding.playerView
    }

    override fun attach(appPlayer: AppPlayer) {
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        binding.playerView.player = (appPlayer as ExoAppPlayer).player

    }

    // ExoPlayer and PlayerView hold circular ref's to each other, so avoid leaking
    // Activity here by nulling it out.
    override fun detachPlayer() {
        binding.playerView.player = null
    }
}
