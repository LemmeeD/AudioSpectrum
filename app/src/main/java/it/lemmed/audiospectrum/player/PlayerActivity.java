package it.lemmed.audiospectrum.player;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BaseSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.R;
import it.lemmed.audiospectrum.settings.SettingsFragment;
import it.lemmed.audiospectrum.utils.FileUtils;
import it.lemmed.audiospectrum.utils.StringUtils;
import it.lemmed.audiospectrum.utils.GraphViewUtils;

public class PlayerActivity extends AppCompatActivity {
    //FIELDS
    protected MediaPlayer player;
    protected Visualizer visualizer;
    protected boolean firstStart;   //if true, the audio file will be started automatically
    //audio file infos
    protected String filename;
    protected String stringPosition;
    protected String stringExtension;
    protected String stringDuration;
    //boolean for understanding what to draw (from settings)
    protected boolean visualizeWaveform;    //true: graph1 will show waveform, false: graph1 will show nothing
    protected boolean visualizeFft;    //true: graph2 will show fft magnitude or real part, false: graph3 will show fft phase or imaginary part
    //buttons
    protected FloatingActionButton fabPlayPause;
    protected FloatingActionButton fabPrevious;
    protected FloatingActionButton fabNext;
    //infos displayed on screen
    protected TextView textFormat;
    protected TextView textFilename;
    protected TextView textDuration;
    protected TextView textSampling;
    protected LinearLayout layoutContainer;
    protected ConstraintLayout layoutControls;
    //Views for live plots
    protected GraphView graph1; //waveform
    protected GraphView graph2; //fft 1
    protected GraphView graph3; //fft 2
    //series of data to be plotted
    protected BaseSeries<DataPoint> series1;    //waveform
    protected BaseSeries<DataPoint> series2;    //fft 1
    protected BaseSeries<DataPoint> series3;    //fft 2
    protected static int playIcon = R.drawable.ic_baseline_play_arrow_24;
    protected static int pauseIcon = R.drawable.ic_baseline_pause_24;
    protected int rate;
    protected int samplingRate; //of the audio file
    //protected boolean graphic;  //DEPRECATED. Boolean to set if views should plot points or lines... Feature almost completely removed
    protected boolean fftConf;  //true: fft will be displayed as magnitdue/phase, false: fft will be displayed as real part/imaginary part
    protected float strokeWidth;
    protected int fftSize;  //set the captureSize of the Visualizer object

    //CONSTRUCTORS

    //METHODS
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            this.player.stop();
        }
        catch (IllegalStateException e) {
            LogDebug.log(e.getMessage());
            LogDebug.log("PlayerActivity: onStop --> "+e.getMessage());
        }
        catch (NullPointerException e) {
            LogDebug.log(e.getMessage());
            LogDebug.log("PlayerActivity: onStop --> "+e.getMessage());
        }
        this.visualizer.setDataCaptureListener(null, this.rate, false, false);
        this.player.release();
        this.visualizer.release();
        this.textFilename.setText(getResources().getString(R.string.utils07));
        this.fabPlayPause.setImageResource(playIcon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.visualizer = null;
        this.player = null;
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.player.isPlaying()) {
            this.player.pause();
        }
        try {
            this.visualizer.setEnabled(false);
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
        this.initResume();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (this.player.isPlaying()) {
                this.visualizer.setEnabled(false);
                this.visualizer.setDataCaptureListener(null, this.rate, false, false);
                this.visualizer.release();
                int currentPosition = this.player.getCurrentPosition();
                this.player.pause();
                this.player.stop();
                this.player.release();
                this.initCreate();
                this.reInitResume(currentPosition);
            }
            else {
                this.visualizer.setEnabled(false);
                this.visualizer.setDataCaptureListener(null, this.rate, false, false);
                this.player.stop();
                this.visualizer.release();
                int currentPosition = this.player.getCurrentPosition();
                this.player.release();
                this.initCreate();
                this.reInitResume(currentPosition);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (this.player.isPlaying()) {
                this.visualizer.setEnabled(false);
                this.visualizer.setDataCaptureListener(null, this.rate, false, false);
                this.player.pause();
                this.player.stop();
                this.visualizer.release();
                int currentPosition = this.player.getCurrentPosition();
                this.player.pause();
                this.player.release();
                this.initCreate();
                this.reInitResume(currentPosition);
            }
            else {
                this.visualizer.setEnabled(false);
                this.visualizer.setDataCaptureListener(null, this.rate, false, false);
                this.player.stop();
                this.visualizer.release();
                int currentPosition = this.player.getCurrentPosition();
                this.player.release();
                this.initCreate();
                this.reInitResume(currentPosition);
            }
        }
    }

    //AUXILIARY METHODS
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initCreate() {
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        this.firstStart = true;
        this.fabPlayPause = findViewById(R.id.fab_play_pause);
        this.fabPrevious = findViewById(R.id.fab_previous);
        this.fabNext = findViewById(R.id.fab_next);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
                new int[] { -android.R.attr.state_pressed }
        };
        int[] colors = new int[] {
                this.getResources().getColor(R.color.verdeAcqua),
                this.getResources().getColor(R.color.arancione)
        };
        this.fabPlayPause.setBackgroundTintList(new ColorStateList(states, colors));
        this.fabPrevious.setBackgroundTintList(new ColorStateList(states, colors));
        this.fabNext.setBackgroundTintList(new ColorStateList(states, colors));
        this.textFilename = findViewById(R.id.text_filename);
        this.textDuration = findViewById(R.id.text_duration);
        this.textFormat = findViewById(R.id.text_format);
        this.filename = getIntent().getStringExtra("nome_file");
        this.stringPosition = getIntent().getStringExtra("position");
        this.stringExtension = getIntent().getStringExtra("extension");
        this.stringDuration = getIntent().getStringExtra("duration");
        this.layoutContainer = findViewById(R.id.layout_container);
        this.layoutControls = findViewById(R.id.layout_controls);
        this.textSampling = findViewById(R.id.text_sampling);
        //Settings
        SharedPreferences preferences = this.getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        this.strokeWidth = Float.parseFloat(preferences.getString("key_stroke_width", "2.0"));
        int colorWaveform = Integer.parseInt(preferences.getString("key_color_waveform", "-16776961"));
        int colorFft2 = Integer.parseInt(preferences.getString("key_color_fft1", "-65536"));
        int colorFft3 = Integer.parseInt(preferences.getString("key_color_fft2", "-19456"));
        this.visualizeWaveform = preferences.getBoolean("key_visualization_waveform", true);
        this.visualizeFft = preferences.getBoolean("key_visualization_fft", true);
        this.fftConf = preferences.getBoolean("key_fft_graphic", true);
        this.fftSize = Integer.parseInt(preferences.getString("key_capture_size", "1024"));
        this.rate = Integer.parseInt(preferences.getString("key_visualization_rate", "10000"));
        //TEMPORARY SOLUTION: allocating here player and visualizer to get sampling rate.. To be visualized after in the TextView. NOT VERY ELEGANT because allocating this objects is done in the onResume() method...
        //But cannot get sampling rate from MediaPlayer object...
        this.player = MediaPlayer.create(this, FileUtils.seekUriFromFilename(filename));
        this.visualizer = new Visualizer(player.getAudioSessionId());
        this.samplingRate = visualizer.getSamplingRate();
        //Handling plots on screen
        if (this.visualizeWaveform) {
            //Waveform graph
            this.graph1 = findViewById(R.id.graph_1);
            GraphViewUtils.initWaveformGraph(this.graph1, this.fftSize, this.samplingRate);
            this.series1 = new LineGraphSeries<>();
            //style
            this.series1.setColor(colorWaveform);
            ((LineGraphSeries<DataPoint>) this.series1).setThickness((int) this.strokeWidth);
            //binding
            graph1.addSeries(series1);
        }
        else {
            this.graph1 = findViewById(R.id.graph_1);
            this.graph1.setVisibility(View.INVISIBLE);
        }
        if (this.visualizeFft) {
            if (this.fftConf) {
                //Magnitude/Phase
                this.graph2 = findViewById(R.id.graph_2);
                this.graph3 = findViewById(R.id.graph_3);
                GraphViewUtils.initFftMagnitudeGraph(this.graph2, this.fftSize, this.samplingRate);
                GraphViewUtils.initFftPhaseGraph(this.graph3, this.fftSize, this.samplingRate);
                this.series2 = new LineGraphSeries<>();
                this.series3 = new LineGraphSeries<>();
                //styles
                this.series2.setColor(colorFft2);
                this.series3.setColor(colorFft3);
                ((LineGraphSeries<DataPoint>) this.series2).setThickness((int) this.strokeWidth);
                ((LineGraphSeries<DataPoint>) this.series3).setThickness((int) this.strokeWidth);
                //bindings
                this.graph2.addSeries(this.series2);
                this.graph3.addSeries(this.series3);
            }
            else {
                //Real/Imaginary parts
                this.graph2 = findViewById(R.id.graph_2);
                this.graph3 = findViewById(R.id.graph_3);
                GraphViewUtils.initFftRealGraph(this.graph2, this.fftSize, this.samplingRate);
                GraphViewUtils.initFftImagGraph(this.graph3, this.fftSize, this.samplingRate);
                this.series2 = new LineGraphSeries<>();
                this.series3 = new LineGraphSeries<>();
                //styles
                this.series2.setColor(colorFft2);
                this.series3.setColor(colorFft3);
                ((LineGraphSeries<DataPoint>) this.series2).setThickness((int) this.strokeWidth);
                ((LineGraphSeries<DataPoint>) this.series3).setThickness((int) this.strokeWidth);
                //bindings
                this.graph2.addSeries(this.series2);
                this.graph3.addSeries(this.series3);
            }
        }
        else {
            this.graph2 = findViewById(R.id.graph_2);
            this.graph3 = findViewById(R.id.graph_3);
            this.graph2.setVisibility(View.INVISIBLE);
            this.graph3.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initResume() {
        //Handling playback
        this.player = MediaPlayer.create(this, FileUtils.seekUriFromFilename(this.filename));
        this.visualizer = new Visualizer(this.player.getAudioSessionId());
        this.visualizer.setCaptureSize(this.fftSize);
        /*
            Here the implementation of a Visualizer.OnDataCaptureListener object takes care of managing the byte[] given from the internal methods and draws everything on the views passed as arguments.
            Everything is done at a rate set by settings (max 20000 ms), which is a property of the Visualizer object. Rendering's job is not handle in different threads...
            ---> temporary solution?
            So the Visualizer object at a certain rate gives in the Visualizer.OnDataCaptureListener's object methods the waveform and the fft byte[] which are long according to the captureSize.
        */
        this.visualizer.setDataCaptureListener(new PlayerOnDataCaptureListener(this.graph1, this.graph2, this.graph3, this.series1, this.series2, this.series3, this.fftConf), this.rate, this.visualizeWaveform, this.visualizeFft);
        this.samplingRate = this.visualizer.getSamplingRate();  //again
        //setting TextView on screen for audio file infos
        this.textSampling.setText(StringUtils.returnStringifiedSamplingRate(this.visualizer.getSamplingRate()));
        this.textFormat.setText("/"+stringExtension);
        this.visualizer.setEnabled(true);
        //setting button img
        this.fabPlayPause.setImageResource(playIcon);
        this.textFilename.setText(getResources().getString(R.string.utils07));
        this.textDuration.setText(stringDuration);
        //listener if the song finishes
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                PlayerActivity.this.textFilename.setText(getResources().getString(R.string.utils03));
                PlayerActivity.this.fabPlayPause.setImageResource(playIcon);
            }
        });
        //listeners on buttons
        this.fabPlayPause.setOnClickListener(new PlayPauseOnClickListener(this.player, this.visualizer,  this.textFilename, this.fabPlayPause, playIcon, pauseIcon, this.filename));
        this.fabPrevious.setOnClickListener(new PreviousOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        this.fabNext.setOnClickListener(new NextOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        //firstStart?
        if (this.firstStart) {
            try {
                Thread.sleep(750);  //...
            } catch (InterruptedException e) {
                LogDebug.log("PlayerActivity: onResume --> Unable to wait 500 ms due to "+e.getMessage());
            }
            this.fabPlayPause.performClick();
        }
        this.firstStart = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reInitResume(int currentPosition) {
        //slightly different from initResume()...
        player = MediaPlayer.create(this, FileUtils.seekUriFromFilename(filename));
        player.seekTo(currentPosition);
        this.visualizer = new Visualizer(this.player.getAudioSessionId());
        this.visualizer.setCaptureSize(this.fftSize);
        this.visualizer.setDataCaptureListener(new PlayerOnDataCaptureListener(this.graph1, this.graph2, this.graph3, this.series1, this.series2, this.series3, this.fftConf), this.rate, this.visualizeWaveform, this.visualizeFft);
        this.samplingRate = this.visualizer.getSamplingRate();
        this.textSampling.setText(StringUtils.returnStringifiedSamplingRate(this.visualizer.getSamplingRate()));
        this.textFormat.setText("/"+stringExtension);
        this.visualizer.setEnabled(true);
        this.fabPlayPause.setImageResource(playIcon);
        this.textFilename.setText(getResources().getString(R.string.utils07));
        this.textDuration.setText(stringDuration);
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                PlayerActivity.this.textFilename.setText(getResources().getString(R.string.utils03));
                PlayerActivity.this.fabPlayPause.setImageResource(playIcon);
            }
        });
        this.fabPlayPause.setOnClickListener(new PlayPauseOnClickListener(this.player, this.visualizer,  this.textFilename, this.fabPlayPause, playIcon, pauseIcon, this.filename));
        this.fabPrevious.setOnClickListener(new PreviousOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        this.fabNext.setOnClickListener(new NextOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        this.firstStart = false;
        this.fabPlayPause.performClick();
    }

    public void setFilename(String filename) { this.filename = filename; }

    public void setStringPosition(String position) { this.stringPosition = position; }

    public void setStringExtension(String extension) { this.stringExtension = extension; }

    public void setStringDuration(String duration) { this.stringDuration = duration; }

}


