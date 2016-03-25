package com.khasang.vkphoto.domain.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.khasang.vkphoto.presentation.fragments.VKCommentsFragment;
import com.khasang.vkphoto.presentation.model.Photo;

import java.util.List;

/**
 * Created by admin on 18.03.2016.
 */
public class PhotoViewPagerAdapter extends FragmentStatePagerAdapter {

    List<Photo> photoList;

    public PhotoViewPagerAdapter(FragmentManager fm, List<Photo> photoList) {
        super(fm);
        this.photoList = photoList;
    }

    @Override
    public Fragment getItem(int position) {
        return  VKCommentsFragment.newInstance(photoList.get(position));
    }

    @Override
    public int getCount() {
        return photoList.size();
    }
}
