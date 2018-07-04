package org.atos.scouter.util;

/**
 * Class that provide static common methods used in different parts of the project
 * Created by Musab on 07/04/2017.
 */
public class CommonUtils {

    /**
     * A static method that converts a long number of bytes to human readable format
     * @param bytes: the number of bytes to convert
     * @param si: boolean flag to indicate SI units
     * @return String representing the bytes in human readable format
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
