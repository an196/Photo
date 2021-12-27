package com.example.photo_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photo_app.adapter.GridViewAdapter;
import com.example.photo_app.ui.album.AlbumFragment;

import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_album);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        gridView = (GridView)findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);


        adapter = new GridViewAdapter(this, AlbumFragment.al_images,int_position);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ArrayList<String> album =  AlbumFragment.al_images.get(int_position)
                        .getAl_imagepath();


                // inside on click listener we are creating a new intent
                Intent i = new Intent(PhotosActivity.this, ImageDetailActivity.class);

                // on below line we are passing the image path to our new activity.
                i.putExtra("album", album);
                i.putExtra("position", position);
                // at last we are starting our activity.
                PhotosActivity.this.startActivity(i);
            }
        });
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
}