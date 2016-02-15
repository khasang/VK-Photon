package com.khasang.vkphoto.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.khasang.vkphoto.R;
import com.khasang.vkphoto.services.LoadImageService;
import com.khasang.vkphoto.util.Logger;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;



public class GalleryActivity extends AppCompatActivity {

    private GridView imageGrid;
    private LoadImageService imageService;
    private String[] photoUrl;

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
                System.out.println(photoUrl[position]);
                Intent inten = new Intent(GalleryActivity.this, PhotoActivity.class);
                inten.putExtra("photo", photoUrl[position]);
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
                        Logger.d(jsonArray.getJSONObject(i).toString());
                        photoList.add(new VKApiPhoto(jsonArray.getJSONObject(i)));
                        showInGridView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void showInGridView() {
                photoUrl = new String[photoList.size()];
                for (int i = 0; i < photoList.size(); i++) {
                    photoUrl[i] = photoList.get(i).photo_604;
                }

                try {
                    imageService = new LoadImageService(GalleryActivity.this);
                    imageService.execute(photoUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void updateAdapter() {
        imageGrid.setAdapter(imageService.getAdapter());
    }
}
