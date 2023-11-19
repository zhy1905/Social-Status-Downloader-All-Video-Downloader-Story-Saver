package com.techhive.statussaver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoQuality implements Parcelable {

    private int audioBitrate;
    private String audioExt;
    private String audioUrl;
    private long bitrate = 0;
    private String dialogTitle;
    private String fileName;
    private int fps;
    private int height = -1;
    private boolean isYt;
    private String mime;
    private String quality;
    private String qualityLabel;
    private String sizeOfVideo;
    private String title;
    private String url;
    private String width;

    public VideoQuality() {
    }

    public static final Creator<VideoQuality> CREATOR = new Creator<VideoQuality>() {
        @Override
        public VideoQuality createFromParcel(Parcel in) {
            return new VideoQuality(in);
        }

        @Override
        public VideoQuality[] newArray(int size) {
            return new VideoQuality[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public int getAudioBitrate() {
        return this.audioBitrate;
    }

    public String getAudioExt() {
        return this.audioExt;
    }

    public String getAudioUrl() {
        return this.audioUrl;
    }

    public long getBitrate() {
        return this.bitrate;
    }

    public String getDialogTitle() {
        if (this.dialogTitle == null) {
            this.dialogTitle = "";
        }
        return this.dialogTitle;
    }

    public int getHeight() {
        return this.height;
    }

    public String getMime() {
        String str = this.mime;
        if (str != null) {
            if (str.contains("/")) {
                String str2 = this.mime;
                this.mime = str2.substring(str2.indexOf("/"));
            }
            if (this.mime.contains("/")) {
                this.mime = this.mime.replaceAll("/", "");
            }
        }
        return this.mime;
    }

    public String getQualityLabel() {
        return this.qualityLabel;
    }

    public String getSizeOfVideo() {
        return this.sizeOfVideo;
    }

    public String getUrl() {
        return this.url;
    }

    public String getVideoQuality() {
        return this.bitrate / 1000 >= 1500 ? "HD" : "SD";
    }

    public void setAudioBitrate(int i) {
        this.audioBitrate = i;
    }

    public void setAudioExt(String str) {
        this.audioExt = str;
    }

    public void setAudioUrl(String str) {
        this.audioUrl = str;
    }

    public void setBitrate(long j) {
        this.bitrate = j;
    }

    public void setDialogTitle(String str) {
        this.dialogTitle = str;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void setFps(int i) {
        this.fps = i;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void setMime(String str) {
        this.mime = str;
    }

    public void setQuality(String str) {
        this.quality = str;
    }

    public void setQualityLabel(String str) {
        this.qualityLabel = str;
    }

    public void setSizeOfVideo(String str) {
        this.sizeOfVideo = str;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public void setYt(boolean z) {
        this.isYt = z;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.url);
        parcel.writeString(this.mime);
        parcel.writeByte(this.isYt ? (byte) 1 : 0);
        parcel.writeInt(this.height);
        parcel.writeString(this.audioUrl);
        parcel.writeString(this.audioExt);
        parcel.writeString(this.dialogTitle);
        parcel.writeString(this.fileName);
        parcel.writeString(this.qualityLabel);
        parcel.writeLong(this.bitrate);
        parcel.writeString(this.title);
        parcel.writeString(this.quality);
        parcel.writeString(this.width);
        parcel.writeInt(this.fps);
        parcel.writeString(this.sizeOfVideo);
        parcel.writeInt(this.audioBitrate);
    }

    public VideoQuality(Parcel parcel) {
        this.url = parcel.readString();
        this.mime = parcel.readString();
        this.isYt = parcel.readByte() != 0;
        this.height = parcel.readInt();
        this.audioUrl = parcel.readString();
        this.audioExt = parcel.readString();
        this.dialogTitle = parcel.readString();
        this.fileName = parcel.readString();
        this.qualityLabel = parcel.readString();
        this.bitrate = parcel.readLong();
        this.title = parcel.readString();
        this.quality = parcel.readString();
        this.width = parcel.readString();
        this.fps = parcel.readInt();
        this.sizeOfVideo = parcel.readString();
        this.audioBitrate = parcel.readInt();
    }
}

