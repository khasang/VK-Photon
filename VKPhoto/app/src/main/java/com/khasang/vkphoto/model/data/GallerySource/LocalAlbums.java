package com.khasang.vkphoto.model.data.GallerySource;

import android.app.Activity;
import android.util.Log;
import com.khasang.vkphoto.services.AllImagesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 20.02.2016.
 * индуский код
 */
public class LocalAlbums {
    private Map<String, List<LocalPhoto>> albumMap;
    public LocalAlbums(Activity context) {
        albumMap = new HashMap<>();
        fillLocalAlbum(context);
    }

    public void showInLogAllPhotos() {
        Set<String> strings = albumMap.keySet();
        List<String> result =new ArrayList<>();
        for(String temp : strings){
            List<LocalPhoto> localPhotos = albumMap.get(temp);
            System.out.println(temp);
            for (LocalPhoto photo : localPhotos){
                System.out.println("\t name :" +photo.getName()+" path: "+photo.getPath());
            }
            System.out.println("****************************************");
        }
    }

    public List<LocalPhoto> getPhotos(String albumName){
        return albumMap.get(albumName);
    }

    public List<String> getPhotosPath(String albumName){
        List<String> result = new ArrayList<>();
        List<LocalPhoto> localPhotos = albumMap.get(albumName);
        for (LocalPhoto temp : localPhotos){
            result.add(temp.getPath());
        }
        return result;
    }

    public List<String> getAlbums(){
        Set<String> strings = albumMap.keySet();
        List<String> result =new ArrayList<>();
        for(String temp : strings){
            result.add(temp);
        }
        return result;
    }
    private void fillLocalAlbum(Activity context) {
        List<String> allImagesPath = AllImagesService.listOfAllImages(context);
        if(allImagesPath.isEmpty()){
            Log.d("error","allImagesPath is empty");
            return;
        }
        String albumName = "";
        for(String temp : allImagesPath){
            String[] split = temp.split("/");
            albumName = split[split.length-2];
            String photoName = getPhotoName(split);

            if (!albumMap.containsKey(albumName)){
                ArrayList<LocalPhoto> localPhotos = new ArrayList<>();
                localPhotos.add(new LocalPhoto(temp,photoName));
                albumMap.put(albumName,localPhotos);
            }else if (albumMap.containsKey(albumName)){
                List<LocalPhoto> localPhotos = albumMap.get(albumName);
                localPhotos.add(new LocalPhoto(temp,photoName));
            }
        }
    }

    private String getPhotoName(String[] split) {
        String nameWithPoint = split[split.length-1];
        return nameWithPoint.replace(".jpg","");
    }
}
