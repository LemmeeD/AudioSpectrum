package it.lemmed.audiospectrum.player;

import android.media.audiofx.Visualizer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BaseSeries;
import com.jjoe64.graphview.series.DataPoint;

public class PlayerOnDataCaptureListener implements Visualizer.OnDataCaptureListener {
    //FIELDS
    private final GraphView graphWaveform;
    private final GraphView graphFft1;
    private final GraphView graphFft2;
    private final BaseSeries seriesWaveform;
    private final BaseSeries seriesFft1;
    private final BaseSeries seriesFft2;
    private final boolean visualFftMode;    //true = visualize Fft as Magnitude/Phase, real/Imag otherwise

    //CONSTRUCTORS
    public PlayerOnDataCaptureListener(GraphView graphWaveform, GraphView graphFft1, GraphView graphFft2, BaseSeries seriesWaveform, BaseSeries seriesFft1, BaseSeries seriesFft2, boolean visualModeFft) {
        this.graphWaveform = graphWaveform;
        this.graphFft1 = graphFft1;
        this.graphFft2 = graphFft2;
        this.seriesWaveform = seriesWaveform;
        this.seriesFft1 = seriesFft1;
        this.seriesFft2 = seriesFft2;
        this.visualFftMode = visualModeFft;
    }

    //METHODS
    //8 bit: byte waveform: [-128, 128]. byte[] length is captureSize of visualizer object
    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        //waveform: [-128, 127]
        DataPoint[] points = new DataPoint[waveform.length];
        for (int i = 0; i < waveform.length; i++) {
            if (waveform[i] >= 0) {
                points[i] = new DataPoint(i, waveform[i]-128);
            }
            else {
                points[i] = new DataPoint(i, waveform[i]+128);
            }
        }
        this.seriesWaveform.resetData(points);
    }

    //8 bit: byte fft: [-128, 128]
    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        int n = fft.length;
        if (visualFftMode) {
            //M/A
            //magnitude: [0, 256]
            //phase: [-2*Math.PI, 2*Math.PI]
            float[] magnitude = new float[n / 2 + 1];
            DataPoint[] magnitudePoints = new DataPoint[magnitude.length];
            float[] phase = new float[n / 2 + 1];
            DataPoint[] phasePoints = new DataPoint[phase.length];
            magnitude[0] = (float) Math.abs(fft[0]);      // DC
            magnitudePoints[0] = new DataPoint(0, magnitude[0]);
            magnitude[n / 2] = (float) Math.abs(fft[1]);  // Nyquist
            magnitudePoints[n / 2] = new DataPoint(n / 2, magnitude[n / 2]);
            phase[0] = 0;
            phase[n / 2] = 0;
            phasePoints[0] = new DataPoint(0, 0);
            phasePoints[n / 2] = new DataPoint(n / 2, 0);
            for (int k = 1; k < n / 2; k++) {
                int i = k * 2;
                magnitude[k] = (float) Math.hypot(fft[i], fft[i + 1]);
                magnitudePoints[k] = new DataPoint(k, magnitude[k]);
                phase[k] = (float) Math.atan2(fft[i + 1], fft[i]);
                phasePoints[k] = new DataPoint(k, phase[k]);
            }
            this.seriesFft1.resetData(magnitudePoints);
            this.seriesFft2.resetData(phasePoints);
        }
        else {
            //R/I
            //real part: [-128, 127]
            //imaginary part: [-128, 127]
            float[] real = new float[(n / 2) + 1];
            DataPoint[] realPoints = new DataPoint[(n / 2) + 1];
            float[] imag = new float[(n / 2) + 1];
            DataPoint[] imagPoints = new DataPoint[(n / 2) + 1];
            real[0] = (float) fft[0];
            realPoints[0] = new DataPoint(0, real[0]);
            real[n / 2] = (float) fft[1];
            realPoints[n / 2] = new DataPoint(n / 2, real[n / 2]);
            imag[0] = 0;
            imagPoints[0] = new DataPoint(0, imag[0]);
            imag[n / 2] = 0;
            imagPoints[n / 2] = new DataPoint(n / 2, imag[n / 2]);
            for (int k = 1; k < n / 2; k++) {
                int i = k * 2;
                real[k] = fft[i];
                realPoints[k] = new DataPoint(k, real[k]);
                imag[k] = fft[i+1];
                imagPoints[k] = new DataPoint(k, imag[k]);
            }
            this.seriesFft1.resetData(realPoints);
            this.seriesFft2.resetData(imagPoints);
        }
    }
}
