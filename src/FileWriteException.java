public class FileWriteException extends Exception {
    public boolean failed;
    public FileWriteException(boolean failed, String message) {
        super(message);
        this.failed = failed;
    }
}
