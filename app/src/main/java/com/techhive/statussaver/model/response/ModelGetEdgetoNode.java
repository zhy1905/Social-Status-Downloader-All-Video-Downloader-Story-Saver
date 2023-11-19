package com.techhive.statussaver.model.response;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Keep
public class ModelGetEdgetoNode implements Serializable {

    @SerializedName("edges")
    private List<ModelEdNode> modelEdNodes;

    public List<ModelEdNode> getModelEdNodes() {
        return modelEdNodes;
    }

    public void setModelEdNodes(List<ModelEdNode> modelEdNodes) {
        this.modelEdNodes = modelEdNodes;
    }
}
