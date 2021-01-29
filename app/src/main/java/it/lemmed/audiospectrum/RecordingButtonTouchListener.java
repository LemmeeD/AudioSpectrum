package it.lemmed.audiospectrum;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaRecorder;
import android.os.Build;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import it.lemmed.audiospectrum.settings.Formats;

public class RecordingButtonTouchListener implements View.OnTouchListener {
    //FIELDS
    private static MediaRecorder recorder;
    private File tempFileName;

    //CONSTRUCTORS
    public RecordingButtonTouchListener() {
        //LogDebug.log("ISTANZIATO UN: MediaRecorder");
        //recorder = new MediaRecorder();
    }

    //METHODS
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogDebug.log("OnTouch");
        FloatingActionButton button = (FloatingActionButton) v;
        Animation recordingAlphaFlash = AnimationUtils.loadAnimation(button.getContext(), R.anim.recording_alpha_flash);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(button.getContext());
        Formats format = Formats.getEnumFormats(preferences.getString("key_format", "AMR_NB__THREE_GPP"));
        this.tempFileName = new File(MainActivity.userDataDirectory.getPath(), "temp"+format.getExtension());
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
                new int[] { -android.R.attr.state_pressed }
        };
        int[] colors = new int[] {
                v.getContext().getResources().getColor(R.color.verdeAcqua),
                v.getContext().getResources().getColor(R.color.arancione)
        };
        button.setBackgroundTintList(new ColorStateList(states, colors));
        //MediaRecorder recorder = new MediaRecorder();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogDebug.log("Touch: ACTION_DOWN");
                //temporal directory..
                this.startRecording(v, this.tempFileName, format);
                button.startAnimation(recordingAlphaFlash);
                break;
            case MotionEvent.ACTION_UP:
                v.performClick();
                LogDebug.log("Touch: ACTION UP");
                button.clearAnimation();
                this.stopRecording(v, tempFileName, format);
                button.setBackgroundColor(v.getContext().getResources().getColor(R.color.arancione));
                //recordingAlphaFlash.reset();
                break;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startRecording(View v, File tempFileName, Formats format) {
        //FloatingActionButton button = (FloatingActionButton) v;
        LogDebug.log("ISTANZIATO UN: MediaRecorder");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(format.getOutputFormat());
        recorder.setAudioEncoder(format.getAudioEncoder());
        recorder.setOutputFile(tempFileName);
        //recorder.setAudioSamplingRate();
        //LogDebug.log("PARAMETRI MediaRecorder: OutputFormat="+format.getOutputFormat()+", AudioEncoder="+format.getAudioEncoder()+", SamplingRate="+..);
        LogDebug.log("PARAMETRI MediaRecorder: OutputFormat="+format.getOutputFormat()+", AudioEncoder="+format.getAudioEncoder());
        try {
            recorder.prepare();
            recorder.start();
            LogDebug.log("START RECORDING");
        } catch (IOException e) {
            LogDebug.log("IOException");
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.incorrect_shake);
            animation.setRepeatCount(3);
            v.startAnimation(animation);
            e.printStackTrace();
        }
    }

    private void stopRecording(View v, final File tempFileName, Formats format) {
        try {
            recorder.stop();
            LogDebug.log("STOP RECORDING");
            recorder.release();
        } catch (IllegalStateException e) {
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.incorrect_shake);
            animation.setRepeatCount(3);
            v.startAnimation(animation);
            LogDebug.log("ERROR. Tried to stop recorder object prior to the initialization");
            //HandleAudio.tempHandle();
            recorder.release();
            return;
            //e.printStackTrace();
        } catch (RuntimeException e) {
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.incorrect_shake);
            animation.setRepeatCount(3);
            v.startAnimation(animation);
            LogDebug.log("RUNTIME EXCEPTION.....");
            recorder.release();
            return;
        }

        finalizeRecording(v, tempFileName);
    }

    //AUXILIARY METHODS
    public static void finalizeRecording(View v, File tempFileName) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
        builder.setTitle(v.getResources().getString(R.string.fabtl_class01));
        final EditText input = new EditText(v.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(v.getResources().getString(R.string.fabtl_class02), new FinalizePositiveRecordingDialog(v, tempFileName, input));
        builder.setNegativeButton(v.getResources().getString(R.string.fabtl_class03), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                boolean done = tempFileName.delete();
                if (done) LogDebug.log("CANCEL ---> File succesfully deleted");
                else LogDebug.log("ERROR while deleting file");
            }
        });
        builder.show();
    }
}
