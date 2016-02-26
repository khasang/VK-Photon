package com.khasang.vkphoto.domain.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.presenter.VKPhotosPresenter;
import com.khasang.vkphoto.util.Logger;
import com.manuelpeinado.multichoiceadapter.MultiChoiceBaseAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VKPhotoAdapter extends MultiChoiceBaseAdapter {
    private Activity activity;
    private List<Photo> photoList;
    private VKPhotosPresenter vKPhotosPresenter;
    private List<Integer> selectedPositions = new ArrayList<>();

    public VKPhotoAdapter(Bundle savedInstanceState, Activity activity,VKPhotosPresenter vKPhotosPresenter, List<Photo> photoList) {
        super(savedInstanceState);
        this.activity = activity;
        this.vKPhotosPresenter = vKPhotosPresenter;
        this.photoList = photoList;

    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        activity.getMenuInflater().inflate(R.menu.menu_action_mode_vk_album, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync_photo:
                Logger.d(selectedPositions.toString());
                return true;
            case R.id.action_delete_photo:
                simpleDialog();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected View getViewImpl(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_simple_photo, null);
            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.image_view),
                    (ProgressBar) convertView.findViewById(R.id.progressBar));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        Picasso.with(activity).load(photoList.get(position).getUrlToMaxPhoto()).error(R.drawable.vk_share_send_button_background).into(viewHolder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return  photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;

        public ViewHolder(ImageView imageView, ProgressBar progressBar) {
            this.imageView = imageView;
            this.progressBar = progressBar;
        }
    }

    public void simpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Photo?").
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelectedPhoto();
                    }
                }).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void deleteSelectedPhoto() {
        for (Long items : this.getCheckedItems()) {
            selectedPositions.add(items.intValue());
        }
        vKPhotosPresenter.deletePhotoById(selectedPositions);
    }


}
