package com.example.untactrip3.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Location_code {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("data")
    @Expose
    private List<Datum_code> data = null;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public List<Datum_code> getData() {
        return data;
    }
    public void setData(List<Datum_code> data) {
        this.data = data;
    }
}
