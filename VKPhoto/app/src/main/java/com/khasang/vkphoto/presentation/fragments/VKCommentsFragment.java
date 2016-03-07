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
import com.khasang.vkphoto.presentation.view.VkCommentsView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class VKCommentsFragment extends Fragment implements VkCommentsView{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PHOTO_ID = "photoId";
    public static final String TAG = "comments";
    private int photoId;
    private RecyclerView recyclerView;
    private List<Comment> coments;
    private CommentRecyclerViewAdapter adapter;

    // TODO: Rename and change types of parameters
    private List<Comment> comments ;

    private OnFragmentInteractionListener mListener;

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
        EventBus.getDefault().register(this);
        photoId = getArguments().getInt(PHOTO_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        VKCommentSource vkCommentSource = new VKCommentSource();
        vkCommentSource.getCommentsByPhotoId(photoId);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
    @Subscribe
    public void onGetVKCommentsEvent(GetVKCommentsEvent event){
        adapter = new CommentRecyclerViewAdapter(event.commentsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void displayVkComments(List<Comment> comments) {
    }

    @Override
    public void showError(String s) {

    }

    @Override
    public void confirmDelete(MultiSelector multiSelector) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

