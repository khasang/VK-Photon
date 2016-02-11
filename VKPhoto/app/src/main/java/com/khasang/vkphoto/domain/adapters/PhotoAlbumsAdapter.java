package com.khasang.vkphoto.domain.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.util.JsonUtils;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoAlbum;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class PhotoAlbumsAdapter extends RecyclerView.Adapter<PhotoAlbumsAdapter.PhotoAlbumHolder> {
    private List<VKApiPhotoAlbum> photoAlbumList;
    private List<VKApiPhotoAlbum> albumsToSync;

    public PhotoAlbumsAdapter(List<VKApiPhotoAlbum> photoAlbumList, List<VKApiPhotoAlbum> albumsToSync) {
        this.photoAlbumList = photoAlbumList;
        this.albumsToSync = albumsToSync;
    }

    @Override
    public PhotoAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoalbum_item, parent, false);
        return new PhotoAlbumHolder(view, albumsToSync);
    }

    @Override
    public void onBindViewHolder(PhotoAlbumHolder holder, int position) {
        VKApiPhotoAlbum photoAlbum = photoAlbumList.get(position);
        holder.bindPhotoAlbum(photoAlbum);
    }

    @Override
    public int getItemCount() {
        return photoAlbumList.size();
    }

    static class PhotoAlbumHolder extends RecyclerView.ViewHolder {
        final ImageView albumThumbImageView;
        final TextView albumTitleTextView;
        VKApiPhotoAlbum photoAlbum;
        List<VKApiPhotoAlbum> albumsToSync;

        public PhotoAlbumHolder(View itemView, List<VKApiPhotoAlbum> albumsToSync) {
            super(itemView);
            this.albumsToSync = albumsToSync;
            albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
            albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
        }

        public void bindPhotoAlbum(final VKApiPhotoAlbum photoAlbum) {
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
