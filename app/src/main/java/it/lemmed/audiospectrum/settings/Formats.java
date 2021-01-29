package it.lemmed.audiospectrum.settings;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Build;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.P)
public enum Formats {
    AAC_ELD__THREE_GPP(MediaRecorder.OutputFormat.THREE_GPP , MediaRecorder.AudioEncoder.AAC_ELD , ".3gp"),
    AAC_ELD__AAC(MediaRecorder.OutputFormat.AAC_ADTS , MediaRecorder.AudioEncoder.AAC_ELD , ".aac"),
    AMR_WB__THREE_GPP(MediaRecorder.OutputFormat.THREE_GPP , MediaRecorder.AudioEncoder.AMR_WB, ".3gp"),
    AMR_WB__AMR(MediaRecorder.OutputFormat.AMR_WB , MediaRecorder.AudioEncoder.AMR_WB, ".amr"),
    AMR_NB__THREE_GPP(MediaRecorder.OutputFormat.THREE_GPP , MediaRecorder.AudioEncoder.AMR_NB, ".3gp"),
    AMR_NB__AMR(MediaRecorder.OutputFormat.AMR_NB , MediaRecorder.AudioEncoder.AMR_NB, ".amr"),
    MP3(AudioFormat.ENCODING_MP3, AudioFormat.ENCODING_MP3, ".mp3"),
    WAV(AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_16BIT, ".wav");

    private final int outputFormat;
    private final int audioEncoder;
    private final String extension;

    Formats(int outputFormat, int audioEncoder, String extension) {
        this.outputFormat = outputFormat;
        this.audioEncoder = audioEncoder;
        this.extension = extension;
    }

    public String getExtension() { return this.extension; }

    public int getOutputFormat() { return this.outputFormat; }

    public int getAudioEncoder() { return this.audioEncoder; }

    public static Formats getEnumFormats(String key) {
        if (key.equals("Format:3GPP, Encoder:AAC-ELD")  || (key.equals("AAC_ELD__THREE_GPP"))){
            return AAC_ELD__THREE_GPP;
        }
        else if (key.equals("Format:AAC, Encoder:AAC-ELD") || key.equals("AAC_ELD__AAC")) {
            return AAC_ELD__AAC;
        }
        else if (key.equals("Format:3GPP, Encoder:AMR-WB") || key.equals("AMR_WB__THREE_GPP")) {
            return AMR_WB__THREE_GPP;
        }
        else if (key.equals("Format:AMR-WB, Encoder:AMR-WB") || key.equals("AMR_WB__AMR")) {
            return AMR_WB__AMR;
        }
        else if (key.equals("Format:3GPP, Encoder:AMR-NB") || key.equals("AMR_NB__THREE_GPP")) {
            return AMR_NB__THREE_GPP;
        }
        else if (key.equals("Format:AMR-NB, Encoder:AMR-NB") || key.equals("AMR_NB__AMR")) {
            return AMR_NB__AMR;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    public static boolean isExtensionGoodForPlaying(String extension) {
        if (extension.equals(AAC_ELD__THREE_GPP.getExtension()) || extension.equals(AAC_ELD__THREE_GPP.getExtension().substring(1))) { return true; }
        else if (extension.equals(AAC_ELD__AAC.getExtension())  || extension.equals(AAC_ELD__AAC.getExtension().substring(1))) { return true; }
        else if (extension.equals(AMR_WB__THREE_GPP.getExtension())  || extension.equals(AMR_WB__THREE_GPP.getExtension().substring(1))) { return true; }
        else if (extension.equals(AMR_WB__AMR.getExtension()) || extension.equals(AMR_WB__AMR.getExtension().substring(1))) { return true; }
        else if (extension.equals(AMR_NB__THREE_GPP.getExtension()) || extension.equals(AMR_NB__THREE_GPP.getExtension().substring(1))) { return true; }
        else if (extension.equals(AMR_NB__AMR.getExtension())  || extension.equals(AMR_NB__AMR.getExtension().substring(1))) { return true; }
        else if (extension.equals(".mp3") || extension.equals("mp3")) { return true; }
        else if (extension.equals(".wav")  || extension.equals("wav")) { return true; }
        else { return false; }
    }

    public static String stripFromExtension(String fileName) {
        if (fileName.equals("")) return "";
        int pointIndex = -1;
        for (int i = fileName.length()-1; i >= 0; i--) {
            if (fileName.charAt(i) == '.') {
                pointIndex = i;
                break;
            }
        }
        return fileName.substring(0, pointIndex);
    }

    public static String getExtensionFromFilename(String fileName, boolean withPoint) {
        if (fileName.equals("")) return "";
        for (int i = fileName.length()-1; i >= 0; i--) {
            if (fileName.charAt(i) == '.') {
                if (withPoint) {
                    return fileName.substring(i);
                }
                else {
                    return fileName.substring(i+1);
                }
            }
        }
        return null;
    }

    public static Formats getFormatFromFilename(String fileName) {
        String extension = getExtensionFromFilename(fileName, true);
        return getFormatFromExtension(extension);
    }

    public static Formats getFormatFromExtension(String extension) {
        if (extension.equals(AAC_ELD__THREE_GPP.getExtension()) || extension.equals(AAC_ELD__THREE_GPP.getExtension().substring(1))) { return AAC_ELD__THREE_GPP; }
        else if (extension.equals(AAC_ELD__AAC.getExtension()) || extension.equals(AAC_ELD__AAC.getExtension().substring(1))) { return AAC_ELD__AAC; }
        else if (extension.equals(AMR_WB__THREE_GPP.getExtension()) || extension.equals(AMR_WB__THREE_GPP.getExtension().substring(1))) { return AMR_WB__THREE_GPP; }
        else if (extension.equals(AMR_WB__AMR.getExtension())  || extension.equals(AMR_WB__AMR.getExtension().substring(1))) { return AMR_WB__AMR; }
        else if (extension.equals(AMR_NB__THREE_GPP.getExtension()) || extension.equals(AMR_NB__THREE_GPP.getExtension().substring(1))) { return AMR_NB__THREE_GPP; }
        else if (extension.equals(AMR_NB__AMR.getExtension()) || extension.equals(AMR_NB__AMR.getExtension().substring(1))) { return AMR_NB__AMR; }
        else if (extension.equals(".mp3") || extension.equals("mp3")) { return MP3; }
        else if (extension.equals(".wav") || extension.equals("wav")) { return WAV; }
        else { return null; }
    }
}
