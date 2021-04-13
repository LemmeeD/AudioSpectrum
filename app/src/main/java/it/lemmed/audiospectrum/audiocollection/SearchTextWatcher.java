package it.lemmed.audiospectrum.audiocollection;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import java.util.List;
import it.lemmed.audiospectrum.R;

public class SearchTextWatcher implements TextWatcher {
    //FIELDS
    protected Context context;
    protected EditText input;
    protected RecyclerViewPopulator populator;
    protected List<RowRecord> records;
    protected RecyclerViewAdapter adapter;
    protected TextView suggeritionText;

    //CONSTRUCTORS
    @RequiresApi(api = Build.VERSION_CODES.N)
    public SearchTextWatcher(Context context, EditText input, TextView suggeritionText, RecyclerViewPopulator populator) {
        this.context = context;
        this.input = input;
        this.suggeritionText = suggeritionText;
        this.populator = populator;
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
            this.populator.populateRecyclerView(s.toString());
            this.suggeritionText.setText("");

        }
        else {
            this.populator.populateRecyclerView();
            this.suggeritionText.setText(suggeritionText.getContext().getResources().getString(R.string.main_activity02));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
