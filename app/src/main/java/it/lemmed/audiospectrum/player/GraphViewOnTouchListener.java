package it.lemmed.audiospectrum.player;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GraphViewOnTouchListener implements View.OnTouchListener {
    //FIELDS
    protected GraphViewWrapperForGridResettingOnDoubleTap wrapper;
    protected GestureDetector doubleTapListener;

    //CONSTRUCTORS
    public GraphViewOnTouchListener(GraphViewWrapperForGridResettingOnDoubleTap wrapper) {
        this.wrapper = wrapper;
        this.doubleTapListener = new GestureDetector(wrapper.graph.getContext(), new ResetDoubleTapListener(wrapper));
    }

    //METHODS
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);      //questo mantiene gli altri onTouchEvent se si settano scroll e zoom del GraphView
        this.doubleTapListener.onTouchEvent(event);
        return true;
    }
}
