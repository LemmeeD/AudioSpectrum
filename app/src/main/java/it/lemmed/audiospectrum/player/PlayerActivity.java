package it.lemmed.audiospectrum.player;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.TimeUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.R;
import it.lemmed.audiospectrum.settings.SettingsFragment;
import it.lemmed.audiospectrum.utils.FileUtils;
import it.lemmed.audiospectrum.utils.StringUtils;
import it.lemmed.audiospectrum.visualizerview.VisualizerView;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewLineFftImaginary;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewLineFftMagnitude;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewLineFftPhase;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewLineFftReal;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewLineWaveform;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewPointFftImaginary;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewPointFftMagnitude;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewPointFftPhase;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewPointFftReal;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewPointWaveform;

public class PlayerActivity extends AppCompatActivity {
    //FIELDS
    protected MediaPlayer player;
    protected Visualizer visualizer;
    protected boolean firstStart;
    protected String filename;
    protected String stringPosition;
    protected String stringExtension;
    protected String stringDuration;
    protected boolean visualizeWaveform;
    protected boolean visualizeFft;
    protected FloatingActionButton fabPlayPause;
    protected FloatingActionButton fabPrevious;
    protected FloatingActionButton fabNext;
    protected TextView textFormat;
    protected TextView textFilename;
    protected TextView textDuration;
    protected TextView textFreqMax;
    protected TextView textFreqMed;
    protected ConstraintLayout layoutContainer;
    protected ConstraintLayout layoutControls;
    //View?..
    protected LinearLayout layoutVisualizerView1;      //waveform
    protected LinearLayout layoutVisualizerView2;      //fft 1 (magnitude or real)
    protected LinearLayout layoutVisualizerView3;      //fft 2 (phase or imaginary)
    //
    protected VisualizerView visualizerView1;      //waveform
    protected VisualizerView visualizerView2;      //fft 1 (magnitude or real)
    protected VisualizerView visualizerView3;      //fft 2 (phase or imaginary)
    protected static int playIcon = R.drawable.ic_baseline_play_arrow_24;
    protected static int pauseIcon = R.drawable.ic_baseline_pause_24;
    protected int rate;
    protected boolean graphic;
    protected boolean fftConf;
    protected float strokeWidth;
    protected int fftSize;

    //CONSTRUCTORS

    //METHODS
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LogDebug.log("--- PlayerActivity ---");
        firstStart = true;
        fabPlayPause = findViewById(R.id.fab_play_pause);
        fabPrevious = findViewById(R.id.fab_previous);
        fabNext = findViewById(R.id.fab_next);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
                new int[] { -android.R.attr.state_pressed }
        };
        int[] colors = new int[] {
                this.getResources().getColor(R.color.verdeAcqua),
                this.getResources().getColor(R.color.arancione)
        };
        fabPlayPause.setBackgroundTintList(new ColorStateList(states, colors));
        fabPrevious.setBackgroundTintList(new ColorStateList(states, colors));
        fabNext.setBackgroundTintList(new ColorStateList(states, colors));
        textFilename = findViewById(R.id.text_filename);
        textDuration = findViewById(R.id.text_duration);
        textFormat = findViewById(R.id.text_format);
        textFreqMax = findViewById(R.id.text_f_max);
        textFreqMed = findViewById(R.id.text_f_med);      //tolto
        filename = getIntent().getStringExtra("nome_file");
        stringPosition = getIntent().getStringExtra("position");
        stringExtension = getIntent().getStringExtra("extension");
        stringDuration = getIntent().getStringExtra("duration");
        layoutContainer = findViewById(R.id.layout_container);
        layoutControls = findViewById(R.id.layout_controls);

        SharedPreferences preferences = this.getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

        strokeWidth = Float.parseFloat(preferences.getString("key_stroke_width", "2.0"));
        int colorWaveform = Integer.parseInt(preferences.getString("key_color_waveform", "-16776961"));
        int colorFft2 = Integer.parseInt(preferences.getString("key_color_fft1", "-65536"));
        int colorFft3 = Integer.parseInt(preferences.getString("key_color_fft2", "-19456"));

        visualizeWaveform = preferences.getBoolean("key_visualization_waveform", true);
        visualizeFft = preferences.getBoolean("key_visualization_fft", true);
        graphic = preferences.getBoolean("key_visualization_graphic", false);
        fftConf = preferences.getBoolean("key_fft_graphic", true);
        fftSize = Integer.parseInt(preferences.getString("key_capture_size", "1024"));
        rate = Integer.parseInt(preferences.getString("key_visualization_rate", "10000"));
        setRate(rate);

        layoutVisualizerView1 = findViewById(R.id.layout_visualizer_view1);
        layoutVisualizerView2 = findViewById(R.id.layout_visualizer_view2);
        layoutVisualizerView3 = findViewById(R.id.layout_visualizer_view3);

        TextView littleText1 = findViewById(R.id.little_text_title1);
        TextView littleText2 = findViewById(R.id.little_text_title2);
        TextView littleText3 = findViewById(R.id.little_text_title3);

        LogDebug.log("playerActivity started with Settings as: visualWaveform="+visualizeWaveform+", visualFft="+visualizeFft+", line="+graphic+", Fft_m/a="+fftConf+", fftSize="+fftSize+", rate="+rate);

        if (visualizeWaveform && visualizeFft) {
            //Tutti e 2
            if (graphic) {
                //caso linee
                if (fftConf) {
                    //conf fft mod/fase
                    littleText1.setText(getResources().getString(R.string.utils12));
                    littleText2.setText(getResources().getString(R.string.utils10));
                    littleText3.setText(getResources().getString(R.string.utils11));
                    visualizerView1 = new VisualizerViewLineWaveform(this, strokeWidth, colorWaveform);
                    ((View) visualizerView1).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView1.addView((View) visualizerView1);
                    visualizerView2 = new VisualizerViewLineFftMagnitude(this, strokeWidth, colorFft2);
                    ((View) visualizerView2).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewLineFftPhase(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
                else {
                    //conf fft real/im
                    littleText1.setText(getResources().getString(R.string.utils12));
                    littleText2.setText(getResources().getString(R.string.utils13));
                    littleText3.setText(getResources().getString(R.string.utils14));
                    visualizerView1 = new VisualizerViewLineWaveform(this, strokeWidth, colorWaveform);
                    ((View) visualizerView1).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView1.addView((View) visualizerView1);
                    visualizerView2 = new VisualizerViewLineFftReal(this, strokeWidth, colorFft2);
                    ((View) visualizerView2).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewLineFftImaginary(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
            }
            else {
                //caso punti
                if (fftConf) {
                    //conf fft mod/fase
                    littleText1.setText(getResources().getString(R.string.utils12));
                    littleText2.setText(getResources().getString(R.string.utils10));
                    littleText3.setText(getResources().getString(R.string.utils11));
                    visualizerView1 = new VisualizerViewPointWaveform(this, strokeWidth, colorWaveform);
                    ((View) visualizerView1).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView1.addView((View) visualizerView1);
                    visualizerView2 = new VisualizerViewPointFftMagnitude(this, strokeWidth, colorFft2);
                    ((View) visualizerView2).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewPointFftPhase(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
                else {
                    //conf fft real/im
                    littleText1.setText(getResources().getString(R.string.utils12));
                    littleText2.setText(getResources().getString(R.string.utils10));
                    littleText3.setText(getResources().getString(R.string.utils11));
                    visualizerView1 = new VisualizerViewPointWaveform(this, strokeWidth, colorWaveform);
                    ((View) visualizerView1).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView1.addView((View) visualizerView1);
                    visualizerView2 = new VisualizerViewPointFftReal(this, strokeWidth, colorFft2);
                    ((View) visualizerView2).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewPointFftImaginary(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
            }
        }
        else if (visualizeWaveform) {
            //Solo waveform
            if (graphic) {
                //caso linee
                littleText1.setText(getResources().getString(R.string.utils12));
                visualizerView2 = null;
                visualizerView3 = null;
                visualizerView1 = new VisualizerViewLineWaveform(this, strokeWidth, colorWaveform);
                ((View) visualizerView1).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                layoutVisualizerView1.addView((View) visualizerView1);
            }
            else {
                //caso punti
                littleText1.setText(getResources().getString(R.string.utils12));
                visualizerView2 = null;
                visualizerView3 = null;
                visualizerView1 = new VisualizerViewPointWaveform(this, strokeWidth, colorWaveform);
                ((View) visualizerView1).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                layoutVisualizerView1.addView((View) visualizerView1);
            }

        }
        else if (visualizeFft) {
            //Solo Fft
            if (graphic) {
                //caso linee
                if (fftConf) {
                    //caso m/p
                    littleText1.setText(getResources().getString(R.string.utils10));
                    littleText1.setText(getResources().getString(R.string.utils11));
                    visualizerView1 = null;
                    visualizerView2 = new VisualizerViewLineFftMagnitude(this, strokeWidth, colorFft2);
                    ((View) visualizerView2).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewLineFftPhase(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
                else {
                    //caso r/i
                    littleText1.setText(getResources().getString(R.string.utils13));
                    littleText1.setText(getResources().getString(R.string.utils14));
                    visualizerView1 = null;
                    visualizerView2 = new VisualizerViewLineFftReal(this, strokeWidth, colorFft2);
                    ((View) visualizerView2).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewLineFftImaginary(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
            }
            else {
                //caso punti
                if (fftConf) {
                    //caso m/p
                    littleText1.setText(getResources().getString(R.string.utils10));
                    littleText1.setText(getResources().getString(R.string.utils11));
                    visualizerView1 = null;
                    visualizerView2 = new VisualizerViewPointFftMagnitude(this, strokeWidth, colorFft2);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ((View) visualizerView2).setLayoutParams(params);
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewPointFftPhase(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(params);
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
                else {
                    //caso r/i
                    littleText1.setText(getResources().getString(R.string.utils13));
                    littleText1.setText(getResources().getString(R.string.utils14));
                    visualizerView1 = null;
                    visualizerView2 = new VisualizerViewPointFftReal(this, strokeWidth, colorFft2);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ((View) visualizerView2).setLayoutParams(params);
                    layoutVisualizerView2.addView((View) visualizerView2);
                    visualizerView3 = new VisualizerViewPointFftImaginary(this, strokeWidth, colorFft3);
                    ((View) visualizerView3).setLayoutParams(params);
                    layoutVisualizerView3.addView((View) visualizerView3);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            player.stop();
        }
        catch (IllegalStateException e) {
            LogDebug.log(e.getMessage());
            LogDebug.log("PlayerActivity: onStop --> "+e.getMessage());
        }
        catch (NullPointerException e) {
            LogDebug.log(e.getMessage());
            LogDebug.log("PlayerActivity: onStop --> "+e.getMessage());
        }
        visualizer.setDataCaptureListener(null, getRate(), false, false);
        player.release();
        visualizer.release();
        textFilename.setText(getResources().getString(R.string.utils07));
        fabPlayPause.setImageResource(playIcon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        visualizer = null;
        player = null;
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            player.pause();
        }
        try {
            visualizer.setEnabled(false);
        }
        catch (IllegalStateException e) {
            LogDebug.log("onStop --> "+e.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        player = MediaPlayer.create(this, FileUtils.seekUriFromFilename(filename));
        visualizer = new Visualizer(player.getAudioSessionId());
        visualizer.setCaptureSize(fftSize);
        textFreqMax.setText(StringUtils.returnStringifiedSamplingRate(visualizer.getSamplingRate()/4));
        textFreqMed.setText(StringUtils.returnStringifiedSamplingRate(visualizer.getSamplingRate()/8));
        TextView textSampling = findViewById(R.id.text_sampling);
        textSampling.setText(StringUtils.returnStringifiedSamplingRate(visualizer.getSamplingRate()));

        if (visualizeWaveform && visualizeFft) {
            visualizer.setDataCaptureListener(new PlayerOnDataCaptureListener(visualizerView1, visualizerView2, visualizerView3), getRate(), visualizeWaveform, visualizeFft);
        }
        else if (visualizeWaveform) {
            visualizer.setDataCaptureListener(new PlayerOnDataCaptureListener(visualizerView1), getRate(), visualizeWaveform, visualizeFft);
        }
        else if (visualizeFft) {
            visualizer.setDataCaptureListener(new PlayerOnDataCaptureListener(visualizerView2, visualizerView3), getRate(), visualizeWaveform, visualizeFft);
        }

        visualizer.setEnabled(true);
        textFormat.setText("/"+stringExtension);
        fabPlayPause.setImageResource(playIcon);
        textFilename.setText(getResources().getString(R.string.utils07));
        textDuration.setText(stringDuration);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                textFilename.setText(getResources().getString(R.string.utils03));
                fabPlayPause.setImageResource(playIcon);
            }
        });
        fabPlayPause.setOnClickListener(new PlayPauseOnClickListener(player, visualizer,  textFilename, fabPlayPause, playIcon, pauseIcon, filename));
        fabPrevious.setOnClickListener(new PreviousOnClickListener(this, player, textFilename, Integer.parseInt(stringPosition), playIcon));
        fabNext.setOnClickListener(new NextOnClickListener(this, player, textFilename, Integer.parseInt(stringPosition), playIcon));
        if (firstStart) {
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                LogDebug.log("PlayerActivity: onResume --> Unable to wait 500 ms due to "+e.getMessage());
            }
            fabPlayPause.performClick();
        }
        firstStart = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_player_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Toast.makeText(this, getResources().getString(R.string.player_activity01), Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //AUXILIARY METHODS
    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRate() {
        return this.rate;
    }

    public void setFilename(String filename) { this.filename = filename; }

    public void setStringPosition(String position) { this.stringPosition = position; }

    public void setStringExtension(String extension) { this.stringExtension = extension; }

    public void setStringDuration(String duration) { this.stringDuration = duration; }
}


