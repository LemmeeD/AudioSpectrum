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
import android.content.res.Configuration;
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
import it.lemmed.audiospectrum.audiocollection.RecyclerViewPopulator;
import it.lemmed.audiospectrum.audiocollection.SearchTextWatcher;
import it.lemmed.audiospectrum.settings.SettingsActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {
    //FIELDS
    public static File userDataDirectory;
    public static RecyclerView recyclerView;
    protected static RecyclerViewPopulator populator;
    protected static FloatingActionButton button;
    //Permissions
    private boolean permissionToRecordAccepted = false;
    private final String [] permissions = { Manifest.permission.RECORD_AUDIO };
    private static final int REQUEST_ALL = 666;

    //CONSTRUCTORS

    //METHODS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ALL:
                permissionToRecordAccepted  = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }
        if (!permissionToRecordAccepted) {
            this.finish();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogDebug.log("onCreate");
        userDataDirectory = this.getFilesDir();
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_ALL);
        LogDebug.log("--- MainActivity ---");
        button = findViewById(R.id.recording_fab);
        LogDebug.log("Directory is: " + userDataDirectory);

        //FileUtils.deleteFileWithoutName();
        //FileUtils.deleteAllFiles(userDataDirectory.getAbsolutePath());
        //testCallablesCuncurrency();

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogDebug.log("onOptionsItemSelected");
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
        LogDebug.log("onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogDebug.log("onStart");
        //BINDINGS
        button.setOnTouchListener(new RecordingButtonTouchListener());
        RecyclerView recyclerView = findViewById(R.id.filez);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        //Scroll is handled "externally" with RecyclerViews...
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
        //RecyclerViewPopulator object that obviously populate the RecyclerView with audio files from the designated directory of the entire app
        populator = new RecyclerViewPopulator(this, recyclerView);
        populator.populateRecyclerView();
        //Searchbar handling
        TextView suggestionText = findViewById(R.id.text_suggerition);
        EditText input = findViewById(R.id.searchText);
        input.addTextChangedListener(new SearchTextWatcher(this, input, suggestionText, recyclerView));        //internally everytime a letter changes, it re-populates the RecyclerView
        //Pretty useless... It can be useful if you add file while th eapp is running in development, maybe using Device File Explorer
        ImageView imageRefresh = findViewById(R.id.image_refresh);
        //Handling record button
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
        LogDebug.log("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogDebug.log("onDestroy");
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogDebug.log("onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogDebug.log("onResume");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.reInitialize();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.reInitialize();
        }
    }

    private void reInitialize() {
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);
        button = findViewById(R.id.recording_fab);
        this.onStart();
    }
}