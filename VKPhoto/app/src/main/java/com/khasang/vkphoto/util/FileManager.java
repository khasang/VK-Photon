package com.khasang.vkphoto.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.khasang.vkphoto.presentation.model.PhotoAlbum;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static final String BASE_DIR_NAME = "VK Photo";
    private static String BASE_DIR_PATH = "";
    public static final String BASE_DIRECTORY = "base_directory";
    public static final String JPEG_FORMAT = "/%d.jpg";

    public static String createAlbumDirectory(String albumName, Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        File baseDirectory = getBaseDirectory(defaultSharedPreferences);
        if (baseDirectory == null) {
            initBaseDirectory(context);
            baseDirectory = getBaseDirectory(defaultSharedPreferences);
        }
        assert baseDirectory != null;
        File albumDirectory = new File(baseDirectory.getAbsolutePath() + "/" + albumName);
        if (checkDirectoryExists(albumDirectory) || albumDirectory.mkdirs()) {
            return albumDirectory.getAbsolutePath();
        } else {
            return null;
        }
    }

    public static boolean deleteAlbumDirectory(String path) {
        File albumDirectory = new File(path);
        boolean success = false;
        success = checkDirectoryExists(albumDirectory) && deleteDir(albumDirectory);
        Logger.d("FileManager. deleteAlbumDirectory. success?=" + success);
        return success;
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    public static boolean initBaseDirectory(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        File storageDirectory = StorageUtils.getStorageDirectories(context);
        if (storageDirectory == null) {
            Logger.d("storageDirectory = null");
            return false;
        }
        BASE_DIR_PATH = storageDirectory.getAbsolutePath() + "/" + BASE_DIR_NAME;
        File baseDirectory = new File(BASE_DIR_PATH);
        if (checkDirectoryExists(baseDirectory) || baseDirectory.mkdirs()) {
            if (!defaultSharedPreferences.getString(BASE_DIRECTORY, "").equals(baseDirectory.getAbsolutePath())) {
                SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                editor.putString(BASE_DIRECTORY, baseDirectory.getAbsolutePath());
                editor.apply();
            }
            return true;
        }
        return false;
    }

    public static File getBaseDirectory(SharedPreferences sharedPreferences) {
        String basePath = sharedPreferences.getString(BASE_DIRECTORY, "");
        if (basePath.equals("")) {
            return null;
        }
        File baseDirectory = new File(basePath);
        return checkDirectoryExists(baseDirectory) ? baseDirectory : null;
    }

    private static boolean checkDirectoryExists(File folder) {
        return folder.exists() && folder.isDirectory();
    }

    public static File saveImage(String urlPath, PhotoAlbum photoAlbum, int photoId) {
        int count;
        File file = null;
        try {
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            String targetFileName = String.format(JPEG_FORMAT, photoId);
//            String folderPath = photoAlbum.filePath + "/";
            String folderPath = replaceIdWithNameInAlbumPath(photoAlbum);
            String filePath = folderPath + targetFileName;
            File folder = new File(folderPath);
            if (!checkDirectoryExists(folder)) {
                folder.mkdirs();//If there is no folder it will be created.
            }
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(filePath, false);
            byte data[] = new byte[1024];
            while ((count = input.read(data)) != -1 && !Thread.currentThread().isInterrupted()) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            file = new File(filePath);
        } catch (Exception e) {
            Logger.d(e.toString());
        }
        return file;
    }

    private static String replaceIdWithNameInAlbumPath(PhotoAlbum photoAlbum){
        String albumPathFixed = photoAlbum.filePath;
        if (!"".equals(photoAlbum.title)){
            String albumID = String.valueOf(photoAlbum.id);
            String albumName = photoAlbum.title;
            albumPathFixed = albumPathFixed.replace(albumID, albumName);
//            checkIfDirNameExist(albumPathFixed, photoAlbum);
        }
        return albumPathFixed;
    }

    //проверить имя альбома на уникальность
    //код не завершен. необходимо дописывать метод перед его подключением
    private static String checkIfDirNameExist(String albumPathToBeFixed, PhotoAlbum photoAlbum) {
        File folder = new File(BASE_DIR_PATH);
        File[] allFilesInBaseDir = folder.listFiles();
        List<String> foldersPathsInBaseDir = new ArrayList<>();
        for (File fileInBaseDir : allFilesInBaseDir) {
            if (fileInBaseDir.isDirectory()) {
                foldersPathsInBaseDir.add(fileInBaseDir.getAbsolutePath());
            }
        }
        if (foldersPathsInBaseDir.contains(albumPathToBeFixed)) {//тут точно мистейк
            Logger.d("oh shit!");
            String foundPhotoAlbumPath = foldersPathsInBaseDir.get(foldersPathsInBaseDir.indexOf(albumPathToBeFixed));
            String foundPhotoAlbumName = foundPhotoAlbumPath.substring(foundPhotoAlbumPath.lastIndexOf('/') + 1);
            //проверить, не фиксили ли мы это имя альбома раньше
            //потом кто-нибудь заменит это на регекс
            if (foundPhotoAlbumName.endsWith(" (2)") || foundPhotoAlbumName.endsWith(" (3)") ||
                    foundPhotoAlbumName.endsWith(" (4)") || foundPhotoAlbumName.endsWith(" (5)") ||
                    foundPhotoAlbumName.endsWith(" (6)") || foundPhotoAlbumName.endsWith(" (7)") ||
                    foundPhotoAlbumName.endsWith(" (8)") || foundPhotoAlbumName.endsWith(" (9)") ||
                    foundPhotoAlbumName.endsWith(" (10)") || foundPhotoAlbumName.endsWith(" (11)") ||
                    foundPhotoAlbumName.endsWith(" (12)") || foundPhotoAlbumName.endsWith(" (13)") ||
                    foundPhotoAlbumName.endsWith(" (14)") || foundPhotoAlbumName.endsWith(" (15)") ||
                    foundPhotoAlbumName.endsWith(" (16)") || foundPhotoAlbumName.endsWith(" (17)") ||
                    foundPhotoAlbumName.endsWith(" (18)") || foundPhotoAlbumName.endsWith(" (19)")) {
                int timesCopied = Integer.valueOf(foundPhotoAlbumName.substring(
                        foundPhotoAlbumName.lastIndexOf("(") + 1, foundPhotoAlbumName.lastIndexOf(")")));
                foundPhotoAlbumName = foundPhotoAlbumName.replace(String.valueOf(timesCopied), String.valueOf(++timesCopied));
            } else {
                foundPhotoAlbumName += " (2)";
            }
            Logger.d("FileManager. new PhotoAlbumName" + foundPhotoAlbumName);
            albumPathToBeFixed = albumPathToBeFixed.substring(foundPhotoAlbumPath.lastIndexOf('/') + 1) + foundPhotoAlbumName;
            photoAlbum.title = foundPhotoAlbumName;
        }
        return albumPathToBeFixed;
    }
}
