package it.lemmed.audiospectrum.player;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;

import it.lemmed.audiospectrum.R;
import it.lemmed.audiospectrum.utils.StringUtils;

public enum PlotType {
    //ENUMS
    WAVEFORM("Waveform"),
    FFT_MAGNITUDE("Magnitude FFT"),
    FFT_PHASE("Phase FFT"),
    FFT_REAL("Real Part FFT"),
    FFT_IMAGINARY("Imaginary Part FFT");

    //FIELDS
    protected String name;

    //CONSTRUCTORS
    PlotType(String name) {
        this.name = name;
    }

    //METHODS
    public void resetViewport(Viewport v, int captureSize, int samplingRate) {
        if (this == WAVEFORM) {
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
        else if (this == FFT_MAGNITUDE) {
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
        else if (this == FFT_PHASE) {
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
        else if ((this == FFT_REAL) || (this == FFT_IMAGINARY)) {
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

    public void formatGrid(GraphView graph, int captureSize, int samplingRate) {
        if (this == WAVEFORM) {
            //labels: titles
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
                        return super.formatLabel(value, false);
                    }
                    else {
                        if (value == 0) {
                            return "0";
                        }
                        else {
                            return super.formatLabel(value, false);
                        }
                    }
                }
            });
        }
        else if (this == FFT_MAGNITUDE) {
            //labels: titles
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
                            return "DC";
                        }
                        else {
                            return StringUtils.returnStringifiedFrequency((value * samplingRate) / captureSize);
                        }
                    }
                    else {
                        if (value == 0) {
                            return "0";
                        }
                        else {
                            return super.formatLabel(value, false);
                        }
                    }
                }
            });
        }
        else if (this == FFT_PHASE) {
            //labels: titles
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
                            return "DC";
                        }
                        else {
                            return StringUtils.returnStringifiedFrequency((value*samplingRate) / captureSize);
                        }
                    }
                    else {
                        if (value == 0) {
                            return "0";
                        }
                        else {
                            return super.formatLabel(value, false);
                        }
                    }
                }
            });
        }
        else if (this == FFT_REAL) {
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
                            return "DC";
                        }
                        else {
                            return StringUtils.returnStringifiedFrequency((value*samplingRate) / captureSize);
                        }
                    }
                    else {
                        if (value == 0) {
                            return "0";
                        }
                        else {
                            return super.formatLabel(value, false);
                        }
                    }
                }
            });
        }
        else if (this == FFT_IMAGINARY) {
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
                            return "DC";
                        }
                        else {
                            return StringUtils.returnStringifiedFrequency((value*samplingRate) / captureSize);
                        }
                    }
                    else {
                        if (value == 0) {
                            return "0";
                        }
                        else {
                            return super.formatLabel(value, false);
                        }
                    }
                }
            });
        }
    }
}
