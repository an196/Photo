package com.example.photo_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.photo_app.ImageDetailActivity;
import com.example.photo_app.R;
import com.example.photo_app.model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> {
    private List<ImageModel> items;
    private Context context;
    private ArrayList<ImageModel> images;

    public ChildRecyclerAdapter(List<ImageModel> items, ArrayList<ImageModel> images) {
        this.items = items;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_layout, parent, false);

        return new ViewHolder(view);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int avd) {
        int position =  avd;

        File imgFile = new File(items.get(position).getPath());
        if (imgFile.exists()) {
            Glide.with(context).load("/"+imgFile)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into( holder.itemImageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idnex = 0;
                    for (ImageModel image: images){
                        if(image.getPath() == items.get(position).getPath()){
                            idnex = images.indexOf(image);
                        }
                    }
                    Intent i = new Intent(context, ImageDetailActivity.class);
                    i.putExtra("position", idnex);

                    context.startActivity(i);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImageView = itemView.findViewById(R.id.idCIVImage);
        }
    }
}
