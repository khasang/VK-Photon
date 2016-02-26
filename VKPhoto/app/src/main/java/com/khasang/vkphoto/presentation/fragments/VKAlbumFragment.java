package com.khasang.vkphoto.presentation.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.VKPhotoAdapter;
import com.khasang.vkphoto.domain.events.GetVKPhotosEvent;
import com.khasang.vkphoto.domain.events.SyncAndTokenReadyEvent;
import com.khasang.vkphoto.domain.interfaces.SyncServiceProvider;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.presentation.presenter.VKAlbumPresenterImpl;
import com.khasang.vkphoto.presentation.presenter.VKPhotosPresenter;
import com.khasang.vkphoto.presentation.view.VkAlbumView;
import com.khasang.vkphoto.util.Logger;
import com.khasang.vkphoto.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class VKAlbumFragment extends Fragment implements VkAlbumView {
    public static final String TAG = VKAlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    private PhotoAlbum photoAlbum;
    private VKPhotosPresenter vKPhotosPresenter;
    private GridView gridview;
    private List<Photo> photoList = new ArrayList<>();
    private List<Integer> selectedPositions = new ArrayList<>();
    int albumId;
    private EventBus eventBus;
    private VKPhotoAdapter adapter;

    public static VKAlbumFragment newInstance(PhotoAlbum photoAlbum) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTOALBUM, photoAlbum);
        VKAlbumFragment fragment = new VKAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        vKPhotosPresenter = new VKAlbumPresenterImpl(this, ((SyncServiceProvider) getActivity()));
        adapter = new VKPhotoAdapter(savedInstanceState,getActivity(), vKPhotosPresenter, photoList);
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_albums, null);
        photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (photoAlbum != null) {
            Logger.d("photoalbum " + photoAlbum.title);
        } else {
            Logger.d("wtf where is album?");
        }
        albumId = photoAlbum.id;
        gridview = (GridView) view.findViewById(R.id.gridView);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// Go to PhotoFragment
            }
        });
        gridview.setAdapter(adapter);
//        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
//        gridview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//                if (checked) {
//                    selectedPositions.add(position);
//                }
//                if (!checked){
//                    selectedPositions.remove(((Object) position));
//                }
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                VKAlbumFragment.this.getActivity().getMenuInflater().inflate(R.menu.menu_action_mode_vk_album, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_sync_photo:
//                        Logger.d(selectedPositions.toString());
//                        return true;
//                    case R.id.action_delete_photo:
//                        simpleDialog();
//                        return true;
//                    default:
//                        break;
//                }
//                return false;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//                selectedPositions.clear();
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("onResume VKAlbumFragment");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("VkAlbumFragment onStart");
        vKPhotosPresenter.onStart();
        vKPhotosPresenter.getPhotosByAlbumId(albumId);
    }

    @Override
    public void onStop() {
        super.onStop();
        vKPhotosPresenter.onStop();
    }

    @Override
    public void displayVkPhotosByAlbumId() {
    }

    @Override
    public List<Photo> getPhotoList() {
        return photoList;
    }

    @Override
    public void showError(String s) {
        ToastUtils.showError(s, getContext());
    }

    private void deletePhotoById() {
        vKPhotosPresenter.deletePhotoById(selectedPositions);
        vKPhotosPresenter.getPhotosByAlbumId(albumId);
        EventBus.getDefault().postSticky(new SyncAndTokenReadyEvent());
    }

    @Subscribe
    public void onGetVKPhotosEvent(GetVKPhotosEvent getVKPhotosEvent) {
        photoList = getVKPhotosEvent.photosList;
        adapter.setPhotoList(photoList);
    }

    public void simpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Photo?").
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePhotoById();
                    }
                }).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}
