package revisionprogram;

import revisionprogram.documents.Document;
import revisionprogram.files.FileException;

public record DocumentErrorPackage(Document document, FileException e) {
}
