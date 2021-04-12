package it.lemmed.audiospectrum.audiocollection;

import android.net.Uri;

import it.lemmed.audiospectrum.utils.StringUtils;

public class RowRecord {
    //FIELDS
    private final String name;
    private final String extension;  //with point
    private final long size;     //which unit?...
    private int duration;       //ms
    private String absolutePath;
    private Uri uri;

    //CONSTRUCTORS
    public RowRecord(String name, String extension, long size, int duration, String absPath, Uri uri) {
        this.name = name;
        this.extension = extension;
        this.size = size;
        this.duration = duration;
        this.absolutePath = absPath;
        this.uri = uri;
    }

    //METHODS
    public String getName() {
        return this.name;
    }

    public String getExtension() {
        return this.extension;
    }

    public long getSize() {
        return this.size;
    }

    public int getDuration() { return this.duration; }

    public String getAbsolutePath() { return this.absolutePath; }

    public Uri getUri() { return this.uri; }

    public String getStringifiedDuration() { return StringUtils.returnStringifiedDuration(this.duration); }
}
