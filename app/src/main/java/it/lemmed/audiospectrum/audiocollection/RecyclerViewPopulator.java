package it.lemmed.audiospectrum.audiocollection;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.MainActivity;
import it.lemmed.audiospectrum.player.PlayerActivity;
import it.lemmed.audiospectrum.utils.ListUtils;

public class RecyclerViewPopulator implements RecyclerViewAdapter.ItemClickListener {
    //FIELDS
    protected Context context;
    protected RecyclerView recyclerView;
    protected List<RowRecord> records;
    protected RecyclerViewAdapter adapter;

    //CONSTRUCTORS
    public RecyclerViewPopulator(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    //METHODS
    public List<RowRecord> getCurrentPopulation() {
        return this.records;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void populateRecyclerView(List<RowRecord> data) {
        this.adapter = new RecyclerViewAdapter(context, ListUtils.orderList(data));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void populateRecyclerView() {
        records = AudioCollectionProvider.executeQueryFolder();
        this.adapter = new RecyclerViewAdapter(context, ListUtils.orderList(records));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void populateRecyclerView(String nameToSearch) {
        records = AudioCollectionProvider.executeQueryFolder(nameToSearch);
        this.adapter = new RecyclerViewAdapter(context, ListUtils.orderList(records));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(view.getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.recyclerView.getContext(), PlayerActivity.class);
        String filenameWithExtension = this.adapter.getItem(position).getName() + this.adapter.getItem(position).getExtension();
        intent.putExtra("nome_file", filenameWithExtension);
        intent.putExtra("position", Integer.toString(position));
        intent.putExtra("extension", this.adapter.getItem(position).getExtension());
        intent.putExtra("duration", this.adapter.getItem(position).getStringifiedDuration());
        MainActivity.recyclerView.getContext().startActivity(intent);
    }
}
