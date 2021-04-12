package it.lemmed.audiospectrum.utils;

public class NumberUtils {
    //FIELDS
    protected static int[] strokeWidths = new int[5];
    static {
        strokeWidths[0] = 1;
        strokeWidths[1] = 3;
        strokeWidths[2] = 5;
        strokeWidths[3] = 8;
        strokeWidths[4] = 12;
    }
    //CONSTRUCTORS

    //METHODS
    public static int returnStrokeWidth(int n) {    //map: [1, 2, 3, 4, 5] ---> [1, 3, 5, 8, 12]
        if ((n <= 0) || (n >= 6)) {
            return -1;
        }
        else {
            return strokeWidths[n-1];
        }
    }
}
