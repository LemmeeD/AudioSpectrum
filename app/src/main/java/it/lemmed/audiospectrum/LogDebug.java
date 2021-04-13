package it.lemmed.audiospectrum;

import android.util.Log;

public class LogDebug {
    public static boolean DEBUGGING = true;
    public static String TAG = "DEBUG";

    public static void log(String string) {
        if (DEBUGGING) {
            Log.i(TAG, string);
        }
        else {
        }
    }
}
