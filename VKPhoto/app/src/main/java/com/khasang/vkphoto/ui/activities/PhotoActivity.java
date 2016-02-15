package com.khasang.vkphoto.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.services.LoadImageService;

public class PhotoActivity extends AppCompatActivity {
    private ImageView imageView;
    LoadImageService imageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        this.imageView = (ImageView) findViewById(R.id.photo_gridview);
        Intent intent = getIntent();
        String exequte = intent.getStringExtra("photo");

        imageService = new LoadImageService(PhotoActivity.this);
        imageService.execute(exequte);
    }

    public void updateAdapter() {
        imageView.setImageBitmap(imageService.getBitmapList().get(0));
    }
}
