package seedu.address.commons.exceptions;

/**
 * Represents an error during loading of data from a file.
 */
public class DataLoadingException extends Exception {
    public DataLoadingException(Exception cause) {
        super(cause);
    }

    /**
     * Constructs a {@code DataLoadingException} with a message describing the failure to the user.
     *
     * @param message The user-facing description of the failure.
     * @param cause The underlying exception.
     */
    public DataLoadingException(String message, Exception cause) {
        super(message, cause);
    }

}
