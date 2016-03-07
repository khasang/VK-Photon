package com.khasang.vkphoto.presentation.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.vk.VKCommentSource;
import com.khasang.vkphoto.domain.adapters.CommentRecyclerViewAdapter;
import com.khasang.vkphoto.presentation.model.Comment;
import com.khasang.vkphoto.presentation.view.VkCommentsView;

import java.util.List;

public class VKCommentsFragment extends Fragment implements VkCommentsView{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PHOTO_ID = "photoId";
    private int photoId;
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
        if (getArguments() != null) {
            photoId = getArguments().getInt(PHOTO_ID);
        }
        VKCommentSource vkCommentSource = new VKCommentSource();
        vkCommentSource.getCommentsByPhotoId(photoId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        initRecyclerView(view);

        return view;
    }

    private void initRecyclerView(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        if(adapter == null){
            adapter = new CommentRecyclerViewAdapter(comments);
        }

        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

