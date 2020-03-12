package obby0511.sentiment.analysis;

@SuppressWarnings("unused")
public class LanguageServiceException extends RuntimeException {
    public LanguageServiceException() {
        super();
    }

    public LanguageServiceException(String message) {
        super(message);
    }

    public LanguageServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LanguageServiceException(Throwable cause) {
        super(cause);
    }

    protected LanguageServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
