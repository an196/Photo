package com.example.photo_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.photo_app.R;
import com.example.photo_app.model.VideoModel;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.viewHolder> {

    Context context;
    ArrayList<VideoModel> videoArrayList;
    public OnItemClickListener onItemClickListener;

    public VideoAdapter(Context context, ArrayList<VideoModel > videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, final int i) {
        //holder.title.setText(videoArrayList.get(i).getVideoTitle());
        holder.duration.setText(videoArrayList.get(i).getVideoDuration());

        Glide.with(context).load(videoArrayList.get(i).getVideoThumbnail())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into( holder.imageView);
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, duration;
        ImageView imageView;

        public viewHolder(View itemView) {
            super(itemView);
            //title = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.duration);
            imageView = (ImageView)itemView.findViewById(R.id.image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }

    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }


}