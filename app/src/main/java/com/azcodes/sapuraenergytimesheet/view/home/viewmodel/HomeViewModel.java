package com.azcodes.sapuraenergytimesheet.view.home.viewmodel;

import androidx.lifecycle.LiveData;

import com.azcodes.sapuraenergytimesheet.model.TimesheetEntryModel;

import java.util.List;

public interface HomeViewModel {

    LiveData<HomeEvent> getState();

    void getTimesheetList();
    void createTimesheetEntry(TimesheetEntryModel entry);
    void updateTimesheetEntry(TimesheetEntryModel updatedEntry);
    void deleteTimesheetEntry(TimesheetEntryModel entry);
    void searchTimesheetEntry(String text);

    void getUserList();

    interface HomeEvent {

        class OnDisplayTimesheetList implements HomeEvent {
            private List<TimesheetEntryModel> timesheetList;

            public OnDisplayTimesheetList(List<TimesheetEntryModel> timesheetList){
                this.timesheetList = timesheetList;
            }

            public List<TimesheetEntryModel> getTimesheetList() {
                return timesheetList;
            }
        }

        class OnFormError implements HomeEvent {
            private String errorMessage;

            public OnFormError(String errorMessage){
                this.errorMessage = errorMessage;
            }

            public String getErrorMessage() {
                return errorMessage;
            }
        }

        class OnCreateTimesheetEntry implements HomeEvent {
            private Boolean isSuccess;

            public OnCreateTimesheetEntry(Boolean isSuccess){
                this.isSuccess = isSuccess;
            }

            public Boolean getSuccess() {
                return isSuccess;
            }
        }

        class OnUpdateTimesheetEntry implements HomeEvent {
            private Boolean isSuccess;

            public OnUpdateTimesheetEntry(Boolean isSuccess){
                this.isSuccess = isSuccess;
            }

            public Boolean getSuccess() {
                return isSuccess;
            }
        }

        class OnDeleteTimesheetEntry implements HomeEvent {
            private Boolean isSuccess;

            public OnDeleteTimesheetEntry(Boolean isSuccess){
                this.isSuccess = isSuccess;
            }

            public Boolean getSuccess() {
                return isSuccess;
            }
        }

        class OnDisplaySearchTimesheetList implements HomeEvent {
            private List<TimesheetEntryModel> timesheetList;

            public OnDisplaySearchTimesheetList(List<TimesheetEntryModel> timesheetList){
                this.timesheetList = timesheetList;
            }

            public List<TimesheetEntryModel> getTimesheetList() {
                return timesheetList;
            }
        }

        class OnGetUserList implements HomeEvent {
            private List<String> userList;

            public OnGetUserList(List<String> userList){
                this.userList = userList;
            }

            public List<String> getUserList() {
                return userList;
            }
        }
    }

}
