package com.techhive.statussaver.model;

import java.util.Map;

public class FacebookPost {
    String postUrl ;
    String thumbnailUrl;
    String videoSdUrl;
    String videoHdUrl;
    String videoMp3Url;
    String description;
    String dateTime;
    int likes = 0;
    int commentsCount = 0;
    int sharesCount = 0;
    int videoViewsCount = 0;

    public FacebookPost() {
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoSdUrl() {
        return videoSdUrl;
    }

    public void setVideoSdUrl(String videoSdUrl) {
        this.videoSdUrl = videoSdUrl;
    }

    public String getVideoHdUrl() {
        return videoHdUrl;
    }

    public void setVideoHdUrl(String videoHdUrl) {
        this.videoHdUrl = videoHdUrl;
    }

    public String getVideoMp3Url() {
        return videoMp3Url;
    }

    public void setVideoMp3Url(String videoMp3Url) {
        this.videoMp3Url = videoMp3Url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(int sharesCount) {
        this.sharesCount = sharesCount;
    }

    public int getVideoViewsCount() {
        return videoViewsCount;
    }

    public void setVideoViewsCount(int videoViewsCount) {
        this.videoViewsCount = videoViewsCount;
    }
}
