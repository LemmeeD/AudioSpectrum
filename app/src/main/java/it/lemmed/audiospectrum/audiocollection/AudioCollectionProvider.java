package it.lemmed.audiospectrum.audiocollection;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.File;
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
                    if ( (files[i].getName().contains(nameToSearch)) || (files[i].getName().contains(nameToSearch.toLowerCase())) ) {
                        records.add(new RowRecord(
                                Formats.stripFromExtension(files[i].getName()),
                                Formats.getExtensionFromFilename(files[i].getName(), true),
                                files[i].length(),
                                FileUtils.getDuration(files[i]),
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
