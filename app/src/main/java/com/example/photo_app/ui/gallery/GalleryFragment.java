package com.example.photo_app.ui.gallery;

import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_app.R;
import com.example.photo_app.adapter.RecyclerViewAdapter;
import com.example.photo_app.databinding.FragmentGalleryBinding;
import com.example.photo_app.helper.GallerySection;
import com.example.photo_app.model.ImageModel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.example.photo_app.MainActivity.imagePaths;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    private RecyclerView imagesRV;
    private RecyclerViewAdapter imageRVAdapter;
    private ImageModel imageModel;

    List<GallerySection> sectionList = new ArrayList<>();

    Handler loadGalleryHandler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imagesRV = binding.getRoot().findViewById(R.id.idRVImages);
        prepareRecyclerView();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void prepareRecyclerView() {
        imageModel = new ImageModel();
        imagePaths = imageModel.getAllImage(getContext());

        Thread myBackgroundThread = new Thread( backgroundTask, "load images service");
        myBackgroundThread.start();
    }

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                imagesRV.setLayoutManager(mLayoutManager);
                imagesRV.setAdapter(imageRVAdapter);
                imagesRV.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

            }
            catch (Exception e) { Log.e("<<foregroundTask>>", e.getMessage()); }
        }
    };

    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
            HashMap<Date, List<ImageModel>> hashMap = new HashMap<>();


            for (int i = 0; i <imagePaths.size(); i++){

                ExifInterface intf = null;
                File file = new File(imagePaths.get(i).getPath());

                if(intf == null)
                {
                    Date lastModDate = new Date(file.lastModified());

                    SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
                    Date newDate = null;
                    Date dDate = null;
                    try {
                        newDate = format.parse( lastModDate.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    format = new SimpleDateFormat("dd-MM-yyyy");
                    String date = format.format(newDate);

                    try {
                        dDate = new Date(format.parse(date).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (!hashMap.containsKey(dDate)) {
                        List<ImageModel> list = new ArrayList<>();
                        list.add(imagePaths.get(i));

                        hashMap.put(dDate, list);
                    } else {
                        hashMap.get(dDate).add(imagePaths.get(i));
                    }
                }

            }

            //sort item
            Map<Date, List<ImageModel>> hashMap2 = new TreeMap(Collections.reverseOrder());
            hashMap2.putAll(hashMap);

            for (Map.Entry<Date, List<ImageModel>> entry: hashMap2.entrySet()) {

                Date sectionGallery = entry.getKey();
                List<ImageModel> images = new ArrayList<>(entry.getValue());

                sectionList.add(new GallerySection(sectionGallery, images));
            }

            imageRVAdapter = new RecyclerViewAdapter(getContext(), sectionList, imagePaths);
            loadGalleryHandler.post(foregroundRunnable);
        }
    };


}