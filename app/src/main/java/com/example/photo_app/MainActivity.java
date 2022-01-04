package com.example.photo_app;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.photo_app.databinding.ActivityMainBinding;
import com.example.photo_app.model.ImageModel;
import com.example.photo_app.model.VideoModel;
import com.example.photo_app.ui.album.AlbumFragment;
import com.example.photo_app.ui.gallery.GalleryFragment;
import com.example.photo_app.ui.video.VideoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavigationView mNavigationView;
    private BottomNavigationView mBottomNavigationView;

    private  static  final  int  FRAGMENT_HOME = 1;
    private  static  final  int  FRAGMENT_GALLERY = 2;
    private  static  final  int  FRAGMENT_SLIDESHOW = 3;
    private  int currentFragment = FRAGMENT_HOME;

    public static ArrayList<VideoModel> videoArrayList;
    public static ArrayList<ImageModel> imagePaths;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        mNavigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_gallery, R.id.nav_album, R.id.nav_video)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_gallery:
                                openGalleryFragment();
                                setTileToolbar();
                                mNavigationView.setCheckedItem(R.id.nav_gallery);
                                break;
                            case R.id.action_album:
                                openAlbumFragment();
                                setTileToolbar();
                                mNavigationView.setCheckedItem(R.id.nav_album);
                                break;
                            case R.id.action_video:
                                openVideoFragment();
                                setTileToolbar();
                                mNavigationView.setCheckedItem(R.id.nav_video);
                                break;
                        }
                        return true;
                    }
                }
        );


        replaceFragment(new GalleryFragment());
        mBottomNavigationView.getMenu().findItem(R.id.action_gallery).setChecked(true);
        mNavigationView.setCheckedItem(R.id.nav_gallery);
        setTileToolbar();
    }

    @Override
    protected  void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_bottom_navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int i = item.getItemId();
        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
        switch (i) {
            case R.id.nav_gallery:
                openGalleryFragment();
                setTileToolbar();
                mBottomNavigationView.getMenu().findItem(R.id.action_gallery).setChecked(true);
                break;

            case R.id.nav_album:
                openAlbumFragment();
                mBottomNavigationView.getMenu().findItem(R.id.action_album).setChecked(true);
                setTileToolbar();
                break;

            case R.id.nav_video:
                openVideoFragment();
                mBottomNavigationView.getMenu().findItem(R.id.action_video).setChecked(true);
                setTileToolbar();
                break;
            default:
                break;
        }
        setTileToolbar();
        return true;
    };

    private void openGalleryFragment(){
        if(currentFragment !=  FRAGMENT_HOME){
            replaceFragment(new GalleryFragment());
            currentFragment  = FRAGMENT_HOME;

        }
    }
    private void openAlbumFragment(){
        if(currentFragment !=  FRAGMENT_GALLERY){
            replaceFragment(new AlbumFragment());
            currentFragment  = FRAGMENT_GALLERY;
        }
    }

    private void openVideoFragment(){
        if(currentFragment !=  FRAGMENT_SLIDESHOW){
            replaceFragment(new VideoFragment());

            currentFragment  = FRAGMENT_SLIDESHOW;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main,fragment);
        transaction.commit();
    }

    private void setTileToolbar(){
        String title = "";
        switch (currentFragment){
            case FRAGMENT_HOME:
                title = getString(R.string.menu_gallery);
                break;
            case FRAGMENT_GALLERY:
                title = getString(R.string.menu_album);
                break;
            case FRAGMENT_SLIDESHOW:
                title = getString(R.string.menu_video);
                break;
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }


}