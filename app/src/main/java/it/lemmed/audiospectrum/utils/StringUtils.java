package it.lemmed.audiospectrum.utils;

import android.util.Log;

import it.lemmed.audiospectrum.LogDebug;

public class StringUtils {
    //expected to have duration in ms..
    public static String returnStringifiedDuration(int duration) {
        if (duration < 1000) {
            return Integer.toString(duration)+" ms";
        }
        else if ((duration >= 1000) && (duration < 60000)) {
            return Integer.toString(duration/1000)+" s";
        }
        else if (duration >= 60000) {
            return Integer.toString(duration/60000)+ " m";
        }
        else {
            return null;
        }
    }

    //rate in mHz
    public static String returnStringifiedSamplingRate(int rate) {
        if (rate < 1000) {
            return Integer.toString(rate)+" mHz";
        }
        else if ((rate >= 1000) && (rate < 1000000)) {
            return Double.toString(rate/1000.0)+" Hz";
        }
        else if ((rate >= 1000000) && (rate < 1000000000)) {
            return Double.toString(rate/1000000.0)+ " kHz";
        }
        else {
            return null;
        }
    }

    //f frequency
    public static String returnStringifiedFrequency(double f) {
        if (f < 1000) {
            return Long.toString(Math.round(f))+" mHz";
        }
        else if ((f >= 1000) && (f < 1000000)) {
            return Long.toString(Math.round(f/1000.0))+" Hz";
        }
        else if ((f >= 1000000) && (f < 1000000000)) {
            return Long.toString(Math.round(f/1000000.0))+ " kHz";
        }
        else {
            return null;
        }
    }

    public static String returnStringifiedPhase(double f) {
        if (f < 1000) {
            return Long.toString(Math.round(f))+" mHz";
        }
        else if ((f >= 1000) && (f < 1000000)) {
            return Long.toString(Math.round(f/1000.0))+" Hz";
        }
        else if ((f >= 1000000) && (f < 1000000000)) {
            return Long.toString(Math.round(f/1000000.0))+ " kHz";
        }
        else {
            return null;
        }
    }
}
