package com.example.photo_app.helper;

import com.example.photo_app.model.ImageModel;

import java.util.Date;
import java.util.List;

public class GallerySection {

    private Date date;
    private List<ImageModel> sectionItems;

    public GallerySection(Date date, List<ImageModel> sectionItems) {
        this.date = date;
        this.sectionItems = sectionItems;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ImageModel> getSectionItems() {
        return sectionItems;
    }

    public void setSectionItems(List<ImageModel> sectionItems) {
        this.sectionItems = sectionItems;
    }

    @Override
    public String toString() {
        return "GallerySection{" +
                "date='" + date + '\'' +
                ", sectionItems=" + sectionItems +
                '}';
    }
}
