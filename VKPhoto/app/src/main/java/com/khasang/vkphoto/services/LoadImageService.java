package com.khasang.vkphoto.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.Toast;

import com.khasang.vkphoto.domain.adapters.ImageAdapter;
import com.khasang.vkphoto.model.Photo;
import com.khasang.vkphoto.ui.activities.GalleryActivity;
import com.khasang.vkphoto.ui.activities.PhotoActivity;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by admin on 13.02.2016.
 */
public class LoadImageService extends AsyncTask<String, Bitmap, Bitmap> {
    private Context context;
    private ImageAdapter adapter;

    private Bitmap bitmap;
    private ArrayList<Bitmap> bitmapList;

    public LoadImageService(Context context) {
        bitmapList = new ArrayList<>();
        this.context = context;
        adapter = new ImageAdapter(context,this.bitmapList);
    }

    private ProgressDialog pDialog;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading Image ....");
        pDialog.show();
    }

    public ImageAdapter getAdapter() {
        return adapter;
    }

    protected Bitmap doInBackground(String... args) {
        try {
            for (int i = 0; i < args.length; i++) {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[i]).getContent());
                if (bitmap != null) {
                    bitmapList.add(bitmap);
                } else {
                    Toast.makeText(context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public ArrayList<Bitmap> getBitmapList() {
        return bitmapList;
    }

    protected void onPostExecute(Bitmap image) {
        if (context.getClass()==GalleryActivity.class) {
            ((GalleryActivity) context).updateAdapter();
            pDialog.dismiss();
        }else if(context.getClass()== PhotoActivity.class){
            ((PhotoActivity) context).updateAdapter();
            pDialog.dismiss();
        }else {
            pDialog.dismiss();
            Toast.makeText(context, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
        }
    }
}
