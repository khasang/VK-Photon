package com.khasang.vkphoto.presentation.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.vk.VKCommentSource;
import com.khasang.vkphoto.domain.adapters.CommentRecyclerViewAdapter;
import com.khasang.vkphoto.domain.events.GetVKCommentsEvent;
import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.presentation.model.VkProfile;
import com.khasang.vkphoto.presentation.presenter.VkCommentsPresenter;
import com.khasang.vkphoto.presentation.presenter.VkCommentsPresenterImpl;
import com.khasang.vkphoto.presentation.view.VkCommentsView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VKCommentsFragment extends Fragment implements VkCommentsView{

    private static final String PHOTO_ID = "photoId";
    public static final String TAG = VKCommentsFragment.class.getSimpleName();
    private int photoId;
    private RecyclerView recyclerView;
    private CommentRecyclerViewAdapter adapter;
    private VkCommentsPresenter presenter;
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
        presenter.getCommentsByPhotoId(photoId);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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
    public void showError(String s) {

    }

    @Override
    public void confirmDelete(MultiSelector multiSelector) {

    }

}

