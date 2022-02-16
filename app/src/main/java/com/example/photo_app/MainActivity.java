package com.example.photo_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.photo_app.databinding.ActivityMainBinding;
import com.example.photo_app.model.ImageModel;
import com.example.photo_app.model.VideoModel;
import com.example.photo_app.ui.album.AlbumFragment;
import com.example.photo_app.ui.gallery.GalleryFragment;
import com.example.photo_app.ui.video.VideoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavigationView mNavigationView;
    private BottomNavigationView mBottomNavigationView;
    private  ImageView imageView;

    private  static  final  int  FRAGMENT_HOME = 1;
    private  static  final  int  FRAGMENT_GALLERY = 2;
    private  static  final  int  FRAGMENT_SLIDESHOW = 3;
    private  int currentFragment = FRAGMENT_HOME;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int TAKE_PHOTO = 101;
    public static final int CREATE_IMAGE = 14;

    public static ArrayList<VideoModel> videoArrayList;
    public static ArrayList<ImageModel> imagePaths;


    private String currentPhotoPath;
    private Dialog dialog;

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


//        replaceFragment(new GalleryFragment());
//        mBottomNavigationView.getMenu().findItem(R.id.action_gallery).setChecked(true);
//        mNavigationView.setCheckedItem(R.id.nav_gallery);
//        setTileToolbar();
    }

    @Override
    protected  void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.more_options, menu);

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
                setTileToolbar();
                mBottomNavigationView.getMenu().findItem(R.id.action_album).setChecked(true);
                break;

            case R.id.nav_video:
                openVideoFragment();
                setTileToolbar();
                mBottomNavigationView.getMenu().findItem(R.id.action_video).setChecked(true);
                break;
            default:
                break;
        }
        setTileToolbar();
        return true;
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newer:

                return true;
            case R.id.older:
                return true;
            case R.id.two:
                return true;

            case R.id.three:
                return true;

            case R.id.four:

                return true;

            case R.id.five:

                return true;
            case R.id.camera:
                openCamera();
                return true;

            case R.id.setting:
                goToSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
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

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + getPackageName());
        intent.setData(uri);
        startActivity(intent);
    }

    public void openCamera() {
        askCameraPermission();

    }
    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, TAKE_PHOTO);
        } else {
            openCameraApp();
        }
    }

    private void askStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_PHOTO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraApp();
            } else {
                Toast.makeText(this, "Camera permission required to take picture", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fun Gallery", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
//                askStoragePermission();

            }
        }

    }

    private void openCameraApp() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Intent saving
            //createImage();

            // Create the File where the photo should go
            File photoFile = null;
            //API > 29
            //photoFile = createImageFile();
            //photoFile = new File(currentPhotoPath);
            //Log.d("PHOTO_FILE", photoFile.toString());
            // Continue only if the File was successfully created
            //if (photoFile != null) {
            //scanFile(this, new File(currentPhotoPath), "image/*");
            Uri photoURI = saveImage();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, TAKE_PHOTO);
//            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ResultActivity", "Result");

        if (requestCode == TAKE_PHOTO) {
            AssetFileDescriptor fileDescriptor = null;
            long fileSize = 0;
            try {
                fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(Uri.parse(currentPhotoPath), "r");
                fileSize = fileDescriptor.getLength();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (fileSize == 0) {
                try {
                    deleteCacheImage(Uri.parse(currentPhotoPath));
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_OK) {

                Log.d("SIZE_FILE", String.valueOf(fileSize));
                if (fileSize != 0) {
                    Toast.makeText(this, "Image captured", Toast.LENGTH_SHORT).show();
                    File imgFile = new File(currentPhotoPath);
                    dialog = new Dialog(MainActivity.this, R.style.DialogBox) {
                        public boolean dispatchTouchEvent(MotionEvent event) {
                            dialog.dismiss();
                            return false;
                        }
                    };
                    dialog.setContentView(R.layout.image_preview);

                    imageView = dialog.findViewById(R.id.imageView);
                    Glide.with(dialog.getContext())
                            .load(Uri.parse(currentPhotoPath))
                            .error(R.drawable.ic_launcher_foreground)
                            .into(imageView);
                    dialog.show();

                    galleryAddPicNotify(imgFile);
                }
            }
        }
//
//        if (requestCode == adapter.DELETE_REQUEST_CODE) {
//            if (resultCode != 0) {
//                adapter.notifyItemRemoved(adapter.getPos());
//            }
//        }


        if (requestCode == CREATE_IMAGE) {
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri saveImage() {
        ContentResolver contentResolver = getContentResolver();
        String imageFilename = String.valueOf(System.currentTimeMillis());

        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFilename + ".jpg");
        /**
         * if you use VOLUME_EXTERNAL_PRIMARY
         * Pictures/ or DCIM/ only
         *contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/"+"My Custom Directory");
         */
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Fun Zone/");
        Uri uri = contentResolver.insert(imageCollection, contentValues);
        File imgFile = new File(String.valueOf(uri));
        Log.d("SAVED_IMAGE", uri.toString());
        Log.d("SAVED_IMAGE", imgFile.getAbsolutePath());
        Log.d("SAVED_IMAGE", imageCollection.toString());
        currentPhotoPath = String.valueOf(uri);

        return uri;
    }

    private void deleteCacheImage(Uri uri) throws IntentSender.SendIntentException {
        // Remove a specific media item.
        ContentResolver resolver = getContentResolver();

        // URI of the image to remove.
        Uri imageUri = Uri.parse(currentPhotoPath);

        // WHERE clause.
        String selection = "...";
        String[] selectionArgs = null;

        // Perform the actual removal.
        int numImagesRemoved = resolver.delete(
                imageUri,
                null,
                null);
    }

    private void galleryAddPicNotify(File imgFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imgFile.getAbsolutePath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


}