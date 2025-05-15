package io.github.jan.supabase.storage;

/**
 * Custom implementation of UploadData for compatibility
 */
public class UploadData {
    private final byte[] data;
    
    private UploadData(byte[] data) {
        this.data = data;
    }
    
    /**
     * Creates upload data from byte array
     * @param data The data to upload
     * @return The UploadData object
     */
    public static UploadData from(byte[] data) {
        return new UploadData(data);
    }
    
    /**
     * Gets the data to upload
     * @return The data as byte array
     */
    public byte[] getData() {
        return data;
    }
}
