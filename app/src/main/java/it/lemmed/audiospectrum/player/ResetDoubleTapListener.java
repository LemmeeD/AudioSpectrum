package it.lemmed.audiospectrum.player;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class ResetDoubleTapListener extends GestureDetector.SimpleOnGestureListener {
    //FIELDS
    private final GraphViewWrapperForViewportResettingOnDoubleTap wrapper;

    //CONSTRUCTORS
    public ResetDoubleTapListener(GraphViewWrapperForViewportResettingOnDoubleTap wrapper) {
        this.wrapper = wrapper;
    }

    //METHODS
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        this.wrapper.reset();
        return true;
    }
}
