package com.example.photo_app;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.photo_app.databinding.ActivityMapsBinding;
import com.example.photo_app.helper.BubbleTransformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private String _path;
    private String _size;
    private String _date;
    private String _title;
    private double _latitude = 10.762622; //
    private double _longitude = 106.660172; //106.660172
    private String exifAttribute;
    TextView textView1, textView2, textView3;
    private boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        _path = getIntent().getStringExtra("path");
        _title = getIntent().getStringExtra("title");
        _date = getIntent().getStringExtra("date");
        _size = getIntent().getStringExtra("size");

        getIntent().getDoubleExtra("latitude", _latitude);
        getIntent().getDoubleExtra("longitude", _longitude);


        textView1 = (TextView) findViewById(R.id.tv_img_title);


        exifAttribute = null;
        File f = new File(_path);
        if (f.exists()) {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(_path);
                exifAttribute = getExif(exif);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        textView1.setText(exifAttribute);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        if (valid) {
            //Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(_latitude, _longitude);

            Target mTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Marker driver_marker = mMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("test")
                            .snippet("test address")
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_latitude, _longitude), 15.0f));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }


                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            File imgFile = new File("//" + _path);

            Picasso.get()
                    .load(imgFile)
                    .resize(250, 250)
                    .centerCrop()
                    .transform(new BubbleTransformation(20))
                    .into(mTarget);

//        }
    }


    private void load() throws IOException {
        File imgFile = new File(_path);
        ExifInterface exif = new ExifInterface(_path);
    }

    private String getExif(ExifInterface exif) {
        String attrLATITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String attrLATITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String attrLONGITUDE = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String attrLONGITUDE_REF = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

        if ((attrLATITUDE != null)
                && (attrLATITUDE_REF != null)
                && (attrLONGITUDE != null)
                && (attrLONGITUDE_REF != null)) {
            valid = true;

            if (attrLATITUDE_REF.equals("N")) {
                _latitude = convertToDegree(attrLATITUDE);
            } else {
                _latitude = 0 - convertToDegree(attrLATITUDE);
            }

            if (attrLONGITUDE_REF.equals("E")) {
                _longitude = convertToDegree(attrLONGITUDE);
            } else {
                _longitude = 0 - convertToDegree(attrLONGITUDE);
            }
        }

        String myAttribute = "";
        myAttribute += "Storage : " + _path + "\n";
        myAttribute += "Time : " + exif.getAttribute(ExifInterface.TAG_DATETIME) + "\n";
        myAttribute += "Decription : " + exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION) + "\n";
        myAttribute += getTagString(ExifInterface.TAG_FLASH, exif);
        myAttribute += ExifInterface.TAG_GPS_DEST_LATITUDE + " : " + _latitude + "\n";
        myAttribute +=  ExifInterface.TAG_GPS_DEST_LATITUDE + " : "+ _longitude + "\n";
        myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
        myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);
        myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);


        return myAttribute;
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    };
}