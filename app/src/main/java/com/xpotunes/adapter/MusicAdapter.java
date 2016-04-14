package com.xpotunes.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xpotunes.R;
import com.xpotunes.pojo.Music;

import java.util.ArrayList;

public class MusicAdapter extends ArrayAdapter<Music> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private final ArrayList<Music> mList;

    public MusicAdapter(Context context, int layoutResourceId, ArrayList<Music> mListDispenser) {
        super(context, layoutResourceId, mListDispenser);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mList = mListDispenser;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        MusicHolder holder = null;

        if (row == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new MusicHolder();

            View view = row.findViewById(R.id.titleTextView);
            if (view instanceof TextView) {
                holder.titleTextView = (TextView) view;
            }

            view = row.findViewById(R.id.subTitleTextView);
            if (view instanceof TextView) {
                holder.subTitleTextView = (TextView) view;
            }

            view = row.findViewById(R.id.statusTextViewAd);
            if (view instanceof TextView) {
                holder.statusTextView = (TextView) view;
            }

            row.setTag(holder);
        } else {
            holder = (MusicHolder) row.getTag();
        }

        Music music = mList.get(position);

        holder.titleTextView.setText(music.getTitle());
        holder.subTitleTextView.setText(String.format("%s - %s", music.getAlbum(), music.getArtist()));

        if (music.isTrailer()) {
            holder.statusTextView.setText("Trailer");
        } else {
            holder.statusTextView.setText("Full");
        }

        return row;

    }

    static class MusicHolder {
        TextView titleTextView;
        TextView subTitleTextView;
        TextView statusTextView;
    }
}
