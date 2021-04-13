package it.lemmed.audiospectrum.player;

import com.jjoe64.graphview.GraphView;

public abstract class GraphViewWrapperForViewportResettingOnDoubleTap {
    //FIELDS
    protected GraphView graph;
    protected int captureSize;
    protected int samplingRate;
    protected PlotType plotType;

    //CONSTRUCTORS
    public GraphViewWrapperForViewportResettingOnDoubleTap(GraphView graph, int captureSize, int samplingRate, PlotType plotType) {
        this.graph = graph;
        this.captureSize = captureSize;
        this.samplingRate = samplingRate;
        this.plotType = plotType;
    }

    //METHODS
    public void setDoubleTapListenerForResettingViewport() {
        graph.setOnTouchListener(new GraphViewOnTouchListener(this));
    }

    public void setGridFormatter() {
        this.plotType.formatGrid(this.graph, this.captureSize, this.samplingRate);
    }

    public abstract void reset();
}
