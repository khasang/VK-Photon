package com.khasang.vkphoto.domain.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.util.JsonUtils;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;

import org.json.JSONArray;
import org.json.JSONException;

public class PhotoAlbumCursorAdapter extends CursorRecyclerViewAdapter<PhotoAlbumCursorAdapter.ViewHolder> {

    public PhotoAlbumCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bindPhotoAlbum(new PhotoAlbum(cursor));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoalbum_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView albumThumbImageView;
        final TextView albumTitleTextView;
        PhotoAlbum photoAlbum;

        public ViewHolder(View itemView) {
            super(itemView);
            albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
            albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
        }

        public void bindPhotoAlbum(final PhotoAlbum photoAlbum) {
            this.photoAlbum = photoAlbum;
            albumTitleTextView.setText(photoAlbum.title);
            if (photoAlbum.thumb_id != 0) {
                VKRequest vkRequest = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, photoAlbum.id, VKApiConst.PHOTO_IDS, photoAlbum.thumb_id));
                vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            JSONArray jsonArray = JsonUtils.getJsonArray(response.json);
                            VKApiPhoto vkApiPhoto = new VKApiPhoto(jsonArray.getJSONObject(0));
                            Picasso.with(albumThumbImageView.getContext())
                                    .load(vkApiPhoto.photo_130)
                                    .into(albumThumbImageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
