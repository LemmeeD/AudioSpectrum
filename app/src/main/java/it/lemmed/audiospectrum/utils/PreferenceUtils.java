package it.lemmed.audiospectrum.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;
import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.MainActivity;
import it.lemmed.audiospectrum.R;

public class PreferenceUtils {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String returnStringValue(int progress) {
        String[] strings = MainActivity.recyclerView.getContext().getApplicationContext().getResources().getStringArray(R.array.stroke_width_entries);      //poco elegante...
        if (progress == 1) return strings[0];
        else if (progress == 2) return strings[1];
        else if (progress == 3) return strings[2];
        else if (progress == 4) return strings[3];
        else if (progress == 5) return strings[4];
        else return "PreferenceUtils.returnStringValue() --> Error.";
    }

    public static boolean getWaveformVisualizationPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("key_visualization_waveform", true)) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean getFftVisualizationPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("key_visualization_fft", true)) {
            return true;
        }
        else {
            return false;
        }
    }

    public static int getFftSizePreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int result = 1024;
        try {
            result = Integer.parseInt(preferences.getString("key_capture_size", "1024"));
        }
        catch (ClassCastException e) {
            LogDebug.log("getFftSizePreference --> "+e.getMessage());
        }
        return result;
    }

    public static float getStrokeWidthPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        float result = 2f;
        try {
            result = Float.parseFloat(preferences.getString("key_stroke_width", "2"));
        }
        catch (ClassCastException e) {
            LogDebug.log("getStrokeWidthPreference --> "+e.getMessage());
        }
        return result;
    }

    public static boolean getGraphicPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean result = false;
        try {
            result = preferences.getBoolean("key_visualization_graphic", false);
        }
        catch (ClassCastException e) {
            LogDebug.log("getStrokeWidthPreference --> "+e.getMessage());
        }
        return result;
    }

    public static boolean getFftConfPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean result = false;
        try {
            result = preferences.getBoolean("key_fft_graphic", false);
        }
        catch (ClassCastException e) {
            LogDebug.log("getStrokeWidthPreference --> "+e.getMessage());
        }
        return result;
    }
}
