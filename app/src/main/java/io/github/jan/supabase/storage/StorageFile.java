package io.github.jan.supabase.storage;

/**
 * Custom implementation of StorageFile for compatibility
 */
public class StorageFile {
    private final String path;

    /**
     * Creates a new StorageFile
     * @param path The path to the file
     */
    public StorageFile(String path) {
        this.path = path;
    }

    /**
     * Gets the path of the file
     * @return The path
     */
    public String getPath() {
        return path;
    }
    
    @Override
    public String toString() {
        return path;
    }
}
