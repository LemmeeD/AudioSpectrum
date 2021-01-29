package it.lemmed.audiospectrum.audiocollection;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileFilter;
import it.lemmed.audiospectrum.settings.Formats;

public class AudioFileFilter implements FileFilter {
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return false;
        }
        else {
            if (pathname.isFile()) {
                String filename = pathname.getName();
                for (int i = filename.length()-1; i >= 0; i--) {
                    if (filename.charAt(i) == '.') {
                        String extension = filename.substring(i);
                        if (Formats.isExtensionGoodForPlaying(extension)) {
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                }
            }
            else {
                return false;
            }
        }
        return false;
    }
}
