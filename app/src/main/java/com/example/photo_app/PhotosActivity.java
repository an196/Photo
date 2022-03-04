package com.example.photo_app;

import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo_app.adapter.RecyclerViewAdapter;
import com.example.photo_app.helper.GallerySection;
import com.example.photo_app.model.ImageModel;
import com.example.photo_app.ui.album.AlbumFragment;

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

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private List<GallerySection> sectionList = new ArrayList<>();
    private RecyclerViewAdapter imageRVAdapter;
    private RecyclerView imagesRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable( getResources().getColor(R.color.yellow,null)));
        imagesRV = findViewById(R.id.idRVImages);

        int_position = getIntent().getIntExtra("value", 0);

        fillIntoSection();
//        adapter = new GridViewAdapter(this, AlbumFragment.al_images, int_position);
//        gridView.setAdapter(adapter);
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ArrayList<ImageModel> album = AlbumFragment.al_images.get(int_position)
//                        .getAl_imagepath();
//
//                // inside on click listener we are creating a new intent
//                Intent i = new Intent(PhotosActivity.this, ImageDetailActivity.class);
//
//                // on below line we are passing the image path to our new activity.
//                i.putExtra("album", (ArrayList<ImageModel>) album);
//                i.putExtra("position", position);
//                // at last we are starting our activity.
//                startActivity(i);
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillIntoSection() {
        HashMap<Date, List<ImageModel>> hashMap = new HashMap<>();


        for (int i = 0; i < AlbumFragment.al_images.get(int_position).getAl_imagepath().size(); i++) {

            ExifInterface intf = null;
            File file = new File(AlbumFragment.al_images.get(int_position).getAl_imagepath().get(i).getPath());

            if (intf == null) {
                Date lastModDate = new Date(file.lastModified());

                SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
                Date newDate = null;
                Date dDate = null;
                try {
                    newDate = format.parse(lastModDate.toString());
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
                    list.add(AlbumFragment.al_images.get(int_position).getAl_imagepath().get(i));

                    hashMap.put(dDate, list);
                } else {
                    hashMap.get(dDate).add(AlbumFragment.al_images.get(int_position).getAl_imagepath().get(i));
                }
            }

        }

        //sort item
        Map<Date, List<ImageModel>> hashMap2 = new TreeMap(Collections.reverseOrder());
        hashMap2.putAll(hashMap);

        for (Map.Entry<Date, List<ImageModel>> entry : hashMap2.entrySet()) {

            Date sectionGallery = entry.getKey();
            List<ImageModel> images = new ArrayList<>(entry.getValue());

            sectionList.add(new GallerySection(sectionGallery, images));
        }

        imageRVAdapter = new RecyclerViewAdapter(getApplicationContext(), sectionList,AlbumFragment.al_images.get(int_position).getAl_imagepath());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        imagesRV.setLayoutManager(mLayoutManager);
        imagesRV.setAdapter(imageRVAdapter);
    }
}