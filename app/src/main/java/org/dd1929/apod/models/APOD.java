package org.dd1929.apod.models;

/* APOD - A simple app to view images from NASA's APOD service
    Copyright (C) 2021  Deepto Debnath

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see https://www.gnu.org/licenses/ */

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.JsonAdapter;

import org.dd1929.apod.utils.APODDeserializer;

@JsonAdapter(APODDeserializer.class)
@Entity(tableName = "apod_table")
public class APOD {

    private String mTitle;
    private String mUrl;
    private String mHdUrl;

    @PrimaryKey
    private Long mDate;
    private String mDetails;
    private String mMediaType;
    private String mCopyright;
    private String mThumbUrl;
    private Boolean mIsFavourite;
    private Boolean mIsRandom;

    public APOD(String title, String url, String hdUrl, Long date, String details, String mediaType, String copyright, String thumbUrl, Boolean isFavourite, Boolean isRandom) {
        mTitle = title;
        mUrl = url;
        mHdUrl = hdUrl;
        mDate = date;
        mDetails = details;
        mMediaType = mediaType;
        mCopyright = copyright;
        mThumbUrl = thumbUrl;
        mIsFavourite = isFavourite;
        mIsRandom = isRandom;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getHdUrl() {
        return mHdUrl;
    }

    public void setHdUrl(String hdUrl) {
        mHdUrl = hdUrl;
    }

    public Long getDate() {
        return mDate;
    }

    public void setDate(Long date) {
        mDate = date;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public String getMediaType() {
        return mMediaType;
    }

    public void setMediaType(String mediaType) {
        mMediaType = mediaType;
    }

    public String getCopyright() {
        return mCopyright;
    }

    public void setCopyright(String copyright) {
        mCopyright = copyright;
    }

    public String getThumbUrl() {
        return mThumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        mThumbUrl = thumbUrl;
    }

    public Boolean getIsFavourite() {
        return mIsFavourite;
    }

    public void setIsFavourite(Boolean favourite) {
        mIsFavourite = favourite;
    }

    public Boolean getIsRandom() {
        return mIsRandom;
    }

    public void setIsRandom(Boolean random) {
        mIsRandom = random;
    }
}
