package com.khasang.vkphoto.presentation.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VkAddAlbumPresenter;
import com.khasang.vkphoto.presentation.presenter.VkAddAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkAddAlbumView;
import com.khasang.vkphoto.util.ToastUtils;
import com.vk.sdk.api.model.VKPrivacy;

/** Created by bugtsa on 18-Feb-16. */
public class VkAddAlbumFragment extends DialogFragment implements VkAddAlbumView {
    public static final String TAG = VkAddAlbumFragment.class.getSimpleName();
    private VkAddAlbumPresenter vkAddAlbumPresenter;
    private Context context;
    private String st;

    public VkAddAlbumFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        context = activity;
        vkAddAlbumPresenter = new VkAddAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Add Album");
        View view = inflater.inflate(R.layout.fragment_vk_add_album, null);
        final EditText etTitle = (EditText) view.findViewById(R.id.et_title_add_album);
        final EditText etDescription = (EditText) view.findViewById(R.id.et_description_add_album);
        view.findViewById(R.id.btn_ok_add_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            vkAddAlbumPresenter.addAlbum(etTitle.getText().toString(), etDescription.getText().toString(),
                    VKPrivacy.PRIVACY_NOBODY, VKPrivacy.PRIVACY_NOBODY);
            }
        });
        view.findViewById(R.id.btn_cancel_add_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        vkAddAlbumPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        vkAddAlbumPresenter.onStop();
    }

    @Override
    public void displayVkAddAlbum(PhotoAlbum photoAlbum) {
        getDialog().dismiss();
        vkAddAlbumPresenter.goToPhotoAlbum(getContext(), photoAlbum);
    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }
}
