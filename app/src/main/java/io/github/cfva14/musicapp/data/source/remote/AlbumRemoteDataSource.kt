package io.github.cfva14.musicapp.data.source.remote

import com.google.firebase.database.*
import io.github.cfva14.musicapp.data.Album
import io.github.cfva14.musicapp.data.Track
import io.github.cfva14.musicapp.data.source.AlbumDataSource

/**
 * Created by Carlos Valencia on 12/11/17.
 */

object AlbumRemoteDataSource : AlbumDataSource {

    private val databaseRef = FirebaseDatabase.getInstance().reference

    override fun getAlbum(albumId: String, callback: AlbumDataSource.GetAlbumCallback) {
        val albumRef = databaseRef.child("album/" + albumId)

        val albumListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                callback.onDataNotAvailable()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val album = p0?.getValue<Album>(Album::class.java)
                callback.onAlbumLoaded(album!!)
            }
        }

        albumRef.addListenerForSingleValueEvent(albumListener)
    }

    override fun getTracks(albumId: String, callback: AlbumDataSource.GetTracksCallback) {
        val trackRef = databaseRef.child("track").orderByChild("albumId").equalTo(albumId)

        val trackListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                callback.onDataNotAvailable()
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val tracks: MutableList<Track> = ArrayList()
                p0?.children?.forEach {
                    tracks.add(it.getValue(Track::class.java)!!)
                }
                callback.onTracksLoaded(tracks)
            }
        }

        trackRef.addListenerForSingleValueEvent(trackListener)
    }
}