package com.techhive.statussaver.model.response;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class ModelResponse implements Serializable {

    @SerializedName("data")
    private ModelGraphshortcode modelGraphshortcode;

    public ModelGraphshortcode getModelGraphshortcode() {
        return modelGraphshortcode;
    }

    public void setModelGraphshortcode(ModelGraphshortcode modelGraphshortcode) {
        this.modelGraphshortcode = modelGraphshortcode;
    }
}
