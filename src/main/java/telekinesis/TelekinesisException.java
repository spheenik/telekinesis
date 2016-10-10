package telekinesis;

public class TelekinesisException extends RuntimeException {

    public TelekinesisException(Exception cause, String format, Object... parameters) {
        super(String.format(format, parameters), cause);
    }

    public TelekinesisException(String format, Object... parameters) {
        super(String.format(format, parameters));
    }

    public TelekinesisException(Throwable cause) {
        super(cause);
    }

}
