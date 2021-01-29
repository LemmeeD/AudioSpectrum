package it.lemmed.audiospectrum.player;

import android.media.MediaPlayer;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import java.util.List;
import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.audiocollection.AudioCollectionProvider;
import it.lemmed.audiospectrum.audiocollection.RowRecord;
import it.lemmed.audiospectrum.utils.ListUtils;
import it.lemmed.audiospectrum.utils.StringUtils;

public class PreviousOnClickListener implements View.OnClickListener {
    //FIELDS
    protected PlayerActivity activity;
    protected MediaPlayer player;
    protected TextView textFilename;
    protected final int currentPosition;
    protected final int idPlayIcon;

    //CONSTRUCTORS
    public PreviousOnClickListener(PlayerActivity activity, MediaPlayer player, TextView textFilename, int currentPosition, int idPlayIcon) {
        this.activity = activity;
        this.player = player;
        this.textFilename = textFilename;
        this.currentPosition = currentPosition;
        this.idPlayIcon = idPlayIcon;
    }

    //METHODS
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (this.player.isPlaying()) {
            this.activity.fabPlayPause.performClick();
        }
        this.player.stop();
        this.player.release();
        List<RowRecord> records = ListUtils.orderList(AudioCollectionProvider.executeQueryFolder());
        int pos = this.currentPosition;
        RowRecord nextRecord = records.get(pos);
        try {
            nextRecord = records.get(this.currentPosition-1);
            pos = this.currentPosition-1;
        }
        catch (IndexOutOfBoundsException e) {
            LogDebug.log("class PreviousOnClickListener: onClick --> "+e.getMessage());
        }
        this.activity.setFilename(nextRecord.getName() + nextRecord.getExtension());
        this.activity.setStringDuration(StringUtils.returnStringifiedDuration(nextRecord.getDuration()));
        this.activity.setStringExtension(nextRecord.getExtension());
        this.activity.setStringPosition(Integer.toString(pos));
        this.activity.onResume();
        this.activity.fabPlayPause.performClick();
    }
}
