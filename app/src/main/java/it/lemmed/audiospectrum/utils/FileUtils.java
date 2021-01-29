package it.lemmed.audiospectrum.utils;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.IOException;
import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.MainActivity;

public class FileUtils {
    //FIELDS

    //CONSTRUCTORS

    //METHODS
    public static String returnShortStringifiedSize(File file) {
        long size = file.length();
        if (size < 1024 ) {
            return Long.toString(size) + " B";
        }
        else if ((size >= 1024) && (size < 1048576)) {
            return Long.toString(size/1024) + " KB";
        }
        else if ((size >= 1048576) && (size < 1073741824)) {
            return Long.toString(size/1048576) + " MB";
        }
        else if (size >= 1073741824) {
            return Long.toString(size/1073741824) + " GB";
        }
        else {
            return null;
        }
    }

    public static String returnShortStringifiedSize(long size) {
        if (size < 1024 ) {
            return Long.toString(size) + " B";
        }
        else if ((size >= 1024) && (size < 1048576)) {
            return Long.toString(size/1024) + " KB";
        }
        else if ((size >= 1048576) && (size < 1073741824)) {
            return Long.toString(size/1048576) + " MB";
        }
        else if (size >= 1073741824) {
            return Long.toString(size/1073741824) + " GB";
        }
        else {
            return null;
        }
    }

    //return duration in ms..
    public static int getDuration(File file) {
        MediaPlayer player = new MediaPlayer();
        int duration = -1;
        try {
            player.setDataSource(file.getAbsolutePath());
        }
        catch (IOException e1) {
            LogDebug.log("File non trovato per carpirne la durata...");
        }
        catch (IllegalStateException e2) {
            LogDebug.log("MediaPlayer in stato non valido...");
        }
        catch (IllegalArgumentException e3) {
            LogDebug.log("IllegalArgumentException...");
        }
        catch (SecurityException e4) {
            LogDebug.log("SecurityException...");
        }
        try {
            player.prepare();
        }
        catch (IOException e1) {
            LogDebug.log("Player non preparato...");
        }
        duration = player.getDuration();
        player.release();
        player = null;
        return duration;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Uri seekUriFromFilename(String filename) {
        File file = new File(MainActivity.userDataDirectory.getPath(), filename);
        return Uri.parse(file.getAbsolutePath()).buildUpon().build();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Uri seekUriFromAbsolutePath(String absPath) {
        return Uri.parse(absPath).buildUpon().build();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void deleteFileWithoutName(String absPath) {
        File musicDirectory = new File(absPath);
        if (musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles();
            if (files == null) {
                LogDebug.log("deleteFileWithoutName(String aP) --> files is null..");
                return;
            }
            else {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().equals(".aac") || files[i].getName().equals(".amr")) {
                        if (files[i].delete()) {
                            LogDebug.log("deleteFileWithoutName(String aP) --> File with empty name deleted");
                        }
                        else {
                            LogDebug.log("deleteFileWithoutName(String aP) --> File with empty name found but not deleted");
                        }
                    }
                }
            }
        }
        else {
            LogDebug.log("deleteFileWithoutName(String aP) --> directory is not directory");
        }
    }

    public static void deleteAllFiles(String absPath) {
        File directory = new File(absPath);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files == null) {
                LogDebug.log("deleteAllFiles(StringaP) --> files is null..");
                return;
            }
            else {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].delete()) {
                        LogDebug.log("deleteAllFiles(StringaP) --> File "+files[i].getName()+" deleted");
                    }
                    else {
                        LogDebug.log("deleteAllFiles(StringaP) --> File with empty name found but not deleted");
                    }
                }
            }
        }
        else {
            LogDebug.log("deleteAllFiles(StringaP) --> directory "+directory.getPath()+" is not directory");
        }
    }
}
