package it.lemmed.audiospectrum.utils;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import it.lemmed.audiospectrum.R;
import it.lemmed.audiospectrum.player.OnTouchListenerFftImag;
import it.lemmed.audiospectrum.player.OnTouchListenerFftMagnitude;
import it.lemmed.audiospectrum.player.OnTouchListenerFftPhase;
import it.lemmed.audiospectrum.player.OnTouchListenerFftReal;
import it.lemmed.audiospectrum.player.OnTouchListenerWaveform;

public class GraphViewUtils {
    //METHODS
    public static void initWaveformGraph(GraphView graph, int captureSize, int samplingRate) {
        resetWaveformViewport(graph.getViewport(), captureSize);
        graph.setOnTouchListener(new OnTouchListenerWaveform(graph, captureSize, graph.getContext()));
        //labels
        graph.setTitleTextSize(27.5f);
        graph.setTitleColor(graph.getContext().getResources().getColor(R.color.arancione));
        graph.setTitle(graph.getContext().getResources().getString(R.string.utils12));
        //labels: axis names
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(25.0f);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(25.0f);
        //labels: values
        graph.getGridLabelRenderer().setTextSize(17.5f);
        graph.getGridLabelRenderer().setVerticalAxisTitle(graph.getContext().getResources().getString(R.string.utils15));
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (value == 0) {
                        //return super.formatLabel(value, isValueX);
                        return "";
                    }
                    else {
                        return "";
                    }
                }
                else {
                    if (value == 0) {
                        return "0";
                    }
                    else if (value == 127) {
                        return "max";
                    }
                    else if (value == -128) {
                        return "min";
                    }
                    else {
                        return "";
                    }
                }
            }
        });
    }

    public static void initFftMagnitudeGraph(GraphView graph, int captureSize, int samplingRate) {
        resetFftMagnitudeViewport(graph.getViewport(), captureSize);
        graph.setOnTouchListener(new OnTouchListenerFftMagnitude(graph, captureSize, graph.getContext()));
        //labels
        graph.setTitleTextSize(27.5f);
        graph.setTitleColor(graph.getContext().getResources().getColor(R.color.arancione));
        graph.setTitle(graph.getContext().getResources().getString(R.string.utils10));
        //labels: axis names
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(25.0f);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(25.0f);
        //labels: values
        graph.getGridLabelRenderer().setTextSize(17.5f);
        graph.getGridLabelRenderer().setVerticalAxisTitle(graph.getContext().getResources().getString(R.string.utils15));
        graph.getGridLabelRenderer().setHorizontalAxisTitle(graph.getContext().getResources().getString(R.string.utils18));

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (value == 0) {
                        return "0 DC";
                    }
                    else if (value == captureSize) {
                        return "Pi";
                    }
                    else {
                        return StringUtils.returnStringifiedFrequency((value*samplingRate) / captureSize);
                    }
                }
                else {
                    if (value == 0) {
                        return "0";
                    }
                    else if (value == 256) {
                        return "max";
                    }
                    else {
                        return "";
                    }
                }
            }
        });

    }

    public static void initFftPhaseGraph(GraphView graph, int captureSize, int samplingRate) {
        resetFftPhaseViewport(graph.getViewport(), captureSize);
        graph.setOnTouchListener(new OnTouchListenerFftPhase(graph, captureSize, graph.getContext()));
        //labels
        graph.setTitleTextSize(27.5f);
        graph.setTitleColor(graph.getContext().getResources().getColor(R.color.arancione));
        graph.setTitle(graph.getContext().getResources().getString(R.string.utils11));
        //labels: axis names
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(25.0f);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(25.0f);
        //labels: values
        graph.getGridLabelRenderer().setTextSize(17.5f);
        graph.getGridLabelRenderer().setVerticalAxisTitle(graph.getContext().getResources().getString(R.string.utils17));
        graph.getGridLabelRenderer().setHorizontalAxisTitle(graph.getContext().getResources().getString(R.string.utils18));
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (value == 0) {
                        return "0 DC";
                    }
                    else if (value == captureSize) {
                        return "Pi";
                    }
                    else {
                        return StringUtils.returnStringifiedFrequency((value*samplingRate) / captureSize);
                    }
                }
                else {
                    if (value == 0) {
                        return "0";
                    }
                    else if (value == -2*Math.PI) {
                        return "2*pi";
                    }
                    else if (value == 2*Math.PI) {
                        return "-2*pi";
                    }
                    else {
                        return "";
                    }
                }
            }
        });
    }

    public static void initFftRealGraph(GraphView graph, int captureSize, int samplingRate) {
        resetFftRealViewport(graph.getViewport(), captureSize);
        graph.setOnTouchListener(new OnTouchListenerFftReal(graph, captureSize, graph.getContext()));
        //labels: title
        graph.setTitleTextSize(27.5f);
        graph.setTitleColor(graph.getContext().getResources().getColor(R.color.arancione));
        graph.setTitle(graph.getContext().getResources().getString(R.string.utils13));
        //labels: axis names
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(25.0f);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(25.0f);
        //labels: values
        graph.getGridLabelRenderer().setTextSize(17.5f);
        graph.getGridLabelRenderer().setVerticalAxisTitle(graph.getContext().getResources().getString(R.string.utils15));
        graph.getGridLabelRenderer().setHorizontalAxisTitle(graph.getContext().getResources().getString(R.string.utils18));
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (value == 0) {
                        return "0 DC";
                    }
                    else if (value == captureSize) {
                        return "Pi";
                    }
                    else {
                        return StringUtils.returnStringifiedFrequency((value*samplingRate) / captureSize);
                    }
                }
                else {
                    if (value == 0) {
                        return "0";
                    }
                    else if (value == -128) {
                        return "min";
                    }
                    else if (value == 127) {
                        return "max";
                    }
                    else {
                        return "";
                    }
                }
            }
        });
    }

    public static void initFftImagGraph(GraphView graph, int captureSize, int samplingRate) {
        resetFftImagViewport(graph.getViewport(), captureSize);
        graph.setOnTouchListener(new OnTouchListenerFftImag(graph, captureSize, graph.getContext()));
        //labels: title
        graph.setTitleTextSize(27.5f);
        graph.setTitleColor(graph.getContext().getResources().getColor(R.color.arancione));
        graph.setTitle(graph.getContext().getResources().getString(R.string.utils14));
        //labels: axis names
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(25.0f);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(25.0f);
        //labels: values
        graph.getGridLabelRenderer().setTextSize(17.5f);
        graph.getGridLabelRenderer().setVerticalAxisTitle(graph.getContext().getResources().getString(R.string.utils15));
        graph.getGridLabelRenderer().setHorizontalAxisTitle(graph.getContext().getResources().getString(R.string.utils18));
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    if (value == 0) {
                        return "0 DC";
                    }
                    else if (value == captureSize) {
                        return "Pi";
                    }
                    else {
                        return StringUtils.returnStringifiedFrequency((value*samplingRate) / captureSize);
                    }
                }
                else {
                    if (value == 0) {
                        return "0";
                    }
                    else if (value == -128) {
                        return "min";
                    }
                    else if (value == 127) {
                        return "max";
                    }
                    else {
                        return "";
                    }
                }
            }
        });
    }

    public static void resetWaveformViewport(Viewport v, int captureSize) {
        //Y
        v.setYAxisBoundsManual(true);
        v.setMinY(-128);
        v.setMaxY(127);
        //X
        v.setXAxisBoundsManual(true);
        v.setMinX(0);
        v.setMaxX(captureSize);
        //zoom & scroll
        v.setScrollable(true); // enables horizontal scrolling
        v.setScrollableY(true); // enables vertical scrolling
        v.setScalable(true); // enables horizontal zooming and scrolling
        v.setScalableY(true); // enables vertical zooming and scrolling
    }

    public static void resetFftMagnitudeViewport(Viewport v, int captureSize) {
        //Y
        v.setYAxisBoundsManual(true);
        v.setMinY(0);
        v.setMaxY(256);
        //X
        v.setXAxisBoundsManual(true);
        v.setMinX(0);
        v.setMaxX(captureSize / 2);
        //zoom & scroll
        v.setScrollable(true); // enables horizontal scrolling
        v.setScrollableY(true); // enables vertical scrolling
        v.setScalable(true); // enables horizontal zooming and scrolling
        v.setScalableY(true); // enables vertical zooming and scrolling
    }

    public static void resetFftPhaseViewport(Viewport v, int captureSize) {
        //Y
        v.setYAxisBoundsManual(true);
        v.setMinY(-2*Math.PI);
        v.setMaxY(2*Math.PI);
        //X
        v.setXAxisBoundsManual(true);
        v.setMinX(0);
        v.setMaxX(captureSize / 2);
        //zoom & scroll
        v.setScrollable(true); // enables horizontal scrolling
        v.setScrollableY(true); // enables vertical scrolling
        v.setScalable(true); // enables horizontal zooming and scrolling
        v.setScalableY(true); // enables vertical zooming and scrolling
    }

    public static void resetFftRealViewport(Viewport v, int captureSize) {
        //Y
        v.setYAxisBoundsManual(true);
        v.setMinY(-128);
        v.setMaxY(127);
        //X
        v.setXAxisBoundsManual(true);
        v.setMinX(0);
        v.setMaxX(captureSize / 2);
        //zoom & scroll
        v.setScrollable(true); // enables horizontal scrolling
        v.setScrollableY(true); // enables vertical scrolling
        v.setScalable(true); // enables horizontal zooming and scrolling
        v.setScalableY(true); // enables vertical zooming and scrolling
    }

    public static void resetFftImagViewport(Viewport v, int captureSize) {
        //Y
        v.setYAxisBoundsManual(true);
        v.setMinY(-128);
        v.setMaxY(127);
        //X
        v.setXAxisBoundsManual(true);
        v.setMinX(0);
        v.setMaxX(captureSize / 2);
        //zoom & scroll
        v.setScrollable(true); // enables horizontal scrolling
        v.setScrollableY(true); // enables vertical scrolling
        v.setScalable(true); // enables horizontal zooming and scrolling
        v.setScalableY(true); // enables vertical zooming and scrolling
    }
}
