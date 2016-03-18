package com.khasang.vkphoto.presentation.view;

import android.content.Context;

import com.bignerdranch.android.multiselector.MultiSelector;

public interface View {
    void showError(int errorCode);

    Context getContext();

    void confirmDelete(MultiSelector multiSelector);
}
