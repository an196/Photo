package com.example.photo_app.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageModel  implements Serializable {
    private String title;
    private String path;
    private String size;
    private String date;
    private double gpsLat;
    private double gpsLong;

    String str_folder;
    ArrayList<ImageModel> al_imagepath;

    private ArrayList<ImageModel> ImageList = null;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(double gpsLat) {
        this.gpsLat = gpsLat;
    }

    public double getGpsLong() {
        return gpsLong;
    }

    public void setGpsLong(double gpsLong) {
        this.gpsLong = gpsLong;
    }

    public ArrayList<ImageModel> getImageList() {
        return ImageList;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setImageList(ArrayList<ImageModel> imageList) {
        ImageList = imageList;
    }
    public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<ImageModel> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<ImageModel> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }

    public ArrayList<ImageModel> getAllImage(Context context) {
        // in this method we are adding all our image paths
        // in our arraylist which we have created.
        // on below line we are checking if the device is having an sd card or not.
        ImageList = new ArrayList<>();


        // if the sd card is present we are creating a new list in
        // which we are getting our images data with their ids.
        final String[] columns = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE};

        // on below line we are creating a new
        // string to order our images by string.
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        // this method will stores all the images
        // from the gallery in Cursor
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

        // below line is to get total number of images
        int count = cursor.getCount();

        // on below line we are running a loop to add
        // the image file path in our array list.

        if ( cursor != null && cursor.moveToFirst()) {
            do {
                // on below line we are getting image file path
                String dataColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String titleColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow( MediaStore.Images.Media.TITLE));
                String dateColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                double latColumnIndex = cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE));
                double longColumnIndex = cursor.getDouble(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE));
                String sizeColumnIndex = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                // after that we are getting the image file path
                // and adding that path in our array list.

                ImageModel img = new ImageModel();
                img.setTitle(titleColumnIndex);
                img.setPath(dataColumnIndex);
                img.setDate(dateColumnIndex);
                img.setGpsLat(latColumnIndex);
                img.setGpsLong(longColumnIndex);
                img.setSize(sizeColumnIndex);
                ImageList.add(img);

            } while (cursor.moveToNext());


            // after adding the data to our
            // array list we are closing our cursor.
            cursor.close();
        }
        return ImageList;
    }
}
