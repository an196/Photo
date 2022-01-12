package com.example.photo_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_app.R;
import com.example.photo_app.helper.GallerySection;
import com.example.photo_app.model.ImageModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private Context context;
    private List<GallerySection> sectionList;
    private ArrayList<ImageModel> images;

    // on below line we have created a constructor.
    public RecyclerViewAdapter(Context context, List<GallerySection> sectionList, ArrayList<ImageModel> images ) {
        this.context = context;
        this.sectionList = sectionList;
        this.images = images;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_gallery_row, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        GallerySection section = sectionList.get(position);

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        Date newDate = null;
        try {
            newDate = format.parse( section.getDate().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd-MM-yyyy");
        String dateSection = format.format(newDate);


        List<ImageModel> items = section.getSectionItems();

        holder.sectionGalleryTextView.setText(dateSection);
        ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(items,images);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context.getApplicationContext(), 3);
        holder.childRecyclerView.setLayoutManager(layoutManager);
        holder.childRecyclerView.setAdapter(childRecyclerAdapter);

    }

    @Override
    public int getItemCount() {

        return sectionList.size();
    }

    // View Holder Class to handle Recycler View.
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView sectionGalleryTextView;
        RecyclerView childRecyclerView;



        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            sectionGalleryTextView = itemView.findViewById(R.id.sectionGalleryTextView);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
        }
    }
}

