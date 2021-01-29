package it.lemmed.audiospectrum.audiocollection;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.R;

public class SearchTextWatcher implements TextWatcher {
    //FIELDS
    Context context;
    EditText input;
    RecyclerView recyclerView;
    List<RowRecord> records;
    RecyclerViewAdapter adapter;
    TextView suggeritionText;

    //CONSTRUCTORS
    @RequiresApi(api = Build.VERSION_CODES.N)
    public SearchTextWatcher(Context context, EditText input, TextView suggeritionText, RecyclerView recyclerView) {
        this.context = context;
        this.input = input;
        this.suggeritionText = suggeritionText;
        this.recyclerView = recyclerView;
        //records = AudioCollectionProvider.queryAudioCollection(context);
        records = AudioCollectionProvider.executeQueryFolder();
        this.adapter = new RecyclerViewAdapter(context, records);
    }

    //METHODS
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if ( !(s.equals("")) ) {
            (new RecyclerViewPopulator(this.context, this.recyclerView)).populateRecyclerView(s.toString());
            this.suggeritionText.setText("");

        }
        else {
            (new RecyclerViewPopulator(this.context, this.recyclerView)).populateRecyclerView();
            this.suggeritionText.setText(suggeritionText.getContext().getResources().getString(R.string.main_activity02));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
