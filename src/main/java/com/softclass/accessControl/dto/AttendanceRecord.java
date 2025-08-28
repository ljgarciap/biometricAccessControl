package com.softclass.accessControl.dto;

import java.time.LocalDateTime;

public class AttendanceRecord {
    private String documentId;
    private LocalDateTime timestamp;
    private String type; // "IN" or "OUT"

    public AttendanceRecord(String documentId, String type) {
        this.documentId = documentId;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getDocumentId() { return documentId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
}
