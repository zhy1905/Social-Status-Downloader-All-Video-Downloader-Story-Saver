package com.techhive.statussaver.model;

public class FVideo {
    public static final int DOWNLOADING = 1;
    public static final int FACEBOOK = 1;
    private int State;
    private long downloadId;
    private long downloadTime;
    private String fileName;
    private String fileUri;
    private boolean isWatermarked;
    private String outputPath;
    private int videoSource;

    public FVideo(String outputPath2, String fileName2, long downloadId2, boolean isWatermarked2, long downloadTime2) {
        this.outputPath = outputPath2;
        this.fileName = fileName2;
        this.downloadId = downloadId2;
        this.isWatermarked = isWatermarked2;
        this.downloadTime = downloadTime2;
    }

    public FVideo(long downloadTime2) {
        this.downloadTime = downloadTime2;
    }

    public void setIsWatermarked(Boolean watermarked) {
        this.isWatermarked = watermarked.booleanValue();
    }

    public boolean isWatermarked() {
        return this.isWatermarked;
    }

    public String getOutputPath() {
        return this.outputPath;
    }

    public void setOutputPath(String outputPath2) {
        this.outputPath = outputPath2;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName2) {
        this.fileName = fileName2;
    }

    public long getDownloadId() {
        return this.downloadId;
    }

    public void setDownloadId(long downloadId2) {
        this.downloadId = downloadId2;
    }

    public String getFileUri() {
        return this.fileUri;
    }

    public void setFileUri(String fileUri2) {
        this.fileUri = fileUri2;
    }

    public int getState() {
        return this.State;
    }

    public void setState(int state) {
        this.State = state;
    }

    public int getVideoSource() {
        return this.videoSource;
    }

    public void setVideoSource(int videoSource2) {
        this.videoSource = videoSource2;
    }

    public long getDownloadTime() {
        return this.downloadTime;
    }

    public void setDownloadTime(long downloadTime2) {
        this.downloadTime = downloadTime2;
    }
}
