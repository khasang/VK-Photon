package com.khasang.vkphoto.presentation.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Items<T> {
    @SerializedName("items")
    public List<T> results;
    @SerializedName("count")
    public int count;
}
