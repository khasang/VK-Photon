package com.khasang.vkphoto.presentation.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

public class LocalAlbumsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vk_albums, container, false);

        LocalAlbumSource localAlbumSource = new LocalAlbumSource(getContext());
        List<PhotoAlbum> allAlbumsList = localAlbumSource.getAllLocalAlbums();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(allAlbumsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        final RecyclerView localAlbumsGallery = (RecyclerView) rootView.findViewById(R.id.albums_recycler_view);
        localAlbumsGallery.setAdapter(adapter);
        localAlbumsGallery.setLayoutManager(linearLayoutManager);

        return rootView;
    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
        private List<PhotoAlbum> allAlbumsList;

        public RecyclerViewAdapter(List<PhotoAlbum> allAlbumsList) {
            this.allAlbumsList = allAlbumsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.photoalbum_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PhotoAlbum photoAlbum = allAlbumsList.get(position);
            LocalPhotoSource localPhotoSource = new LocalPhotoSource(getContext());
            List <Photo> allPhotosInAlbum = localPhotoSource.getPhotosByAlbum(photoAlbum);
            Photo lastInAlbum = allPhotosInAlbum.get(allPhotosInAlbum.size() - 1);
            Bitmap bitmapImage = BitmapFactory.decodeFile(lastInAlbum.filePath);

            holder.mAlbumThumb.setImageBitmap(bitmapImage);
            holder.mAlbumTitle.setText(photoAlbum.title);
            holder.mPhotosCount.setText(getContext().getString(R.string.count_of_photos_in_album, photoAlbum.size));
        }

        @Override
        public int getItemCount() {
            return allAlbumsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private ImageView mAlbumThumb;
            private TextView mAlbumTitle;
            private TextView mPhotosCount;

            public MyViewHolder(View itemView) {
                super(itemView);
                mAlbumThumb  = (ImageView) itemView.findViewById(R.id.album_thumb);
                mAlbumTitle  = (TextView)  itemView.findViewById(R.id.album_title);
                mPhotosCount = (TextView)  itemView.findViewById(R.id.tv_count_of_albums);
            }
        }
    }
}
