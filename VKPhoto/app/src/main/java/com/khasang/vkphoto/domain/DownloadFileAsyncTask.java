package com.khasang.vkphoto.domain;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.khasang.vkphoto.data.local.LocalPhotoSource;
import com.khasang.vkphoto.presentation.model.Photo;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;

public class DownloadFileAsyncTask extends AsyncTask<String, Integer, File> {
    private WeakReference<ImageView> imageViewWeakReference;
    private PhotoAlbum photoAlbum;
    private Photo photo;
    private Context context;
    private LocalPhotoSource localPhotoSource;
    //    private ProgressDialog mProgressDialog;

    public DownloadFileAsyncTask(ImageView imageView, Photo photo, PhotoAlbum photoAlbum) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
        this.context = imageView.getContext().getApplicationContext();
        this.photoAlbum = photoAlbum;
        this.photo = photo;
        localPhotoSource = new LocalPhotoSource(context);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loadImage(localPhotoSource.getLocalPhoto(photo));
//        mProgressDialog.setMessage("Downloading");
//        mProgressDialog.setIndeterminate(false);
//        mProgressDialog.setMax(100);
//        mProgressDialog.setCancelable(true);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.show();
    }

    @Override
    protected File doInBackground(String... aurl) {
        return localPhotoSource.getPhotoFile(photo, photoAlbum);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        loadImage(file);
    }

    private void loadImage(File file) {
        if (file != null && file.exists() && imageViewWeakReference.get() != null) {
            Picasso.with(imageViewWeakReference.get().getContext())
                    .load(file)
                    .placeholder(android.R.drawable.progress_horizontal)
                    .error(android.R.drawable.stat_notify_error)
                    .into(imageViewWeakReference.get());
//            VKPhotoSource vkPhotoSource = new VKPhotoSource();
//            vkPhotoSource.savePhotoToAlbum(file,photoAlbum);
        }
    }

//    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        mProgressDialog.setProgress(progress[0]);
//        Logger.d("progress " + progress[0]);
//        if (mProgressDialog.getProgress() == mProgressDialog.getMax()) {
//            mProgressDialog.dismiss();
//            Toast.makeText(fa, "File Downloaded", Toast.LENGTH_SHORT).show();
//        }
//    }
}
