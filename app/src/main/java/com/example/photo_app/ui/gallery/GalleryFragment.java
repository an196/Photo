package com.example.photo_app.ui.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_app.R;
import com.example.photo_app.adapter.RecyclerViewAdapter;
import com.example.photo_app.databinding.FragmentGalleryBinding;
import com.example.photo_app.model.ImageModel;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    private RecyclerView imagesRV;
    private RecyclerViewAdapter imageRVAdapter;
    private ImageModel imageModel;

    private ArrayList<String> imagePaths =null;
    Handler loadGallryHandler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prepareRecyclerView();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void prepareRecyclerView() {
        imageModel = new ImageModel();
        imagePaths = imageModel.getAllImage(getContext());
        imagesRV = binding.getRoot().findViewById(R.id.idRVImages);
        Thread myBackgroundThread = new Thread( backgroundTask, "load images service");
        myBackgroundThread.start();
    }

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
                imagesRV.setLayoutManager(manager);
                imagesRV.setAdapter(imageRVAdapter);

            }
            catch (Exception e) { Log.e("<<foregroundTask>>", e.getMessage()); }
        }
    };

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            imageRVAdapter = new RecyclerViewAdapter(getContext(), imagePaths);
            loadGallryHandler.post(foregroundRunnable);
        }
    };
}