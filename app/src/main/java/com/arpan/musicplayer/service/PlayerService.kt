package com.arpan.musicplayer.service

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.model.Song
import java.io.IOException

class PlayerService : Service() {

    //TODO("ADD A VAR SONGSREADY = FALSE")

    //Status Codes
    companion object {

        val PLAYING = 1
        val PAUSED = 2
        val STOPPED = 3

        val PREVIOUS = 0
        val NEXT = 1

        val TAG: String = PlayerService::class.java.simpleName
    }

    val mMediaPlayer = MediaPlayer()
    var mSongsList: ArrayList<Song> = ArrayList()
    var isSongListReady = false
    var mCurrentIndex: Int = 0

    private          var mCurrentPlayerStatus = STOPPED
    private          val mBinder: IBinder = LocalBinder()
    private lateinit var mCurrentSong: Song
    private lateinit var mServiceCallbacks: ServiceCallbacks

    interface ServiceCallbacks {
        fun updateViewsByStatusCode(statusCode: Int, song: Song?)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        fun getService() : PlayerService = this@PlayerService
    }

    //G and S
    fun getCurrentSong() : Song? = mCurrentSong

    fun setCallbacks(callbacks: ServiceCallbacks) {
        mServiceCallbacks = callbacks
    }

    //Client Methods
    fun playMusic() {
        mMediaPlayer.prepareAsync()
        mMediaPlayer.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            mServiceCallbacks.updateViewsByStatusCode(MainActivity.PLAY, mCurrentSong)
        }
        mCurrentPlayerStatus = PLAYING
    }

    fun pauseMusic() {
        mMediaPlayer.pause()
        mServiceCallbacks.updateViewsByStatusCode(MainActivity.PAUSE, null)
        mCurrentPlayerStatus = PAUSED
    }

    fun resumeMusic() {
        mMediaPlayer.start()
        mCurrentPlayerStatus = PLAYING
    }

    fun stopMusic() {
        mMediaPlayer.stop()
        mServiceCallbacks.updateViewsByStatusCode(MainActivity.STOP, null)
        mCurrentPlayerStatus = STOPPED
    }

    fun setCurrentSong(song: Song) {
        mCurrentSong = song
        try {
            mMediaPlayer.reset()
            mMediaPlayer.setDataSource(mCurrentSong.uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getCurrentPlayerStatus(): Int = mCurrentPlayerStatus

    fun loadSongs(): ArrayList<Song> {
        isSongListReady = false

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        val cursor = contentResolver.query(uri, null, selection, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songDuration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)

                    val song = Song(songName, albumName, albumId, albumArtUri, artistName, songUrl, songDuration)

                    mSongsList.add(song)
                    Log.d(TAG, "Song: Song Added" + song)

                    //Song song = new Song(name, albumName, albumId, albumArtUri, artistName, url, duration);
                    //Instead, put the values in ContentValues values = new ContentValues() Obj.
                    //mSongs.add(song);

                } while (cursor.moveToNext())
                cursor.close()
                mSongsList.sortBy { song -> song.songName }
            }
        }
        isSongListReady = true
        return mSongsList
    }

    fun getSpecificSong(index: Int, prevOrNext: Int) : Song? {
        when (prevOrNext) {
            NEXT -> {
                if (index == mSongsList.size - 1) mCurrentIndex = 0
                else mCurrentIndex = index + 1
            }
            PREVIOUS -> {
                if (index == 0) mCurrentIndex = mSongsList.size - 1
                else mCurrentIndex = index - 1
            }
        }
        return mSongsList[mCurrentIndex]
    }

}