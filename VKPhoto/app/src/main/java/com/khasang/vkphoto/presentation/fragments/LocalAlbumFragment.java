package com.khasang.vkphoto.presentation.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.khasang.vkphoto.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TAU on 27.02.2016.
 */
public class LocalAlbumFragment extends Fragment {
    public static final String TAG = LocalAlbumFragment.class.getSimpleName();
    public static final String PHOTOALBUM = "photoalbum";
    private Context context;

    public static LocalAlbumFragment newInstance(PhotoAlbum photoAlbum) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTOALBUM, photoAlbum);
        LocalAlbumFragment fragment = new LocalAlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_local_album, container, false);
        PhotoAlbum photoAlbum = getArguments().getParcelable(PHOTOALBUM);
        if (photoAlbum != null) Logger.d("photoalbum " + photoAlbum.title);
        else Logger.d("wtf where is album?");
        final GridLayout localAlbumGallery = (GridLayout) rootView.findViewById(R.id.localAlbumGridLA);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int imgSize = (metrics.widthPixels - 40) / 2;

        LocalPhotoSource localPhotoSource = new LocalPhotoSource(context);
        List<Photo> allPhotosInAlbum;
        try {allPhotosInAlbum = localPhotoSource.getPhotosByAlbumPath(photoAlbum.filePath);}
        catch (NullPointerException e) {allPhotosInAlbum = new ArrayList<>();}

        for (Photo photo : allPhotosInAlbum) {
            Bitmap bitmapImage = BitmapFactory.decodeFile(photo.filePath);
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(imgSize, imgSize));
            imageView.setPadding(10, 10, 10, 10);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageBitmap(bitmapImage);
            final Photo photoLoc = photo;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msg = !"".equals(photoLoc.text) ? photoLoc.text : String.valueOf(photoLoc.id);
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
            localAlbumGallery.addView(imageView);
        }
        return rootView;
    }
}
