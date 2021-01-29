package it.lemmed.audiospectrum;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import it.lemmed.audiospectrum.audiocollection.RecyclerViewPopulator;
import it.lemmed.audiospectrum.audiocollection.SearchTextWatcher;
import it.lemmed.audiospectrum.settings.SettingsActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    //FIELDS
    public static File userDataDirectory;
    public static RecyclerView recyclerView;
    protected static RecyclerViewPopulator populator;
    private boolean preferencesChanged = true;

    //Permissions
    private boolean permissionToRecordAccepted = false;
    private final String [] permissions = { Manifest.permission.RECORD_AUDIO };
    private static final int REQUEST_ALL = 666;

    //METHODS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ALL:
                permissionToRecordAccepted  = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDataDirectory = this.getFilesDir();
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_ALL);

        LogDebug.log("--- MainActivity ---");

        FloatingActionButton button = findViewById(R.id.recording_fab);
        button.setOnTouchListener(new RecordingButtonTouchListener());

        LogDebug.log("Directory is: " + userDataDirectory);

        //FileUtils.deleteFileWithoutName();
        //FileUtils.deleteAllFiles(userDataDirectory.getAbsolutePath());

        //testCallablesCuncurrency();

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView recyclerView = findViewById(R.id.filez);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.computeScroll();
        MainActivity.recyclerView = recyclerView;

        populator = new RecyclerViewPopulator(this, recyclerView);
        populator.populateRecyclerView();

        TextView suggeritionText = findViewById(R.id.text_suggerition);
        EditText input = findViewById(R.id.searchText);
        input.addTextChangedListener(new SearchTextWatcher(this, input, suggeritionText, recyclerView));        //fa il popolate

        ImageView imageRefresh = findViewById(R.id.image_refresh);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
                new int[] { -android.R.attr.state_pressed }
        };
        int[] colors = new int[] {
                getResources().getColor(R.color.verdeAcqua),
                getResources().getColor(R.color.arancione)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        imageRefresh.setImageTintList(colorStateList);
        imageRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("")) {
                    populator.populateRecyclerView();
                    Toast.makeText(imageRefresh.getContext(), getResources().getString(R.string.main_activity03), Toast.LENGTH_SHORT).show();
                }
                else {
                    populator.populateRecyclerView(input.getText().toString());
                    Toast.makeText(imageRefresh.getContext(), getResources().getString(R.string.main_activity03), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //synchronized or not??
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
}