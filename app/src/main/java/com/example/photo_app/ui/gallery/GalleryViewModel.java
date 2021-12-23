package com.example.photo_app.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.photo_app.model.ImageModel;

import java.util.ArrayList;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<String>> imageList;

    public GalleryViewModel() {

        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");

        ImageModel imageModel = new ImageModel();

        imageList = new MutableLiveData<>();



    }

    public LiveData<String> getText() {
        return mText;
    }
}