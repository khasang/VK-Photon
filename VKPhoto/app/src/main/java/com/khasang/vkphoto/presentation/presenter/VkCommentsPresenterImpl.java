package com.khasang.vkphoto.presentation.presenter;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.khasang.vkphoto.data.vk.VKCommentSource;
import com.khasang.vkphoto.domain.adapters.CommentRecyclerViewAdapter;
import com.khasang.vkphoto.domain.events.GetVKCommentsEvent;
import com.khasang.vkphoto.domain.events.GetVKPhotoEvent;
import com.khasang.vkphoto.domain.interactors.VkCommentsInteractor;
import com.khasang.vkphoto.domain.interactors.VkCommentsInteractorImpl;
import com.khasang.vkphoto.presentation.view.VkCommentsView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by admin on 09.03.2016.
 */
public class VkCommentsPresenterImpl implements VkCommentsPresenter {

    private VkCommentsInteractor interactor;
    private VkCommentsView commentsView;

    public VkCommentsPresenterImpl(VkCommentsView commentsView) {
        EventBus.getDefault().register(this);
        interactor = new VkCommentsInteractorImpl();
        this.commentsView = commentsView;
    }

    @Subscribe
    public void onGetVKCommentsEvent(GetVKCommentsEvent event){
        commentsView.displayVkComments(event.commentsList, event.profiles);
    }

    @Subscribe
    public void onGetVKPhotoEvent(GetVKPhotoEvent event){
        commentsView.displayVkPhoto(event.photo);
    }

    @Override
    public void getCommentsByPhotoId(int photoId) {
        interactor.getCommentsByPhotoId(photoId);
    }

    @Override
    public void getPhotoById(int photoId) {
        interactor.getPhotoById(photoId);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
