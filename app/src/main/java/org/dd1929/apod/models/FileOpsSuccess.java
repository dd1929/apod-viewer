package org.dd1929.apod.models;

import java.io.File;

public class FileOpsSuccess {

    private boolean mIsSuccessful;
    private File mFile;

    public FileOpsSuccess(boolean isSuccessful, File file) {
        mIsSuccessful = isSuccessful;
        mFile = file;
    }

    public boolean isSuccessful() {
        return mIsSuccessful;
    }

    public File getFile() {
        return mFile;
    }
}
