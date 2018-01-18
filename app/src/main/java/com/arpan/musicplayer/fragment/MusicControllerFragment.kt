package com.arpan.musicplayer.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import com.arpan.musicplayer.GlideApp
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.R
import com.arpan.musicplayer.model.Song
import com.arpan.musicplayer.service.PlayerService

import kotlinx.android.synthetic.main.fragment_music_controller.*

class MusicControllerFragment : Fragment(), PlayerService.ServiceCallbacks {

    private var mDoubleBackToExitPressedOnce = false
    private val mHandler = Handler()
    private val mRunnable = Runnable { mDoubleBackToExitPressedOnce = false }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_music_controller, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mainActivity = activity as MainActivity

//        mCallbacks = mainActivity.getFragmentFromHorizontalViewPager(0) as SongsListFragment
        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, userChange: Boolean) {
                if (userChange) (activity as MainActivity).seekMusic(progress)
            }
        })

        playButton.setOnClickListener {
            when ((mainActivity).getCurrentPlayerStatusFromService()) {
                PlayerService.PLAYING -> {
                    mainActivity.controlPlayer(MainActivity.PAUSE, null, null)
                    loadImageWithoutPlaceholder(playButton, R.drawable.ic_play_button_control, false)
                }
                PlayerService.PAUSED -> {
                    mainActivity.controlPlayer(MainActivity.RESUME, null, null)
                    loadImageWithoutPlaceholder(playButton, R.drawable.ic_pause_button, false)
                }
                PlayerService.STOPPED -> { //FIXME
                    mainActivity.controlPlayer(MainActivity.RESUME, null, null)
                    loadImageWithoutPlaceholder(playButton, R.drawable.ic_play_button, false)
                }
            }
        }

        forwardButton.setOnClickListener {
            mainActivity.controlPlayer(
                    MainActivity.PLAY,
                    mainActivity.mPlayerService.getSpecificSong(
                            mainActivity.mPlayerService.mCurrentIndex,
                            PlayerService.NEXT
                    ),
                    null
            )
        }

        previousButton.setOnClickListener {
            if (mDoubleBackToExitPressedOnce || mainActivity.mPlayerService.mMediaPlayer.currentPosition == 0) {
                mainActivity.controlPlayer(
                        MainActivity.PLAY,
                        mainActivity.mPlayerService.getSpecificSong(
                                mainActivity.mPlayerService.mCurrentIndex,
                                PlayerService.PREVIOUS
                        ),
                        null
                )
            }

            mDoubleBackToExitPressedOnce = true
            mainActivity.mPlayerService.mMediaPlayer.seekTo(0)
            mHandler.postDelayed(mRunnable, 2000)
        }


    }

    override fun updateViewsByStatusCode(statusCode: Int, song: Song?) {
        when (statusCode) {

            MainActivity.PLAY -> {
                songTitle.text = song!!.songName
                songDetail_collapsed.text = getString(R.string.song_detail, song.songName, song.artistName)
                artistTitle.text = song.artistName
                loadImageWithPlaceholder(songAlbumArt_Fragment, R.drawable.ic_compact_disc, song.albumArtUri, true)
                loadImageWithPlaceholder(albumArt_collapsed, R.drawable.ic_compact_disc, song.albumArtUri, false)
                loadImageWithoutPlaceholder(playButton, R.drawable.ic_pause_button, false)
                loadImageWithoutPlaceholder(playButton_collapsed, R.drawable.ic_pause_button_custom, false)
            }

            MainActivity.PAUSE -> {
                loadImageWithoutPlaceholder(playButton, R.drawable.ic_play_button_control, false)
            }

            MainActivity.STOP -> {
                loadImageWithoutPlaceholder(playButton, R.drawable.ic_play_button ,false)
            }
        }

    }

    private fun loadImageWithoutPlaceholder (destinationView: ImageView, drawableIntId : Any, circleCropOrNot: Boolean) {
        if (circleCropOrNot) {
            GlideApp
                    .with(context)
                    .load(drawableIntId)
                    .circleCrop()
                    .into(destinationView)
        }
        else {
            GlideApp
                    .with(context)
                    .load(drawableIntId)
                    .into(destinationView)
        }
    }

    private fun loadImageWithPlaceholder (destinationView: ImageView, placeHolderImage: Int, drawableIntId : Any, circleCropOrNot: Boolean) {
        if (circleCropOrNot) {
            GlideApp
                    .with(context)
                    .load(drawableIntId)
                    .circleCrop()
                    .placeholder(placeHolderImage)
                    .into(destinationView)
        }
        else {
            GlideApp
                    .with(context)
                    .load(drawableIntId)
                    .placeholder(placeHolderImage)
                    .into(destinationView)
        }
    }
}
