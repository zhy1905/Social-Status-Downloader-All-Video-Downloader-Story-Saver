package com.techhive.statussaver.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_table")
public class History {
    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    long id;

    @NonNull
    @ColumnInfo(name = "appName")
    String appName;

    @NonNull
    @ColumnInfo(name = "url")
    String url;

    public History(long id, @NonNull String appName, @NonNull String url) {
        this.id = id;
        this.appName = appName;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getAppName() {
        return appName;
    }

    public void setAppName(@NonNull String appName) {
        this.appName = appName;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }
}
