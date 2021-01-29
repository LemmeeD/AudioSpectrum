package it.lemmed.audiospectrum.player;

import android.view.ScaleGestureDetector;
import android.view.View;

//INNER CLASSES
public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    //FIELDS
    private final View v;
    private float scale;

    //CONSTRUCTORS
    public ScaleListener(View v, float scale) {
        this.v = v;
        this.scale = scale;
    }

    //METHODS
    // when a scale gesture is detected, use it to resize the image
    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector){
        this.scale *= scaleGestureDetector.getScaleFactor();
        this.v.setScaleX(this.scale);
        this.v.setScaleY(this.scale);
        return false;
    }
}
