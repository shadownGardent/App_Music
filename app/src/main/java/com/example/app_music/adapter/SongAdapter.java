package com.example.app_music.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_music.R;
import com.example.app_music.service.model.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private final List<Song> songs;
    private final OnItemClickListener listener;

    public SongAdapter(OnItemClickListener listener) {
        this.songs = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adpaterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new ViewHolder(adpaterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(songs.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSongs(List<Song> songs) {
        this.songs.clear();
        this.songs.addAll(songs);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar;
        TextView textSongName;
        TextView textArtist;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            textSongName = itemView.findViewById(R.id.text_name);
            textArtist = itemView.findViewById(R.id.text_artist);
        }

        public void bind(Song song, OnItemClickListener listener) {
            textSongName.setSingleLine(true);
            textSongName.setText(song.getName());
            textArtist.setSingleLine(true);
            textArtist.setText(song.getArtist());
            setImage(song);
            view.setOnClickListener(l -> listener.onItemClick(song));
        }

        private void setImage(Song song ) {
            Picasso.get().load(song.getAvatar()).into(imageAvatar);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }
}
