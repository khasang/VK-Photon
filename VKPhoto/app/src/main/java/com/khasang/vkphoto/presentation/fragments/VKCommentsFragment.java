package com.khasang.vkphoto.presentation.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.CommentRecyclerViewAdapter;
import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.VkProfile;
import com.khasang.vkphoto.presentation.presenter.VkCommentsPresenter;
import com.khasang.vkphoto.presentation.presenter.VkCommentsPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkCommentsView;
import com.khasang.vkphoto.util.Logger;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VKCommentsFragment extends Fragment implements VkCommentsView {

    private static final String PHOTO_ID = "photoId";
    public static final String TAG = VKCommentsFragment.class.getSimpleName();
    private int photoId;
    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;
    private VkCommentsPresenter presenter;
    private ImageView userImage;
    private TextView photolikes, commentCount;

    public static VKCommentsFragment newInstance(int photoId) {
        Bundle args = new Bundle();
        args.putInt(PHOTO_ID, photoId);
        VKCommentsFragment fragment = new VKCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new VkCommentsPresenterImpl(this);
        photoId = getArguments().getInt(PHOTO_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        presenter.getPhotoById(photoId);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        commentCount = (TextView) view.findViewById(R.id.commentsCount);
        photolikes = (TextView) view.findViewById(R.id.photoLikes);
        view.findViewById(R.id.commetnsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == RecyclerView.GONE) {
                    presenter.getCommentsByPhotoId(photoId);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onResume();
        presenter.onStop();
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
        Picasso.with(getContext()).load(photo.getUrlToMaxPhoto()).into(userImage);
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

