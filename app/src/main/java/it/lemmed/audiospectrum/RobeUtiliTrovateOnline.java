package it.lemmed.audiospectrum;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class RobeUtiliTrovateOnline {


    /*
    protected void addRecordingToMediaLibrary() {
        //creating content values of size 4
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());

        //creating content resolver and storing it in the external content uri
        ContentResolver contentResolver = getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri = contentResolver.insert(base, values);

        //sending broadcast message to scan the media file so that it can be available
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
    }
    */

    /*
    public static void returnMagnitudeAndPhase() {
        int n = fft.size();
        float[] magnitudes = new float[n / 2 + 1];
        float[] phases = new float[n / 2 + 1];
        magnitudes[0] = (float)Math.abs(fft[0]);      // DC
        magnitudes[n / 2] = (float)Math.abs(fft[1]);  // Nyquist
        phases[0] = phases[n / 2] = 0;
        for (int k = 1; k < n / 2; k++) {
            int i = k * 2;
            magnitudes[k] = (float)Math.hypot(fft[i], fft[i + 1]);
            phases[k] = (float)Math.atan2(fft[i + 1], fft[i]);
        }
    }
    */

    /*
    //metodo scritto nella MainActivity per testare un minimo la concorrenza come in vecchie lezioni. Probabilmente scritto per niente...
        private synchronized static void testCallablesCuncurrency() {
        ExecutorService service = Executors.newCachedThreadPool();
        List<Callable<Void>> callables = new LinkedList<>();
        callables.add(new Callable() {
            @Override
            public Void call() {
                for (int i=0; i<51; i++) {
                    LogDebug.log(Integer.toString(i));
                }
                return null;
            }
        });
        callables.add(new Callable() {
            @Override
            public Void call() {
                for (int i=100; i<151; i++) {
                    LogDebug.log(Integer.toString(i));
                }
                return null;
            }
        });
        try {
            List<Future<Void>> futures = service.invokeAll(callables);
            int count = 1;
            for (Future<Void> f: futures) {
                Void result = f.get();
                LogDebug.log("future"+count+" is done? "+f.isDone()+".");
                count++;
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
     */
}
