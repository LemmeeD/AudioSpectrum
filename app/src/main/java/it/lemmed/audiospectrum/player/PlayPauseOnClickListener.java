package it.lemmed.audiospectrum.player;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import it.lemmed.audiospectrum.R;

public class PlayPauseOnClickListener implements View.OnClickListener {
    //FIELDS
    private MediaPlayer player;
    private Visualizer visualizer;
    private TextView textFilename;
    private FloatingActionButton fab;
    private final String filename;
    private final int idPlayIcon;
    private final int idPauseIcon;

    //CONSTRUCTORS
    public PlayPauseOnClickListener(MediaPlayer player, Visualizer visualizer, TextView textFilename, FloatingActionButton fab, int idPlayIcon, int idPauseIcon, String filename) {
        this.player = player;
        this.visualizer = visualizer;
        this.textFilename = textFilename;
        this.fab = fab;
        this.idPlayIcon = idPlayIcon;
        this.idPauseIcon = idPauseIcon;
        this.filename = filename;
    }

    //METHODS
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (this.player.isPlaying()) {
            this.visualizer.setEnabled(false);
            this.player.pause();
            this.textFilename.setText(this.fab.getContext().getResources().getString(R.string.utils02));
            this.fab.setImageResource(this.idPlayIcon);
        }
        else {
            player.start();
            this.visualizer.setEnabled(true);
            textFilename.setText(this.fab.getContext().getResources().getString(R.string.utils01)+filename);
            this.fab.setImageResource(this.idPauseIcon);
        }
    }
}