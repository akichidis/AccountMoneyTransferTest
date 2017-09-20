package repository.db;

/**
 * Exception that is thrown by the datastore operations.
 */
public class DataStoreException extends RuntimeException {
    public DataStoreException(String message) {
        super(message);
    }
}
