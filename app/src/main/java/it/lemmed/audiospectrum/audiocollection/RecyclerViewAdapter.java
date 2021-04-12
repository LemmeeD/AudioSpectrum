package it.lemmed.audiospectrum.audiocollection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.List;
import it.lemmed.audiospectrum.LogDebug;
import it.lemmed.audiospectrum.MainActivity;
import it.lemmed.audiospectrum.R;
import it.lemmed.audiospectrum.utils.FileUtils;
import it.lemmed.audiospectrum.utils.StringUtils;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    //FIELDS
    private List<RowRecord> data;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    //CONSTRUCTORS
    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, List<RowRecord> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    //METHODS
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RowRecord rowRecord;
        try {
            rowRecord = data.get(position);
        }
        catch (IndexOutOfBoundsException e) {
            return;
        }
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
                new int[] { -android.R.attr.state_pressed }
        };
        int[] colors = new int[] {
                holder.fab.getContext().getResources().getColor(R.color.grigioScuro),
                holder.fab.getContext().getResources().getColor(R.color.nero)
        };
        holder.fab.setBackgroundTintList(new ColorStateList(states, colors));
        holder.textViewName.setText(rowRecord.getName());
        holder.textViewDuration.setText(StringUtils.returnStringifiedDuration(rowRecord.getDuration()));
        holder.textViewType.setText(rowRecord.getExtension());
        holder.textViewSize.setText(FileUtils.returnShortStringifiedSize(rowRecord.getSize()));
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                List<RowRecord> newData = getData();
                RowRecord recordToDelete = newData.get(position);
                File fileToDelete = new File(MainActivity.userDataDirectory, recordToDelete.getName() + recordToDelete.getExtension());
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(v.getContext());
                builder.setTitle(v.getResources().getString(R.string.rva_class01)+fileToDelete.getName()+"?");
                builder.setPositiveButton(v.getResources().getString(R.string.utils05), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (fileToDelete.delete()) {
                            newData.remove(position);
                            LogDebug.log("File " + fileToDelete.getName() + " deleted.");
                        } else {
                            LogDebug.log("unable to delete file " + fileToDelete.getName());
                        }
                        RecyclerViewPopulator populator = new RecyclerViewPopulator(MainActivity.recyclerView.getContext(), MainActivity.recyclerView);
                        populator.populateRecyclerView(newData);
                    }
                });
                builder.setNegativeButton(v.getResources().getString(R.string.utils06), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }

    // convenience method for getting data at click position
    RowRecord getItem(int id) {
        return data.get(id);
    }

    List<RowRecord> getData() {
        return this.data;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemclickListener) {
        this.clickListener = itemclickListener;
    }

    //INNER CLASSES
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //FIELDS
        protected TextView textViewName;
        protected TextView textViewType;
        protected TextView textViewSize;
        protected TextView textViewDuration;
        protected FloatingActionButton fab;
        //CONSTRUCTORS
        ViewHolder(View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.rowName);
            this.textViewType = itemView.findViewById(R.id.rowType);
            this.textViewSize = itemView.findViewById(R.id.rowSize);
            this.textViewDuration = itemView.findViewById(R.id.rowDuration);
            this.fab = itemView.findViewById(R.id.fab1);
            itemView.setOnClickListener(this);
        }
        //METHODS
        @Override
        public void onClick(View view) {
            if (RecyclerViewAdapter.this.clickListener != null) {       //clickListener field of container class RecyclerViewAdapter
                view.setBackgroundColor(view.getContext().getResources().getColor(R.color.grigio));
                RecyclerViewAdapter.this.clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    //INNER INTERFACES
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}