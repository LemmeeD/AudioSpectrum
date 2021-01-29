package it.lemmed.audiospectrum.audiocollection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.MainActivity;
import it.lemmed.audiospectrum.settings.Formats;
import it.lemmed.audiospectrum.utils.FileUtils;

public class AudioCollectionProvider {
    //FIELDS

    //CONSTRUCTORS

    //METHODS
    private static List<RowRecord> queryAudioCollection(Context context) {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.MIME_TYPE
        };
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        );
        if (cursor != null) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
            List<RowRecord> records = new LinkedList<>();
            int count = 0;
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                int type = cursor.getType(typeColumn);
                //records.add(new RowRecord(name, Integer.toString(type), duration, size));
                count++;
            }
            cursor.close();
            return records;
        }
        else {
            return new LinkedList<RowRecord>();
        }
    }

    private static List<RowRecord> queryAudioCollectionFromName(Context context, String nameToSearch) {
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.MIME_TYPE
        };
        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                sortOrder
        );
        if (cursor != null) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int typeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
            List<RowRecord> records = new LinkedList<>();
            int count = 0;
            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                int type = cursor.getType(typeColumn);
                if (name.contains(nameToSearch)) {
                    //records.add(new RowRecord(name, Integer.toString(type), duration, size));
                }
                count++;
            }
            cursor.close();
            return records;
        }
        else {
            LogDebug.log("cursor is null");
            return new LinkedList<RowRecord>();
        }
    }

    //JUST FOR TESTING..
    @RequiresApi(api = Build.VERSION_CODES.P)
    private static List<RowRecord> queryFolder() {
        File musicDirectory = MainActivity.userDataDirectory;
        if (musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles(new AudioFileFilter());
            if (files == null) {
                LogDebug.log("queryFolder() --> files is null..");
                return new LinkedList<RowRecord>();
            }
            else {
                List<RowRecord> records = new LinkedList<>();
                for (int i = 0; i < files.length; i++) {
                    records.add(new RowRecord(
                            Formats.stripFromExtension(files[i].getName()),
                            Formats.getExtensionFromFilename(files[i].getName(), true),
                            files[i].length(),
                            FileUtils.getDuration(files[i]),
                            Formats.getFormatFromFilename(files[i].getName()).getOutputFormat(),
                            files[i].getAbsolutePath(),
                            FileUtils.seekUriFromAbsolutePath(files[i].getAbsolutePath())
                    ));
                }
                return records;
            }
        }
        else {
            return new LinkedList<RowRecord>();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private static List<RowRecord> queryFolder(String nameToSearch) {
        File musicDirectory = MainActivity.userDataDirectory;
        if (musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles(new AudioFileFilter());
            if (files == null) {
                LogDebug.log("queryFolder(String nTS) --> files is null..");
                return new LinkedList<RowRecord>();
            }
            else {
                List<RowRecord> records = new LinkedList<>();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().contains(nameToSearch)) {
                        records.add(new RowRecord(
                                Formats.stripFromExtension(files[i].getName()),
                                Formats.getExtensionFromFilename(files[i].getName(), true),
                                files[i].length(),
                                FileUtils.getDuration(files[i]),
                                Formats.getFormatFromFilename(files[i].getName()).getOutputFormat(),
                                files[i].getAbsolutePath(),
                                FileUtils.seekUriFromAbsolutePath(files[i].getAbsolutePath())
                        ));
                    }
                }
                return records;
            }
        }
        else {
            return new LinkedList<RowRecord>();
        }
    }

    public synchronized static List<RowRecord> executeQueryFolder() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<RowRecord> result = null;
        Future future = executorService.submit(new Callable<List<RowRecord>>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public List<RowRecord> call() throws Exception {
                return queryFolder();
            }
        });
        try {
            result = (List<RowRecord>) future.get();
        } catch (InterruptedException | ExecutionException e) {
            LogDebug.log("executeQueryFolder() --> Cannot compute queryFolder() due to "+e.getMessage());
        }
        executorService.shutdown();
        return result;
    }

    public synchronized static List<RowRecord> executeQueryFolder(String nameToSearch) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<RowRecord> result = null;
        Future future = executorService.submit(new Callable<List<RowRecord>>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public List<RowRecord> call() throws Exception {
                return queryFolder(nameToSearch);
            }
        });
        try {
            result = (List<RowRecord>) future.get();
        } catch (InterruptedException | ExecutionException e) {
            LogDebug.log("executeQueryFolder(StringnTS) --> Cannot compute queryFolder() due to "+e.getMessage());
        }
        executorService.shutdown();
        return result;
    }
}
