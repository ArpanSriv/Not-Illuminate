package com.arpan.musicplayer.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arpan.musicplayer.R
import com.arpan.musicplayer.model.Album
import com.arpan.musicplayer.model.Artist
import kotlinx.android.synthetic.main.artist_list_item.view.*

// Created on 12/19/2017

class ArtistListAdapter (
        val mContext: Context?,
        val mArtistList: ArrayList<Artist>
    ) : RecyclerView.Adapter<ArtistListAdapter.ViewHolder>() {

    lateinit var mCurrentArtist: Artist

    private lateinit var mCallbackHandler: HandleCallbackFromAdapter

    interface HandleCallbackFromAdapter {
        fun handleClick(artist: Artist)
    }

    fun setAdapterCallback(callbackFromAdapter: HandleCallbackFromAdapter) {
        mCallbackHandler = callbackFromAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.artist_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = mArtistList[position]
        mCurrentArtist = artist
        holder.bindViews(artist.artistName, null, null) //FIXME: DO A LAST.FM REQUEST AND GET ARTIST IMAGE
    }

    override fun getItemCount(): Int {
        return mArtistList.size
    }



    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mCallbackHandler.handleClick(mCurrentArtist)
        }

        fun bindViews(artistName: String, artistImageUri: Uri?, albums: ArrayList<Album>?) {
            itemView.artistNameLabel_artistList.text = artistName
        }

    }


}