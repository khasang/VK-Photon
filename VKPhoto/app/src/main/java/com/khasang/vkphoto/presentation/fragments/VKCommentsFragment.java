package com.khasang.vkphoto.presentation.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bumptech.glide.Glide;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.CommentRecyclerViewAdapter;
import com.khasang.vkphoto.domain.interfaces.FabProvider;
import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.VkProfile;
import com.khasang.vkphoto.presentation.presenter.VkCommentsPresenter;
import com.khasang.vkphoto.presentation.presenter.VkCommentsPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkCommentsView;
import com.khasang.vkphoto.util.Logger;

import java.util.List;

public class VKCommentsFragment extends Fragment implements VkCommentsView {

    private static final String PHOTO_ID = "photo";
    public static final String TAG = VKCommentsFragment.class.getSimpleName();
    private Photo photo;
    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;
    private VkCommentsPresenter presenter;
    private ImageView userImage;
    private TextView photolikes, commentCount;
    private LinearLayout hlayout;

    public static VKCommentsFragment newInstance(Photo photo) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTO_ID, photo);
        VKCommentsFragment fragment = new VKCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new VkCommentsPresenterImpl(this);
        if (getArguments() != null) {
            photo = getArguments().getParcelable(PHOTO_ID);
        }
        ((FabProvider) getContext()).getFloatingActionButton().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        commentCount = (TextView) view.findViewById(R.id.commentsCount);
        photolikes = (TextView) view.findViewById(R.id.photoLikes);
        hlayout = ((LinearLayout) view.findViewById(R.id.hLayout));
        view.findViewById(R.id.commetnsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == RecyclerView.GONE) {
                    presenter.getCommentsByPhotoId(photo.id);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        loadPhoto();
        ((FabProvider) getContext()).getFloatingActionButton().hide();
        return view;
    }

    private void loadPhoto() {
        if (!TextUtils.isEmpty(photo.filePath)) {
            Logger.d(VKCommentsFragment.class.getSimpleName()+": image load form local album");
            Glide.with(userImage.getContext())
                    .load("file://" + photo.filePath)
                    .error(R.drawable.vk_share_send_button_background)
                    .into(userImage);
        } else {
            Logger.d(VKCommentsFragment.class.getSimpleName() + ": image load form server");
            Glide.with(userImage.getContext())
                    .load(photo.getUrlToMaxPhoto())
                    .error(R.drawable.vk_share_send_button_background)
                    .into(userImage);
            hlayout.setVisibility(View.VISIBLE);
            photolikes.setText(String.valueOf(photo.likes));
            commentCount.setText(String.valueOf(photo.comments));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG + " onStop");
        presenter.onStop();
        hlayout.setVisibility(View.GONE);

    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG + " onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
        Logger.d(TAG + " onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG + " onDestroy");
    }

    @Override
    public void displayVkComments(List<Comment> comments, List<VkProfile> profiles) {
        adapter = new CommentRecyclerViewAdapter(comments, profiles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void displayVkPhoto(Photo photo) {
        Logger.d("load image " + photo.getUrlToMaxPhoto() + " into imageView");
        Glide.with(getContext()).load(photo.getUrlToMaxPhoto()).into(userImage);
        photolikes.setText(String.valueOf(photo.likes));
        commentCount.setText(String.valueOf(photo.comments));
    }


    @Override
    public void showError(int errorCode) {

    }

    @Override
    public void confirmDelete(MultiSelector multiSelector) {

    }

}

