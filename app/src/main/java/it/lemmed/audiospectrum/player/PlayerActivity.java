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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import it.lemmed.audiospectrum.utils.NumberUtils;
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
    protected SeekBar progressBar;
    protected LinearLayout layoutContainer;
    protected ConstraintLayout layoutControls;
    protected int currentPosition;
    //Views for live plots
    protected GraphView graph1; //waveform
    protected GraphView graph2; //fft 1
    protected GraphView graph3; //fft 2
    //series of data to be plotted
    protected BaseSeries<DataPoint> series1;    //waveform
    protected BaseSeries<DataPoint> series2;    //fft 1
    protected BaseSeries<DataPoint> series3;    //fft 2
    //static icons for buttons
    protected static int playIcon = R.drawable.ic_baseline_play_arrow_24;
    protected static int pauseIcon = R.drawable.ic_baseline_pause_24;
    //infos
    protected int rate;
    protected int samplingRate; //of the audio file
    protected boolean fftConf;  //true: fft will be displayed as magnitdue/phase, false: fft will be displayed as real part/imaginary part
    protected float strokeWidth;
    protected int fftSize;  //set the captureSize of the Visualizer object
    //handling progress bar
    private Handler handler = new Handler();


    //CONSTRUCTORS

    //METHODS
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogDebug.log("PlayerActivity: onCreate()");
        this.initCreate(-1);

        /*
        String id = "666666";
        NotificationChannel nc = new NotificationChannel(id, "Bar player notification", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(nc);
        Notification notification = new Notification.Builder(this, id)
                .setSmallIcon(R.drawable.ic_baseline_play_circle_6)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build();


        this.tempIntent = initServiceMediaPlayer(this, this.filename);
         */
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogDebug.log("PlayerActivity: onStart()");
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        LogDebug.log("PlayerActivity: onResume()");
        this.initResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogDebug.log("PlayerActivity: onPause()");
        if (this.player.isPlaying()) {
            this.currentPosition = this.player.getCurrentPosition();
            this.player.pause();
        }
        try {
            this.visualizer.setEnabled(false);
        }
        catch (IllegalStateException e) {
            LogDebug.log("onStop --> "+e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogDebug.log("PlayerActivity: onStop()");
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
        LogDebug.log("PlayerActivity: onDestroy()");
        this.player = null;
        this.deallocateGraphicBindings();
        this.finish();
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
            this.deallocateAllBindings();
            this.initCreate(this.samplingRate);
            this.reInitResume();
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.deallocateAllBindings();
            this.initCreate(this.samplingRate);
            this.reInitResume();
        }
    }

    //AUXILIARY METHODS
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initCreate(int sampleRate) {       //int argument not very elegant..
        LogDebug.log("PlayerActivity: initCreate()");
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_player);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Bindings
        this.fabPlayPause = findViewById(R.id.fab_play_pause);
        this.fabPrevious = findViewById(R.id.fab_previous);
        this.fabNext = findViewById(R.id.fab_next);
        //PRESSED state has more 'priority' than ENABLED, so if you define a color for NOT PRESSED, the color you defined for ENABLED will never be shown.. Can be done with a XML selector.
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed },
                //new int[] { -android.R.attr.state_pressed },
                new int[] { android.R.attr.state_enabled },
                new int[] { -android.R.attr.state_enabled }
        };
        int[] colors = new int[] {
                this.getResources().getColor(R.color.verdeAcqua),
                //this.getResources().getColor(R.color.arancione),
                this.getResources().getColor(R.color.arancione),
                this.getResources().getColor(R.color.arancioneScuro)
        };
        this.fabPlayPause.setBackgroundTintList(new ColorStateList(states, colors));
        this.fabPrevious.setBackgroundTintList(new ColorStateList(states, colors));
        this.fabNext.setBackgroundTintList(new ColorStateList(states, colors));
        this.textFilename = findViewById(R.id.text_filename);
        this.textDuration = findViewById(R.id.text_duration);
        this.textFormat = findViewById(R.id.text_format);
        this.layoutContainer = findViewById(R.id.layout_container);
        this.layoutControls = findViewById(R.id.layout_controls);
        this.textSampling = findViewById(R.id.text_sampling);
        this.progressBar = findViewById(R.id.progress_bar);
        this.currentPosition = -1;
        //Getting info from preferences
        if ((this.filename == null) && (this.stringDuration == null) && (this.stringExtension == null) && (this.stringPosition == null)) {
            //first start of the activity
            this.firstStart = true;
            this.filename = getIntent().getStringExtra("nome_file");
            this.stringPosition = getIntent().getStringExtra("position");
            this.stringExtension = getIntent().getStringExtra("extension");
            this.stringDuration = getIntent().getStringExtra("duration");
        }
        else {
            LogDebug.log("TEEEEEEEEST");
            this.firstStart = false;
            //means that the configuration was changed and to reset everything we don't want to use the information of the intent which was the initial song that was meant to be played,
            //because maybe the next/previous button was pressed. So we want the next/previous if it was selected. In that case the 4 variable are set externally through the setter methods of this activity
        }
        //Settings
        SharedPreferences preferences = this.getSharedPreferences(SettingsFragment.SETTINGS_SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        //this.strokeWidth = Float.parseFloat(preferences.getString("key_stroke_width", "2.0"));
        int colorWaveform = Integer.parseInt(preferences.getString("key_color_waveform", "-16776961"));
        int colorFft2 = Integer.parseInt(preferences.getString("key_color_fft1", "-65536"));
        int colorFft3 = Integer.parseInt(preferences.getString("key_color_fft2", "-19456"));
        LogDebug.log("TEST: SeekBarPreference = "+preferences.getInt("test", 2)+", so after conversion is = "+NumberUtils.returnStrokeWidth(preferences.getInt("test", 2)));
        this.strokeWidth = NumberUtils.returnStrokeWidth(preferences.getInt("test", 2));
        this.visualizeWaveform = preferences.getBoolean("key_visualization_waveform", true);
        this.visualizeFft = preferences.getBoolean("key_visualization_fft", true);
        this.fftConf = preferences.getBoolean("key_fft_graphic", true);
        this.fftSize = Integer.parseInt(preferences.getString("key_capture_size", "1024"));
        this.rate = Integer.parseInt(preferences.getString("key_visualization_rate", "10000"));
        //TEMPORARY SOLUTION: allocating here a temporary player and visualizer to get sampling rate.. To be visualized after in the TextView. NOT VERY ELEGANT because allocating this objects is done in the onResume() method...
        //But cannot get sampling rate from MediaPlayer object... Visualizer is needed
        if (sampleRate <= 0) {
            //first call of method
            MediaPlayer tempPlayer = MediaPlayer.create(this, FileUtils.seekUriFromFilename(filename));
            Visualizer tempVisualizer = new Visualizer(tempPlayer.getAudioSessionId());
            this.samplingRate = tempVisualizer.getSamplingRate();
            tempPlayer.release();
            tempPlayer = null;
            tempVisualizer.release();
            tempVisualizer = null;
        }
        else {
            //a valid int argument means this call of initCreate() is used after the creation of the Activity, which it only means that the configuration has changed ==> LANDSCAPE or PORTRAIT
            this.samplingRate = sampleRate;
        }
        //Handling plots on screen
        if (this.visualizeWaveform) {
            /*
            //Waveform graph
            this.graph1 = findViewById(R.id.graph_1);
            GraphViewUtils.initWaveformGraph(this.graph1, this.fftSize, this.samplingRate);
            this.series1 = new LineGraphSeries<>();
            //style
            this.series1.setColor(colorWaveform);
            ((LineGraphSeries<DataPoint>) this.series1).setThickness((int) this.strokeWidth);
            //binding
            graph1.addSeries(series1);
             */

            //Waveform graph
            this.graph1 = findViewById(R.id.graph_1);
            GraphViewUtils.init(this.graph1, PlotType.WAVEFORM, this.fftSize, this.samplingRate);
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
                GraphViewUtils.init(this.graph2, PlotType.FFT_MAGNITUDE, this.fftSize, this.samplingRate);
                GraphViewUtils.init(this.graph3, PlotType.FFT_PHASE, this.fftSize, this.samplingRate);
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
                GraphViewUtils.init(this.graph2, PlotType.FFT_REAL, this.fftSize, this.samplingRate);
                GraphViewUtils.init(this.graph3, PlotType.FFT_IMAGINARY, this.fftSize, this.samplingRate);
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
        LogDebug.log("PlayerActivity: initResume()");
        //Handling playback
        this.player = MediaPlayer.create(this, FileUtils.seekUriFromFilename(this.filename));
        if ( !(this.currentPosition < 0) ) {
            this.player.seekTo(this.currentPosition);
        }
        this.visualizer = new Visualizer(this.player.getAudioSessionId());
        this.visualizer.setCaptureSize(this.fftSize);
        /*
            Here the implementation of a Visualizer.OnDataCaptureListener object takes care of managing the byte[] given from the internal methods and draws everything on the views passed as arguments.
            Everything is done at a rate set by settings (max 20000 ms), which is a property of the Visualizer object. Rendering's job is not handled in different threads...
            ---> temporary solution?
            So the Visualizer object at a certain rate gives in the Visualizer.OnDataCaptureListener's object methods the waveform and the fft byte[] which length is set according to the captureSize variable here,
            which it is also a property of the Visualizer object.
        */
        this.visualizer.setDataCaptureListener(new PlayerOnDataCaptureListener(this.graph1, this.graph2, this.graph3, this.series1, this.series2, this.series3, this.fftConf), this.rate, this.visualizeWaveform, this.visualizeFft);
        this.samplingRate = this.visualizer.getSamplingRate();  //again
        //setting TextView on screen for audio file infos
        this.textSampling.setText(StringUtils.returnStringifiedSamplingRate(this.visualizer.getSamplingRate()));
        this.textFormat.setText("/"+this.stringExtension);
        this.visualizer.setEnabled(true);
        //setting button img
        this.fabPlayPause.setImageResource(playIcon);
        this.textFilename.setText(getResources().getString(R.string.utils07));
        this.textDuration.setText(this.stringDuration);
        //set progress bar max
        this.progressBar.setMax(this.player.getDuration());
        //listener if the song finishes
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                PlayerActivity.this.textFilename.setText(getResources().getString(R.string.utils03));
                PlayerActivity.this.fabPlayPause.setImageResource(playIcon);
                PlayerActivity.this.currentPosition = -1;
            }
        });
        //listeners on buttons
        this.fabPlayPause.setOnClickListener(new PlayPauseOnClickListener(this.player, this.visualizer,  this.textFilename, this.fabPlayPause, playIcon, pauseIcon, this.filename));
        this.fabPrevious.setOnClickListener(new PreviousOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        this.fabNext.setOnClickListener(new NextOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        //progress bar
        this.runOnUiThread(new Runnable() {
            //FIELDS
            protected final int period = 500;     //[ms]
            //CONSTRUCTORS
            //METHODS
            @Override
            public void run() {
                if(PlayerActivity.this.player != null){
                    int currentPosition = PlayerActivity.this.player.getCurrentPosition();
                    PlayerActivity.this.progressBar.setProgress(currentPosition);
                }
                PlayerActivity.this.handler.postDelayed(this, period);        //update every 1000 ms = 1 s
            }
        });
        this.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //FIELDS
            protected boolean wasPlaying = false;
            //CONSTRUCTORS

            //METHODS
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerActivity.this.fabPlayPause.setEnabled(true);
                PlayerActivity.this.fabPrevious.setEnabled(true);
                PlayerActivity.this.fabNext.setEnabled(true);
                if (wasPlaying) {
                    PlayerActivity.this.player.start();
                    PlayerActivity.this.visualizer.setEnabled(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayerActivity.this.fabPlayPause.setEnabled(false);
                PlayerActivity.this.fabPrevious.setEnabled(false);
                PlayerActivity.this.fabNext.setEnabled(false);
                if (PlayerActivity.this.player.isPlaying()) {
                    this.wasPlaying = true;
                    PlayerActivity.this.player.pause();
                    PlayerActivity.this.visualizer.setEnabled(false);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if ((PlayerActivity.this.player != null) && (fromUser)) {
                    PlayerActivity.this.player.seekTo(progress);
                }
            }
        });
        //firstStart?
        if (this.firstStart) {
            this.fabPlayPause.performClick();
        }
        this.firstStart = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reInitResume() {
        LogDebug.log("PlayerActivity: reInitResume()");
        //slightly different from initResume()... Without comments
        this.progressBar.setProgress(this.player.getCurrentPosition());
        this.visualizer = new Visualizer(this.player.getAudioSessionId());
        this.visualizer.setCaptureSize(this.fftSize);
        this.visualizer.setDataCaptureListener(new PlayerOnDataCaptureListener(this.graph1, this.graph2, this.graph3, this.series1, this.series2, this.series3, this.fftConf), this.rate, this.visualizeWaveform, this.visualizeFft);
        this.samplingRate = this.visualizer.getSamplingRate();
        this.textSampling.setText(StringUtils.returnStringifiedSamplingRate(this.samplingRate));
        this.textFormat.setText("/"+this.stringExtension);
        this.visualizer.setEnabled(true);
        if (this.player.isPlaying()) {
            this.fabPlayPause.setImageResource(pauseIcon);
            this.textFilename.setText(getResources().getString(R.string.utils01));
        }
        else {
            this.fabPlayPause.setImageResource(playIcon);
            this.textFilename.setText(getResources().getString(R.string.utils07));
        }
        this.textDuration.setText(stringDuration);
        this.progressBar.setMax(this.player.getDuration());
        this.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                PlayerActivity.this.textFilename.setText(getResources().getString(R.string.utils03));
                PlayerActivity.this.fabPlayPause.setImageResource(playIcon);
                PlayerActivity.this.currentPosition = -1;
            }
        });
        this.fabPlayPause.setOnClickListener(new PlayPauseOnClickListener(this.player, this.visualizer,  this.textFilename, this.fabPlayPause, playIcon, pauseIcon, this.filename));
        this.fabPrevious.setOnClickListener(new PreviousOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        this.fabNext.setOnClickListener(new NextOnClickListener(this, this.player, this.textFilename, Integer.parseInt(this.stringPosition), playIcon));
        this.runOnUiThread(new Runnable() {
            protected final int period = 500;

            @Override
            public void run() {
                if(PlayerActivity.this.player != null){
                    int currentPosition = PlayerActivity.this.player.getCurrentPosition();
                    PlayerActivity.this.progressBar.setProgress(currentPosition);
                }
                PlayerActivity.this.handler.postDelayed(this, period);
            }
        });
        this.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            protected boolean wasPlaying = false;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerActivity.this.fabPlayPause.setEnabled(true);
                PlayerActivity.this.fabPrevious.setEnabled(true);
                PlayerActivity.this.fabNext.setEnabled(true);
                if (wasPlaying) {
                    PlayerActivity.this.player.start();
                    PlayerActivity.this.visualizer.setEnabled(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayerActivity.this.fabPlayPause.setEnabled(false);
                PlayerActivity.this.fabPrevious.setEnabled(false);
                PlayerActivity.this.fabNext.setEnabled(false);
                if (PlayerActivity.this.player.isPlaying()) {
                    this.wasPlaying = true;
                    PlayerActivity.this.player.pause();
                    PlayerActivity.this.visualizer.setEnabled(false);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if ((PlayerActivity.this.player != null) && (fromUser)) {
                    PlayerActivity.this.player.seekTo(progress);
                }
            }
        });
        this.firstStart = false;
    }

    public void setFilename(String filename) { this.filename = filename; }

    public void setStringPosition(String position) { this.stringPosition = position; }

    public void setStringExtension(String extension) { this.stringExtension = extension; }

    public void setStringDuration(String duration) { this.stringDuration = duration; }

    protected void deallocateAllBindings() {
        this.visualizer.setEnabled(false);
        this.visualizer.setDataCaptureListener(null, this.rate, false, false);
        this.visualizer.release();
        this.progressBar.setOnSeekBarChangeListener(null);
        this.deallocateGraphicBindings();
    }

    protected void deallocateGraphicBindings() {
        this.graph1 = null;
        this.graph2 = null;
        this.graph3 = null;
        this.fabPlayPause = null;
        this.fabPrevious = null;
        this.fabNext = null;
        this.textFilename = null;
        this.textDuration = null;
        this.textFormat = null;
        this.layoutContainer = null;
        this.layoutControls = null;
        this.textSampling = null;
    }
}
