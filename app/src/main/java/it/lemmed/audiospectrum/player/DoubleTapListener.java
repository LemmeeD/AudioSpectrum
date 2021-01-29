package it.lemmed.audiospectrum.player;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import it.lemmed.audiospectrum.LogDebug;

public class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
    //FIELDS
    private final View v;
    private final int oLeft;
    private final int oTop;
    private final int oRight;
    private final int oBottom;

    //COSTRUCTORS
    public DoubleTapListener(View v, int oLeft, int oTop, int oRight, int oBottom) {
        this.v = v;
        this.oLeft = oLeft;
        this.oTop = oTop;
        this.oRight = oRight;
        this.oBottom = oBottom;

    }

    // when a scale gesture is detected, use it to resize the image
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        LogDebug.log("onDoubleTap");
        this.v.setScaleX(1f);
        this.v.setScaleY(1f);
        this.v.setLeft(oLeft);
        this.v.setTop(oTop);
        this.v.setRight(oRight);
        this.v.setBottom(oBottom);
        return true;
    }
}
