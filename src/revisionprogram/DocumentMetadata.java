package revisionprogram;

import revisionprogram.documents.DocumentType;

import java.time.LocalDate;

public record DocumentMetadata(String name, String title, LocalDate lastRevised, DocumentType documentType) { }
