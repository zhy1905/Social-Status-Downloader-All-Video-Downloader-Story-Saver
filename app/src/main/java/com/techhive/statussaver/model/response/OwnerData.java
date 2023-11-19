package com.techhive.statussaver.model.response;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class OwnerData implements Serializable {


    @SerializedName("username")
    private String username;
    @SerializedName("profile_pic_url")
    private String profile_pic_url;

    public OwnerData() {
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
