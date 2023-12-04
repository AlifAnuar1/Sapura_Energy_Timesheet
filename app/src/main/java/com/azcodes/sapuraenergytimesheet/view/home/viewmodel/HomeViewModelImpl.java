package com.azcodes.sapuraenergytimesheet.view.home.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.azcodes.sapuraenergytimesheet.model.TimesheetEntryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModelImpl extends ViewModel implements HomeViewModel {

    private List<TimesheetEntryModel> timesheetList;

    private DatabaseReference databaseReference;

    private MutableLiveData<HomeViewModel.HomeEvent> _mutableHomeEvent = new MutableLiveData<>();

    @Override
    public LiveData<HomeEvent> getState() {
        return _mutableHomeEvent;
    }

    @Override
    public void getTimesheetList() {
        databaseReference = FirebaseDatabase.getInstance().getReference("timesheet");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timesheetList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    TimesheetEntryModel dataClass = item.getValue(TimesheetEntryModel.class);
                    timesheetList.add(dataClass);
                }

                _mutableHomeEvent.postValue(new HomeEvent.OnDisplayTimesheetList(timesheetList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void createTimesheetEntry(TimesheetEntryModel entry) {
        if (TextUtils.isEmpty(entry.projectTitle)) {
            _mutableHomeEvent.postValue(new HomeEvent.OnFormError("Please Enter Project Title."));
        } else if (TextUtils.isEmpty(entry.taskDescription)) {
            _mutableHomeEvent.postValue(new HomeEvent.OnFormError("Please Enter Task Title."));
        } else if (TextUtils.isEmpty(entry.assignedTo)) {
            _mutableHomeEvent.postValue(new HomeEvent.OnFormError("Please Enter Who Will Be Assigned To This Entry."));
        } else if (TextUtils.isEmpty(entry.dateFrom)) {
            _mutableHomeEvent.postValue(new HomeEvent.OnFormError("Please Enter The Start Date."));
        } else if (TextUtils.isEmpty(entry.dateTo)) {
            _mutableHomeEvent.postValue(new HomeEvent.OnFormError("Please Enter The End Date."));
        } else if (TextUtils.isEmpty(entry.dateTo)) {
            _mutableHomeEvent.postValue(new HomeEvent.OnFormError("Please Enter The End Date."));
        } else if (TextUtils.isEmpty(entry.status)) {
            _mutableHomeEvent.postValue(new HomeEvent.OnFormError("Please Enter The Status."));
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("timesheet");

            String entryId = databaseReference.push().getKey();
            TimesheetEntryModel newEntry = new TimesheetEntryModel(
                    entryId, entry.projectTitle, entry.taskDescription, entry.assignedTo,
                    entry.dateFrom, entry.dateTo, entry.status);

            databaseReference.child(entryId).setValue(newEntry).addOnSuccessListener(success -> {
                _mutableHomeEvent.postValue(new HomeEvent.OnCreateTimesheetEntry(true));
            }).addOnFailureListener(failure -> {
                _mutableHomeEvent.postValue(new HomeEvent.OnUpdateTimesheetEntry(false));
            });
        }
    }

    @Override
    public void updateTimesheetEntry(TimesheetEntryModel updatedEntry) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("/projectTitle", updatedEntry.projectTitle);
        updates.put("/taskDecription", updatedEntry.taskDescription);
        updates.put("/assignedTo", updatedEntry.assignedTo);
        updates.put("/dateFrom", updatedEntry.dateFrom);
        updates.put("/dateTo", updatedEntry.dateTo);
        updates.put("/status", updatedEntry.status);

        databaseReference = FirebaseDatabase.getInstance().getReference("timesheet").child(updatedEntry.entryId);
        databaseReference.updateChildren(updates).addOnSuccessListener(success -> {
            _mutableHomeEvent.postValue(new HomeEvent.OnUpdateTimesheetEntry(true));
        }).addOnFailureListener(failure -> {
            _mutableHomeEvent.postValue(new HomeEvent.OnUpdateTimesheetEntry(false));
        });
    }

    @Override
    public void deleteTimesheetEntry(TimesheetEntryModel entry) {
        databaseReference = FirebaseDatabase.getInstance().getReference("timesheet");
        databaseReference.child(entry.entryId).removeValue().addOnSuccessListener(success -> {
            _mutableHomeEvent.postValue(new HomeEvent.OnDeleteTimesheetEntry(true));
        }).addOnFailureListener(failure -> {
            _mutableHomeEvent.postValue(new HomeEvent.OnDeleteTimesheetEntry(false));
        });
    }

    @Override
    public void searchTimesheetEntry(String text) {
        if (!TextUtils.isEmpty(text)) {
            ArrayList<TimesheetEntryModel> searchList = new ArrayList<>();
            for (TimesheetEntryModel item : timesheetList) {
                if (item.projectTitle.contains(text)) {
                    searchList.add(item);
                } else if (item.taskDescription.contains(text)) {
                    searchList.add(item);
                } else if (item.assignedTo.contains(text)) {
                    searchList.add(item);
                } else if (item.dateFrom.contains(text)) {
                    searchList.add(item);
                } else if (item.dateTo.contains(text)) {
                    searchList.add(item);
                } else if (item.status.contains(text)) {
                    searchList.add(item);
                }
            }

            _mutableHomeEvent.postValue(new HomeEvent.OnDisplayTimesheetList(searchList));
        } else {
            _mutableHomeEvent.postValue(new HomeEvent.OnDisplayTimesheetList(timesheetList));
        }
    }

    @Override
    public void getUserList() {
        databaseReference = FirebaseDatabase.getInstance().getReference("timesheet");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timesheetList = new ArrayList<>();
                List<String> userList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    TimesheetEntryModel dataClass = item.getValue(TimesheetEntryModel.class);
                    timesheetList.add(dataClass);
                }

                for (TimesheetEntryModel item: timesheetList){
                    if(!userList.contains(item.assignedTo)){
                        userList.add(item.assignedTo);
                    }
                }

                _mutableHomeEvent.postValue(new HomeEvent.OnGetUserList(userList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
