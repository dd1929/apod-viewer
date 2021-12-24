package org.dd1929.apod.models;

import org.dd1929.apod.enums.License;

import java.io.Serializable;

public class Library implements Serializable {

    private String mName;
    private String mAuthor;
    private String mDescription;
    private String mCopyright;
    private License mLicense;
    private String mLicenseUrl;
    private String mSourceUrl;
    private String mWebsite;

    public Library(String name, String author, String description, String copyright, License license, String source, String sourceUrl, String website) {
        mName = name;
        mAuthor = author;
        mDescription = description;
        mCopyright = copyright;
        mLicense = license;
        mLicenseUrl = source;
        mSourceUrl = sourceUrl;
        mWebsite = website;
    }

    public String getName() {
        return mName;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getCopyright() {
        return mCopyright;
    }

    public License getLicense() {
        return mLicense;
    }

    public String getLicenseUrl() {
        return mLicenseUrl;
    }

    public String getSourceUrl(){
        return mSourceUrl;
    }

    public String getWebsite() {
        return mWebsite;
    }
}
