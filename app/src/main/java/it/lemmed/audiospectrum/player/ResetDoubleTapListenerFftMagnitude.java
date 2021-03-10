package it.lemmed.audiospectrum.player;

import android.view.GestureDetector;
import android.view.MotionEvent;
import com.jjoe64.graphview.GraphView;
import it.lemmed.audiospectrum.utils.GraphViewUtils;

//INNER CLASSES
public class ResetDoubleTapListenerFftMagnitude extends GestureDetector.SimpleOnGestureListener {
    //FIELDS
    private final GraphView graph;
    private final int captureSize;

    //CONSTRUCTORS
    public ResetDoubleTapListenerFftMagnitude(GraphView graph, int captureSize) {
        this.graph = graph;
        this.captureSize = captureSize;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        GraphViewUtils.resetFftMagnitudeViewport(graph.getViewport(), this.captureSize);
        return true;
    }

    //METHODS
}
