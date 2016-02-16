package com.khasang.vkphoto.domain.entities;

import com.google.gson.annotations.SerializedName;

public class Response<T> {
    @SerializedName("response")
    public T response;
}
