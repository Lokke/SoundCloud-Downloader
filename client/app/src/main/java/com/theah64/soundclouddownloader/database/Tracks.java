package com.theah64.soundclouddownloader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.theah64.soundclouddownloader.models.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 9/12/16.
 */

public class Tracks extends BaseTable<Track> {

    private static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SOUNDCLOUD_URL = "soundcloud_url";
    public static final String COLUMN_DOWNLOAD_ID = "download_id";
    private static final String TABLE_NAME_TRACKS = "tracks";
    private static final String COLUMN_PLAYLIST_ID = "playlist_id";
    private static final String X = Tracks.class.getSimpleName();
    private static final String COLUMN_ABS_FILE_PATH = "abs_file_path";
    public static final String COLUMN_IS_DOWNLOADED = "is_downloaded";
    private static Tracks instance;

    Tracks(Context context) {
        super(context, TABLE_NAME_TRACKS);
    }

    public static Tracks getInstance(final Context context) {
        if (instance == null) {
            instance = new Tracks(context);
        }
        return instance;
    }


    //title, url, download_id, artwork_url
    @Override
    public long add(Track track) {

        Log.d(X, "Adding new track to database : " + track.toString());

        final ContentValues cv = new ContentValues(4);
        cv.put(COLUMN_TITLE, track.getTitle());
        cv.put(COLUMN_SOUNDCLOUD_URL, track.getSoundCloudUrl());
        cv.put(COLUMN_DOWNLOAD_ID, track.getDownloadId());
        cv.put(COLUMN_ARTWORK_URL, track.getArtWorkUrl());
        cv.put(COLUMN_PLAYLIST_ID, track.getPlaylistId());
        cv.put(COLUMN_ABS_FILE_PATH, track.getAbsoluteFilePath());
        cv.put(COLUMN_IS_DOWNLOADED, track.isDownloaded());

        final long trackId = this.getWritableDatabase().insert(TABLE_NAME_TRACKS, null, cv);

        if (trackId == -1) {
            throw new IllegalArgumentException("Failed to insert new track");
        }

        return trackId;
    }


    public List<Track> getAll(@Nullable final String playlistId) {
        List<Track> trackList = null;

        final Cursor c = this.getReadableDatabase().query(TABLE_NAME_TRACKS, new String[]{COLUMN_ARTWORK_URL, COLUMN_TITLE, COLUMN_DOWNLOAD_ID, COLUMN_ABS_FILE_PATH, COLUMN_IS_DOWNLOADED}, playlistId != null ? "playlist_id = ?" :
                        null, playlistId != null ? new String[]{playlistId} : null
                , null, null, COLUMN_ID + " DESC");
        if (c != null && c.moveToFirst()) {

            trackList = new ArrayList<>(c.getCount());

            do {
                final String artworkUrl = c.getString(c.getColumnIndex(COLUMN_ARTWORK_URL));
                final String title = c.getString(c.getColumnIndex(COLUMN_TITLE));
                final String absoluteFilePath = c.getString(c.getColumnIndex(COLUMN_ABS_FILE_PATH));
                final boolean isDownloaded = c.getString(c.getColumnIndex(COLUMN_IS_DOWNLOADED)).equals(TRUE);

                trackList.add(new Track(null, title, null, null, null, artworkUrl, null, null, null, false, isDownloaded, absoluteFilePath));
            } while (c.moveToNext());
        }


        if (c != null) {
            c.close();
        }

        return trackList;
    }

}
