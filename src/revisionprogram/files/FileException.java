package revisionprogram.files;

public class FileException extends Exception {
    public boolean failed;
    public FileException(boolean failed, String message) {
        super(message);
        this.failed = failed;
    }
}
