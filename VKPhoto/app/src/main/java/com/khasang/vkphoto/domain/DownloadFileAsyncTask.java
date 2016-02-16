package com.khasang.vkphoto.domain;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.model.PhotoAlbum;
import com.khasang.vkphoto.model.data.local.LocalPhotoSource;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;

public class DownloadFileAsyncTask extends AsyncTask<String, Integer, File> {
    private WeakReference<ImageView> imageViewWeakReference;
    private PhotoAlbum photoAlbum;
    private Photo photo;
    private Context context;
//    private ProgressDialog mProgressDialog;

    public DownloadFileAsyncTask(ImageView imageView, Photo photo, PhotoAlbum photoAlbum) {
        this.imageViewWeakReference = new WeakReference<>(imageView);
        this.context = imageView.getContext().getApplicationContext();
        this.photoAlbum = photoAlbum;
        this.photo = photo;
//        mProgressDialog = new ProgressDialog(imageView.getContext());

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        mProgressDialog.setMessage("Downloading");
//        mProgressDialog.setIndeterminate(false);
//        mProgressDialog.setMax(100);
//        mProgressDialog.setCancelable(true);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mProgressDialog.show();
    }

    @Override
    protected File doInBackground(String... aurl) {
        return new LocalPhotoSource(context).getPhotoFile(photo, photoAlbum);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (file != null && file.exists() && imageViewWeakReference.get() != null) {
            Picasso.with(imageViewWeakReference.get().getContext())
                    .load(file)
                    .into(imageViewWeakReference.get());
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
