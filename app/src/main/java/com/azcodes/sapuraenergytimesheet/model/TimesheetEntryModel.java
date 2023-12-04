package com.azcodes.sapuraenergytimesheet.model;

public class TimesheetEntryModel {

    public String entryId;

    public String projectTitle;

    public String taskDescription;

    public String assignedTo;

    public String dateFrom;

    public String dateTo;

    public String status;

    public TimesheetEntryModel() {
    }

    public TimesheetEntryModel(
            String entryId, String projectTitle, String taskDescription, String assignedTo,
            String dateFrom, String dateTo, String status
    ) {
        this.entryId = entryId;
        this.projectTitle = projectTitle;
        this.taskDescription = taskDescription;
        this.assignedTo = assignedTo;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.status = status;
    }
}
