package com.example.photo_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.photo_app.adapter.ViewPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.photo_app.MainActivity.imagePaths;
public class ImageDetailActivity extends AppCompatActivity {

    // creating a string variable, image view variable
    // and a variable for our scale gesture detector class.
    private int position;
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private ActionBar mActionBar;
    private File imgFile;

    // on below line we are defining our scale factor.
    private float mScaleFactor = 1.0f;
    ViewPager mViewPager;

    // Creating Object of ViewPagerAdapter
    ViewPagerAdapter mViewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);


        // calling the action bar
        mActionBar  = getSupportActionBar();

        // showing the back button in action bar
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // on below line we are initializing our scale gesture detector for zoom in and out for our image.
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // on below line getting data which we have passed from our adapter class.
        position = getIntent().getIntExtra("position", 0 );
        ArrayList<String> album = getIntent().getStringArrayListExtra("album" );
        if( album != null)
            imagePaths = album;


        mViewPager = (ViewPager)findViewById(R.id.viewPagerImageDetail);
        mViewPagerAdapter = new ViewPagerAdapter(ImageDetailActivity.this, imagePaths);

        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(position);


        // if the file exists then we are loading that image in our image view.
       // if (imgFile.exists()) {
           //loadImage();
//            imageView.setOnTouchListener(new OnSwipeTouchListener(ImageDetailActivity.this){
//                public void onSwipeTop() {
//                }
//                public void onSwipeRight() {
//                    if(position > 0 ){
//                        position--;
//                        imgFile = new File(imagePaths.get(position));
//                        Glide.with(ImageDetailActivity.this).load(imgFile)
//                                .centerCrop()
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                .skipMemoryCache(true)
//                                .into(imageView);
//                    }
//                }
//                public void onSwipeLeft() {
//
//                    if(position < imagePaths.size() -1){
//                        position++;
//                        imgFile = new File(imagePaths.get(position));
//                        Glide.with(ImageDetailActivity.this).load("/"+imgFile)
//                                .centerCrop()
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                .skipMemoryCache(true)
//                                .into(imageView);
//                    }
//                }
//                public void onSwipeBottom() {
//                }
//            });
//        }


    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // inside on touch event method we are calling on
        // touch event method and pasing our motion event to it.
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        // on below line we are creating a class for our scale
        // listener and extending it with gesture listener.
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            // inside on scale method we are setting scale
            // for our image in our image view.
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            // on below line we are setting
            // scale x and scale y to our image view.
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_share:
                shareImageToAnOntherApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_detail_top_navigation, menu);

        return true;
    }

    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        // setting type to image
        intent.setType("image/png");
        Intent chooser = Intent.createChooser(intent, "Share File");

        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        // calling startactivity() to share
        startActivity(chooser);
    }

    // Retrieving the url to share
    private Uri getImageToShare(Bitmap bitmap) {
        File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, "com.anni.shareimage.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }

    private void shareImageToAnOntherApp(){
        imageView = (ImageView)findViewById(R.id.ivImageDetail);

        //imageView.invalidate();
        Bitmap bitmap =((GlideBitmapDrawable)imageView.getDrawable()).getBitmap();
        shareImageandText(bitmap);

    }
}
