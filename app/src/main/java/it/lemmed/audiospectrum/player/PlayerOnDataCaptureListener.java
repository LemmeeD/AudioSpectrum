package it.lemmed.audiospectrum.player;

import android.media.audiofx.Visualizer;
import it.lemmed.audiospectrum.visualizerview.VisualizerView;
import it.lemmed.audiospectrum.visualizerview.VisualizerViewWaveform;

public class PlayerOnDataCaptureListener implements Visualizer.OnDataCaptureListener {
    //FIELDS
    private final VisualizerView visualizerViewWaveform;
    private final VisualizerView visualizerViewFft1;
    private final VisualizerView visualizerViewFft2;

    //CONSTRUCTORS
    public PlayerOnDataCaptureListener(VisualizerView visualizerViewWaveform) {
        this.visualizerViewWaveform = visualizerViewWaveform;
        this.visualizerViewFft1 = null;
        this.visualizerViewFft2 = null;
    }

    public PlayerOnDataCaptureListener(VisualizerView visualizerViewFft1, VisualizerView visualizerViewFft2) {
        this.visualizerViewWaveform = null;
        this.visualizerViewFft1 = visualizerViewFft1;
        this.visualizerViewFft2 = visualizerViewFft2;
    }

    public PlayerOnDataCaptureListener(VisualizerView visualizerViewWaveform, VisualizerView visualizerViewFft1, VisualizerView visualizerViewFft2) {
        this.visualizerViewWaveform = visualizerViewWaveform;
        this.visualizerViewFft1 = visualizerViewFft1;
        this.visualizerViewFft2 = visualizerViewFft2;
    }

    //METHODS
    //8 bit: byte waveform: [-128, 128]
    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        this.visualizerViewWaveform.updateVisualizer(waveform);
    }

    //8 bit: byte fft: [-128, 128]
    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        this.visualizerViewFft1.updateVisualizer(fft);
        this.visualizerViewFft2.updateVisualizer(fft);
    }
}
