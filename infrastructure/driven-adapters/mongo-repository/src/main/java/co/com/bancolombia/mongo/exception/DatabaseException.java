package co.com.bancolombia.mongo.exception;

/**
 * Custom exception for database connection errors
 */
public class DatabaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String errorCode;

    public DatabaseException(String message) {
        super(message);
        this.errorCode = "DATABASE_ERROR";
    }

    public DatabaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DATABASE_ERROR";
    }

    public DatabaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

