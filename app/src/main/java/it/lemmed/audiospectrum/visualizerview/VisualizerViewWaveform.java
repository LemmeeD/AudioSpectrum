package it.lemmed.audiospectrum.visualizerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.R;
import it.lemmed.audiospectrum.player.DoubleTapListener;
import it.lemmed.audiospectrum.player.ScaleListener;

public class VisualizerViewWaveform extends View {
    //FIELDS
    protected int id;
    protected byte[] bytes;
    protected float[] points;
    protected ScaleGestureDetector scaleGestureDetector;
    protected GestureDetector doubleTapDetector;
    protected Paint paint = new Paint();
    protected float strokeWidth;
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    //SOLUZIONE MOLTO POCO ELEGANTE... Ma funziona...
    private int originalLeft;
    private int originalTop;
    private int originalRight;
    private int originalBottom;
    private boolean doneGettingOriginalSize;

    //CONSTRUCTORS
    public VisualizerViewWaveform(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.doneGettingOriginalSize = false;
        init();
    }

    //METHODS
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.doubleTapDetector.onTouchEvent(event);
        this.scaleGestureDetector.onTouchEvent(event);
        this.onPan(event);
        return true;
    }

    public void updateVisualizer(byte[] bytes) {
        this.bytes = bytes;
        this.postInvalidate();       //chiamerà onDraw
        //this.invalidate();       //chiamerà onDraw. Better from a UI Thread...
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (bytes == null) {
            return;
        }
        //SOLUZIONE MOLTO POCO ELEGANTE... Ma funziona...
        if (!doneGettingOriginalSize) {
            this.originalLeft = this.getLeft();
            this.originalTop = this.getTop();
            this.originalRight = this.getRight();
            this.originalBottom = this.getBottom();
            this.doubleTapDetector = new GestureDetector(this.getContext(), new DoubleTapListener(this, originalLeft, originalTop, originalRight, originalBottom));
            this.scaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener(this, 1f));
        }

        //
        //public void drawLines (float[] pts, int offset, int count, Paint paint)
        //
        //Draw a series of lines. Each line is taken from 4 consecutive values in the pts array.
        //Thus to draw 1 line, the array must contain at least 4 values. This is logically the same as drawing the array as follows:
        //          drawLine(pts[0], pts[1], pts[2], pts[3]) followed by drawLine(pts[4], pts[5], pts[6], pts[7]) and so on.
        //
        //      float pts: Array of points to draw [x0 y0 x1 y1 x2 y2 ...] This value cannot be null.
        //      int offset: Number of values in the array to skip before drawing.
        //      int count: The number of values in the array to process, after skipping "offset" of them. Since each line uses 4 values, the number of "lines" that are drawn is really (count >> 2).
        //      Paint paint: The paint used to draw the points This value cannot be null.
        //



        if (points == null || points.length < bytes.length * 4) {
            points = new float[bytes.length * 4];
        }
        //LogDebug.log("Dimension VisualizerView (HxW): "+getHeight()+"x"+getWidth()+" [px]");
        //LogDebug.log("bytes.length = "+bytes.length);
        float coeffX = getWidth() / ((float) (bytes.length - 1));
        float coeffY = (getHeight() / 2f) / 128f;       //original: (rect.height() / 2) / 128
        for (int i = 0; i < bytes.length - 1; i++) {
            canvas.drawPoint(i * coeffX, ((float) getHeight()/2) + ((byte) (bytes[i] + 128)) * coeffY, this.paint);
        }
        //canvas.drawLines(points, forePaint);




        //non va... non so perchè
        /*float coeffX = ((float) getWidth()) / ((float) bytes.length);
        float coeffY = getHeight() / 256f;
        for (int i = 0; i < bytes.length; i++) {
            canvas.drawPoint(coeffX*i, ((float) coeffY*(bytes[i] + 128)), forePaint);
        }*/
        this.doneGettingOriginalSize = true;
    }

    //AUXILIARY METHODS
    private void init() {
        this.bytes = null;
        this.paint.setStrokeWidth(2f);
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.BLUE);
        this.doubleTapDetector = new GestureDetector(this.getContext(), new DoubleTapListener(this, this.originalLeft, this.originalTop, this.originalRight, originalBottom));
        this.scaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener(this, 1f));
    }

    private boolean onPan(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        //mScaleDetector.onTouchEvent(ev);
        LogDebug.log("testPan: "+originalLeft+"-"+originalTop+"-"+originalRight+"-"+originalBottom);
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                mPosX += dx;
                mPosY += dy;
                this.setLeft((int) (this.getLeft()+mPosX));
                this.setTop((int) (this.getTop()+mPosY));
                this.setRight((int) (this.getRight()+mPosX));
                this.setBottom((int) (this.getBottom()+mPosY));
                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    private void applyCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.myVisualizerView, 0, 0);
        int pcr = 0;
        int pcg = 0;
        int pcb = 0;
        float sw = 0;
        try {
            pcr = a.getInteger(R.styleable.myVisualizerView_myPaintColorRed, 0);
            pcg = a.getInteger(R.styleable.myVisualizerView_myPaintColorGreen, 0);
            pcb = a.getInteger(R.styleable.myVisualizerView_myPaintColorBlu, 0);
            sw = a.getFloat(R.styleable.myVisualizerView_myStrokeWidth, 1f);
        }
        catch (UnsupportedOperationException e) {
            LogDebug.log("The attribute is defined but is not an integer");
        }
        catch (RuntimeException e) {
            LogDebug.log("The TypedArray has already been recycled");
        }
        finally {
            a.recycle();
        }
    }

    private static int correctIntegerForRGB(int value) {
        if (value < 0) { return 0; }
        else if (value > 255) { return 255; }
        else { return value; }
    }
}
