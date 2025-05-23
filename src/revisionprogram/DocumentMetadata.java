package revisionprogram;

import revisionprogram.documents.DocumentType;

import java.time.LocalDate;

public record DocumentMetadata(String subject, String name, String title, LocalDate lastRevised, LocalDate nextRevision, DocumentType documentType) { }
