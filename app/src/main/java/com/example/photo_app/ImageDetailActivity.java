package com.example.photo_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.photo_app.adapter.ViewPagerAdapter;
import com.example.photo_app.helper.AndroidXI;
import com.example.photo_app.model.ImageModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.photo_app.MainActivity.imagePaths;

public class ImageDetailActivity extends AppCompatActivity {
    int IMAGE_REQUEST_CODE = 45;
    int RESULT_CODE = 200;
    public static final int DELETE_REQUEST_CODE = 13;

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
        mActionBar = getSupportActionBar();

        // showing the back button in action bar
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // on below line getting data which we have passed from our adapter class.
        position = getIntent().getIntExtra("position", 0);

        ArrayList<ImageModel> album = (ArrayList<ImageModel>) getIntent().getSerializableExtra("images");
        ;

        if (album != null)

            imagePaths = album;


        mViewPager = (ViewPager) findViewById(R.id.viewPagerImageDetail);
        mViewPagerAdapter = new ViewPagerAdapter(ImageDetailActivity.this, imagePaths);

        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(position);

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
            case R.id.action_info:
                loadInformationImage();
                break;
            case R.id.action_edit:
                editImage();
                break;
            case R.id.action_delete:
                showDialogDelete();
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

    private void shareImageToAnOntherApp() {
        imageView = (ImageView) findViewById(R.id.ivImageDetail);


        Bitmap bitmap = null;
        try {
            File f = new File(imagePaths.get(mViewPager.getCurrentItem()).getPath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        shareImageandText(bitmap);
        imageView.invalidate();
    }

    private void loadInformationImage() {
        Intent intent = new Intent(ImageDetailActivity.this, MapsActivity.class);

        intent.putExtra("path", imagePaths.get(mViewPager.getCurrentItem()).getPath());
        intent.putExtra("title", imagePaths.get(mViewPager.getCurrentItem()).getTitle());
        intent.putExtra("date", imagePaths.get(mViewPager.getCurrentItem()).getDate());
        intent.putExtra("latitude", imagePaths.get(mViewPager.getCurrentItem()).getGpsLat());
        intent.putExtra("longitude", imagePaths.get(mViewPager.getCurrentItem()).getGpsLong());
        intent.putExtra("size", imagePaths.get(mViewPager.getCurrentItem()).getSize());
        startActivity(intent);
    }


    private void showDialogDelete() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteImage();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageDetailActivity.this);
        builder.setMessage("Do you really want to delete this photo?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void deleteImage() {
        androidx.appcompat.app.AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
        //builder1.setMessage("Do you want to delete image ?");
        builder.setCancelable(true);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();


        String path = imagePaths.get(mViewPager.getCurrentItem()).getPath();
        Uri uri = getContentUriId(Uri.fromFile(new File(path)));
        try {
            deleteAPI28(uri, this);
            Toast.makeText(this, "Image Deleted successfully", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();

        } catch (Exception e) {
            //  PendingIntent createDeleteRequest()
            try {
                deleteAPI30(uri);
                        //notifyItemRemoved(position);
                Toast.makeText(this, "Image Deleted successfully", Toast.LENGTH_SHORT).show();

            } catch (IntentSender.SendIntentException e1) {
                e1.printStackTrace();
            }
            alertDialog.dismiss();
        }
    }

    public static int deleteAPI28(Uri uri, Context context) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }
    private final ActivityResultLauncher<IntentSenderRequest> launcher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.R)

    private void deleteAPI30(Uri imageUri) throws IntentSender.SendIntentException {
        if (imageUri == null) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            AndroidXI.getInstance().with(this).delete(launcher, imageUri);
        }

    }



    private void editImage() {
        Intent intent = new Intent();


        Toast.makeText(this, "sss", Toast.LENGTH_SHORT).show();
        String imgPath = imagePaths.get(mViewPager.getCurrentItem()).getPath();
        Uri filePath = Uri.fromFile(new File(imgPath));
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(filePath);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Pico");
        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
        ((Activity)this).startActivityForResult(dsPhotoEditorIntent, RESULT_CODE);
    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    private Uri getContentUriId(Uri imageUri) {
        String[] projections = {MediaStore.MediaColumns._ID};
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections,
                MediaStore.MediaColumns.DATA + "=?",
                new String[]{imageUri.getPath()}, null);
        long id = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
        }
        cursor.close();
        return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf((int) id));
    }
}
