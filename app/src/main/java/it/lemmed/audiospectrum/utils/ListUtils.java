package it.lemmed.audiospectrum.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Comparator;
import java.util.List;
import it.lemmed.audiospectrum.audiocollection.RowRecord;

public class ListUtils {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<RowRecord> orderList(List<RowRecord> list) {
        list.sort(new Comparator<RowRecord>() {
            @Override
            public int compare(RowRecord o1, RowRecord o2) {
                if (o1.getName().equals(o2.getName())) {
                    return 0;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return list;
    }
}
