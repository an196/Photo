package com.example.photo_app.ui.video;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_app.R;
import com.example.photo_app.VideoPlayActivity;
import com.example.photo_app.adapter.VideoAdapter;
import com.example.photo_app.databinding.FragmentVideoBinding;
import com.example.photo_app.model.VideoModel;

import java.util.ArrayList;

import static com.example.photo_app.MainActivity.videoArrayList;

public class VideoFragment extends Fragment {
    private VideoViewModel videoViewModel;
    private FragmentVideoBinding binding;
    private View root;


    RecyclerView recyclerView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        videoViewModel =
                new ViewModelProvider(this).get(VideoViewModel.class);

        binding = FragmentVideoBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        videoList();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void videoList() {
        recyclerView = (RecyclerView) root.findViewById(R.id.RVVideo);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        videoArrayList = new ArrayList<>();
        getVideos();
    }

    //get video files from storage
    public void getVideos() {
        ContentResolver contentResolver = root.getContext().getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        final String orderBy = MediaStore.Video.Media.DATE_TAKEN + " DESC";
        Cursor cursor = contentResolver.query(uri, null, null, null, orderBy);

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String thumbPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));

                VideoModel videoModel  = new VideoModel ();
                videoModel.setVideoTitle(title);
                videoModel.setVideoUri(Uri.parse(data));
                videoModel.setVideoDuration(timeConversion(Long.parseLong(duration)));
                videoModel.setVideoThumbnail(thumbPath);

                videoArrayList.add(videoModel);

            } while (cursor.moveToNext());
        }

        VideoAdapter adapter = new VideoAdapter (root.getContext(), videoArrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                Intent intent = new Intent(root.getContext(), VideoPlayActivity.class);
                intent.putExtra("pos", pos);
                startActivity(intent);
            }
        });

    }

    //time conversion
    public String timeConversion(long value) {
        String videoTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }



}