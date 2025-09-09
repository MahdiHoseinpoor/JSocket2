package JSocket2.Utils;

import java.io.File;

/**
 * A utility class providing helper methods for file-related operations.
 */
public class FileUtil {
    /**
     * Extracts the name of a file without its extension.
     * For example, "document.txt" would return "document".
     *
     * @param file The {@link File} object to process.
     * @return The file name without the extension, or the full name if no extension is found.
     */
    public static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }

    /**
     * Extracts the extension of a file.
     * For example, "document.txt" would return "txt".
     *
     * @param file The {@link File} object to process.
     * @return The file extension as a string, or an empty string if no extension is found.
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}