package it.lemmed.audiospectrum.player;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.jjoe64.graphview.GraphView;

public class OnTouchListenerFftMagnitude implements View.OnTouchListener {
    //FIELDS
    protected GraphView graph;
    protected GestureDetector doubleTapListener;

    //CONSTRUCTORS
    public OnTouchListenerFftMagnitude(GraphView graph, int captureSize, Context context) {
        this.graph = graph;
        this.doubleTapListener = new GestureDetector(context, new ResetDoubleTapListenerFftMagnitude(graph, captureSize));
    }

    //METHODS
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);      //questo mantiene gli altri onTouchEvent se si settano scroll e zoom del GraphView
        this.doubleTapListener.onTouchEvent(event);
        return true;
    }
}

