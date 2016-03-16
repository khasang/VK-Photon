package com.khasang.vkphoto.domain.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.khasang.vkphoto.R;
import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.util.ArrayList;
import java.util.List;

/** Created by bugtsa on 16-Mar-16. */
public class SelectAlbumItemAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context mContext;
    private List<PhotoAlbum> arrayList = new ArrayList<>();

    public SelectAlbumItemAdapter(Context context, List<PhotoAlbum> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public CharSequence getItem(int position) {
        return arrayList.get(position).title;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.dialog_customlistitem, null);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText(arrayList.get(position).title);
        ImageView myImage = (ImageView) convertView.findViewById(R.id.imageViewDialog);

        Glide.with(myImage.getContext())
                .load(arrayList.get(position).thumbFilePath)
                .override(100, 100)
                .fitCenter()
                .crossFade()
                .error(R.drawable.vk_share_send_button_background)
                .into(myImage);

        Button button = (Button) convertView.findViewById(R.id.button);
        button.setTag(position);
        button.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
//        Integer index = (Integer) v.getTag();
//        if (mToast != null) mToast.cancel();
//        mToast = Toast.makeText(mContext, "Clicked button " + index, Toast.LENGTH_SHORT);
//        mToast.show();
    }
}
