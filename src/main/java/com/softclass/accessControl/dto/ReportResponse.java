package com.softclass.accessControl.dto;

import java.util.List;

public class ReportResponse {
    private List<AttendanceRecord> records;

    public ReportResponse(List<AttendanceRecord> records) {
        this.records = records;
    }

    public List<AttendanceRecord> getRecords() { return records; }
}