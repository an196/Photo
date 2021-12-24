package com.example.photo_app.ui.album;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.photo_app.PhotosActivity;
import com.example.photo_app.R;
import com.example.photo_app.adapter.Adapter_PhotosFolder;
import com.example.photo_app.databinding.FragmentAlbumBinding;
import com.example.photo_app.model.AlbumModel;
import com.example.photo_app.model.Model_images;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {

    private AlbumViewModel albumViewModel;
    private FragmentAlbumBinding binding;
    //album
    public static ArrayList<Model_images> al_images;
    private Adapter_PhotosFolder obj_adapter;
    private GridView gv_folder;
    private AlbumModel albumModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        albumViewModel =
                new ViewModelProvider(this).get(AlbumViewModel.class);

        binding = FragmentAlbumBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //album fragment
        albumModel = new AlbumModel();
        al_images = albumModel.getAllAlbum(getContext());
        gv_folder = (GridView)root.findViewById(R.id.gv_folder);
        obj_adapter = new Adapter_PhotosFolder(getContext().getApplicationContext(),al_images);
        gv_folder.setAdapter(obj_adapter);
        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(root.getContext().getApplicationContext(), PhotosActivity.class);
                intent.putExtra("value",i);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}