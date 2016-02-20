package com.khasang.vkphoto.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.khasang.vkphoto.R;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        this.imageView = (ImageView) findViewById(R.id.photo_gridview);
        Intent intent = getIntent();
        String url = intent.getStringExtra("photo");
        Picasso.with(this).load(url).into(imageView);
    }
}
