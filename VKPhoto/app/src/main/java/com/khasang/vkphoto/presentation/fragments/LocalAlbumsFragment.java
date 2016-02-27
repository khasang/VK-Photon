package com.khasang.vkphoto.presentation.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalAlbumSource;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.activities.Navigator;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import java.util.List;

public class LocalAlbumsFragment extends Fragment {
    private Context context;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        View rootView = inflater.inflate(R.layout.fragment_local_albums, container, false);
        final GridLayout localAlbumsGallery = (GridLayout) rootView.findViewById(R.id.localAlbumsGridLA);
        
        LocalAlbumSource localAlbumSource = new LocalAlbumSource(context);
        List <PhotoAlbum> allAlbumsList = localAlbumSource.getAllAlbums();
        for (PhotoAlbum album: allAlbumsList) {
            addAlbumViewToLayout(album, localAlbumsGallery);
        }
        return rootView;
    }

    private void addAlbumViewToLayout(PhotoAlbum album, GridLayout gridLayout){
        LocalPhotoSource localPhotoSource = new LocalPhotoSource(context);
        List <Photo> allPhotosInAlbum = localPhotoSource.getPhotosByAlbumId(album.id);
        Photo lastInAlbum = allPhotosInAlbum.get(allPhotosInAlbum.size() - 1);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int imgSize = (metrics.widthPixels - 40) / 2;

        LinearLayout albumViewBlock = new LinearLayout(context);
        albumViewBlock.setLayoutParams(new LinearLayout.LayoutParams(imgSize, ViewGroup.LayoutParams.WRAP_CONTENT));
        albumViewBlock.setPadding(10, 10, 10, 10);
        albumViewBlock.setOrientation(LinearLayout.VERTICAL);

        Bitmap bitmapImage = BitmapFactory.decodeFile(lastInAlbum.filePath);
        final ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(imgSize, imgSize));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageBitmap(bitmapImage);
        final PhotoAlbum albumLoc = album;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigator.navigateToLocalAlbumFragment(context, albumLoc);
            }
        });
        albumViewBlock.addView(imageView);

        final TextView albumName = new TextView(context);
        albumName.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        albumName.setText(album.title);
        albumName.setTextSize(18);
        albumName.setTextColor(Color.DKGRAY);
        albumViewBlock.addView(albumName);

        final TextView photosCount = new TextView(context);
        photosCount.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        photosCount.setText(context.getString(R.string.count_of_photos_in_album, album.size));
        photosCount.setTextSize(10);
        photosCount.setTextColor(Color.DKGRAY);
        albumViewBlock.addView(photosCount);

        gridLayout.addView(albumViewBlock);
    }
}
