package it.lemmed.audiospectrum;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.lemmed.audiospectrum.audiocollection.RecyclerViewPopulator;
import it.lemmed.audiospectrum.settings.Formats;

public class FinalizePositiveRecordingDialog implements DialogInterface.OnClickListener {
    //FIELDS
    protected View v;
    protected EditText input;
    protected File tempFileName;

    //CONSTRUCTORS
    public FinalizePositiveRecordingDialog(View v, File tempFileName, EditText input) {
        this.v = v;
        this.input = input;
        this.tempFileName = tempFileName;
    }

    //METHODS
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onClick(DialogInterface dialog, int which) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.v.getContext());
        Formats format = Formats.getEnumFormats(preferences.getString("key_format", ""));
        String inputString = this.input.getText().toString();
        if (inputString.equals("")) {
            RecordingButtonTouchListener.finalizeRecording(v, this.tempFileName);
            return;
        }
        final File oldFile = this.tempFileName;
        final File newFile = new File(MainActivity.userDataDirectory, inputString+format.getExtension());
        //se esiste già ed è un file normale
        if (newFile.isFile()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this.v.getContext());
            builder.setTitle(v.getResources().getString(R.string.fprd_class01));
            builder.setPositiveButton(v.getResources().getString(R.string.fprd_class02), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogDebug.log("PRESSIONE TASTO POSITIVO");
                    finalizeClick(newFile, oldFile, stripFromExtension(newFile.getName()));
                    //saveFileOnMusicFolder(v, newFile, format);
                    RecyclerViewPopulator populator = new RecyclerViewPopulator(MainActivity.recyclerView.getContext(), MainActivity.recyclerView);
                    populator.populateRecyclerView();
                }
            });
            builder.setNegativeButton(v.getResources().getString(R.string.fprd_class03), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogDebug.log("PRESSIONE TASTO NEGATIVO");
                    dialog.cancel();
                    File file = new File(newFile.getAbsolutePath());
                    boolean done = file.delete();
                    if (done) LogDebug.log("CANCEL ---> File succesfully deleted");
                    else LogDebug.log("ERROR while deleting file");
                }
            });
            builder.show();
        }
        else {
            finalizeClick(newFile, oldFile, inputString);
            //saveFileOnMusicFolder(v, newFile, format);
            RecyclerViewPopulator populator = new RecyclerViewPopulator(MainActivity.recyclerView.getContext(), MainActivity.recyclerView);
            populator.populateRecyclerView();
        }
    }

    private static void finalizeClick(File newFile, File oldFile, String inputString) {
        boolean done = oldFile.renameTo(newFile);
        if (done) {
            LogDebug.log(oldFile.getName()+" renamed to "+newFile.getName());
            /*
            try {
                (new HandleAudio(newFile, inputString)).handle();
            } catch (it.lemmed.mrpickles.CommandNotFoundException e) {
                LogDebug.log(e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                LogDebug.log("AHHHHH CAZZOOOO");
                e.printStackTrace();
            }
            */
        }
        else LogDebug.log("ERROR while renaming file");
    }

    private static String stripFromExtension(String fileName) {
        if (fileName.equals("")) return "";
        int pointIndex = -1;
        for (int i = fileName.length()-1; i >= 0; i--) {
            if (fileName.charAt(i) == '.') {
                pointIndex = i;
                break;
            }
        }
        String result = fileName.substring(0, pointIndex);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void saveFileOnMusicFolder(View v, File newFile, Formats format) {
        ContentResolver resolver = v.getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, newFile.getName());
        //contentValues.put(MediaStore.MediaColumns.MIME_TYPE, format.getMimeType());
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
        Uri audioUri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream os = null;
        InputStream is = null;
        try {
            os = resolver.openOutputStream(audioUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long size = newFile.length();
        try {
            is = new FileInputStream(newFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //DA GESTIRE MEGLIO
        byte[] bytes = new byte[(int) size];
        try {
            int resultRead = is.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //da testare..
    private static void testSaveOnMusicFolderOLD(View v, File newFile) {
        String audioDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString();
        File audio = new File(audioDir, newFile.getName());
        OutputStream os;
        try {
            os = new FileOutputStream(audio);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
