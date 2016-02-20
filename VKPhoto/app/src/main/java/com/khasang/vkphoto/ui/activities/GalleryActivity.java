package com.khasang.vkphoto.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.domain.adapters.ImageAdapter;
import com.khasang.vkphoto.services.AllImagesService;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class GalleryActivity extends AppCompatActivity {

    private GridView imageGrid;
    private List<String> photoUrl;
    private ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        this.imageGrid = (GridView) findViewById(R.id.gridview);
        final Intent intent = getIntent();
        int owner_id = intent.getIntExtra("owner_id", -1);
        int album_id = intent.getIntExtra("album_id", -1);
        int count = intent.getIntExtra("count", 100);
        requestByAlbumId(owner_id, album_id, count);

        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent inten = new Intent(GalleryActivity.this, PhotoActivity.class);
                inten.putExtra("photo", photoUrl.get(position));
                startActivity(inten);

            }
        });
    }

    private void requestByAlbumId(int userid, int albumid, int max) {
        VKRequest request = new VKRequest("photos.get", VKParameters.from(VKApiConst.ALBUM_ID, albumid, VKApiConst.OWNER_ID, userid, VKApiConst.COUNT, max));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            VKList<VKApiPhoto> photoList = new VKList<>();

            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONArray jsonArray = response.json.getJSONObject("response").getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        photoList.add(new VKApiPhoto(jsonArray.getJSONObject(i)));
                        showInGridView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void showInGridView() {
                photoUrl = new ArrayList<>();
                for (VKApiPhoto temp : photoList) {
                    photoUrl.add(temp.photo_604);
                }

                try {
                    imageAdapter = new ImageAdapter(GalleryActivity.this, photoUrl);
                    imageGrid.setAdapter(imageAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
