package seedu.address.storage;

import java.io.IOException;

/**
 * Signals that the folder for storing student data could not be created.
 */
public class DataStorageLocationException extends IOException {
    /**
     * @param message The description of the failure.
     * @param cause The underlying exception.
     */
    public DataStorageLocationException(String message, IOException cause) {
        super(message, cause);
    }
}
