package it.lemmed.audiospectrum.utils;

import android.annotation.SuppressLint;

import com.jjoe64.graphview.GraphView;

import it.lemmed.audiospectrum.player.GraphViewWrapperForGridResettingOnDoubleTap;
import it.lemmed.audiospectrum.player.PlotType;

public class GraphViewUtils {
    //METHODS
    @SuppressLint("ClickableViewAccessibility")
    public static void init(GraphView graph, PlotType plotType, int captureSize, int samplingRate) {
        GraphViewWrapperForGridResettingOnDoubleTap wrapper = new GraphViewWrapperForGridResettingOnDoubleTap(graph, captureSize, samplingRate, plotType) {
            @Override
            public void reset() {
                this.plotType.resetViewport(this.graph.getViewport(), this.captureSize, this.samplingRate);
            }
        };
        wrapper.reset();
        wrapper.setGridFormatter();
        wrapper.setDoubleTapListenerForResettingViewport();
    }
}
