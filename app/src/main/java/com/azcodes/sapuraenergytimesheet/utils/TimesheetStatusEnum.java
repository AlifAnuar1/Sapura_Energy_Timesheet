package com.azcodes.sapuraenergytimesheet.utils;

public enum TimesheetStatusEnum {
    OPEN("Open"),
    INPROGRESS("In Progress"),
    CLOSED("Closed");

    public final String status;

    private TimesheetStatusEnum(String status){
        this.status = status;
    }
}
