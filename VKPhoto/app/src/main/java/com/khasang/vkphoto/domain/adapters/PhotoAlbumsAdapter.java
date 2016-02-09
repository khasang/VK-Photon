package com.khasang.vkphoto.domain.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.model.Items;
import com.khasang.vkphoto.model.Response;
import com.khasang.vkphoto.model.album.PhotoAlbum;
import com.khasang.vkphoto.model.photo.Photo;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.lang.reflect.Type;
import java.util.List;

public class PhotoAlbumsAdapter extends RecyclerView.Adapter<PhotoAlbumsAdapter.PhotoAlbumHolder> {
    private List<PhotoAlbum> photoAlbumList;

    public PhotoAlbumsAdapter(List<PhotoAlbum> photoAlbumList) {
        this.photoAlbumList = photoAlbumList;
    }

    @Override
    public PhotoAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoalbum_item, parent, false);
        return new PhotoAlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoAlbumHolder holder, int position) {
        PhotoAlbum photoAlbum = photoAlbumList.get(position);
        holder.bindPhotoAlbum(photoAlbum);
    }

    @Override
    public int getItemCount() {
        return photoAlbumList.size();
    }

    static class PhotoAlbumHolder extends RecyclerView.ViewHolder {
        private final ImageView albumThumbImageView;
        private final TextView albumTitleTextView;
        private PhotoAlbum photoAlbum;

        public PhotoAlbumHolder(View itemView) {
            super(itemView);
            albumThumbImageView = (ImageView) itemView.findViewById(R.id.album_thumb);
            albumTitleTextView = (TextView) itemView.findViewById(R.id.album_title);
        }

        public void bindPhotoAlbum(PhotoAlbum photoAlbum) {
            this.photoAlbum = photoAlbum;
            albumTitleTextView.setText(photoAlbum.title);
            if (photoAlbum.thumb_id != 0) {
                VKRequest vkRequest = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, photoAlbum.id, VKApiConst.PHOTO_IDS, photoAlbum.thumb_id));
                vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Type photoType = new TypeToken<Response<Items<Photo>>>() {
                        }.getType();
                        Response<Items<Photo>> photoResponse = new Gson().fromJson(response.json.toString(), photoType);
                        Photo photo = photoResponse.response.results.get(0);
                        Picasso.with(albumThumbImageView.getContext())
                                .load(photo.photo_130)
                                .into(albumThumbImageView);

                    }
                });
            }
        }
    }
}
