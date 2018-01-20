package com.arpan.musicplayer.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arpan.musicplayer.GlideApp
import com.arpan.musicplayer.activity.MainActivity
import com.arpan.musicplayer.R
import com.arpan.musicplayer.fragment.SongsListFragment
import com.arpan.musicplayer.model.Song
import java.util.ArrayList

import kotlinx.android.synthetic.main.song_list_item.view.*

class SongsListAdapter(private val mContext: Context?, private var mMainActivity: MainActivity, private val mSongsListFragment: SongsListFragment, private val mSongs: ArrayList<Song>) : RecyclerView.Adapter<SongsListAdapter.SongsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.song_list_item, parent, false)
        return SongsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        val song = mSongs[position]
        holder.bindViews(song.songName, song.artistName, song.albumArtUri)
    }

    override fun getItemCount(): Int {
        return mSongs.size
    }

    inner class SongsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindViews(songName: String, artistName: String, albumArtUri: Uri) {
            itemView.songNameLabel.text = songName
            itemView.songNameLabel.isSelected = true
            itemView.artistNameLabel.text = artistName
            itemView.artistNameLabel.isSelected = true

            GlideApp.with(mContext)
                    .load(albumArtUri)
                    .circleCrop()
                    .placeholder(R.drawable.ic_compact_disc)
                    .into(itemView.songAlbumArt)
        }

        override fun onClick(view: View) {
            mMainActivity.controlPlayer(MainActivity.PLAY, mSongs[adapterPosition], adapterPosition)
        }

    }

}
