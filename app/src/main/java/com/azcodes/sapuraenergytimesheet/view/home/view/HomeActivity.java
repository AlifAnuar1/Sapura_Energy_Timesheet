package com.azcodes.sapuraenergytimesheet.view.home.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.azcodes.sapuraenergytimesheet.R;
import com.azcodes.sapuraenergytimesheet.databinding.ActivityHomeBinding;
import com.azcodes.sapuraenergytimesheet.databinding.BottomSelectionDetailsBinding;
import com.azcodes.sapuraenergytimesheet.databinding.BottomSelectionStatusBinding;
import com.azcodes.sapuraenergytimesheet.databinding.DialogConfirmDeleteBinding;
import com.azcodes.sapuraenergytimesheet.databinding.DialogTimesheetEntryBinding;
import com.azcodes.sapuraenergytimesheet.model.TimesheetEntryModel;
import com.azcodes.sapuraenergytimesheet.utils.TimesheetStatusEnum;
import com.azcodes.sapuraenergytimesheet.view.home.adapter.DetailsAdapter;
import com.azcodes.sapuraenergytimesheet.view.home.adapter.TimesheetAdapter;
import com.azcodes.sapuraenergytimesheet.view.home.viewmodel.HomeViewModel.HomeEvent;
import com.azcodes.sapuraenergytimesheet.view.home.viewmodel.HomeViewModelImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class HomeActivity extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();

    private Dialog entryDialog, confirmDialog, statusDialog, detailsDialog;

    private ActivityHomeBinding binding;
    private DialogTimesheetEntryBinding entryBinding;
    private DialogConfirmDeleteBinding confirmBinding;
    private BottomSelectionStatusBinding statusBinding;
    private BottomSelectionDetailsBinding detailsBinding;

    private TimesheetAdapter adapter;
    private DetailsAdapter detailsAdapter;

    private HomeViewModelImpl viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(HomeViewModelImpl.class);

        initUI();
        setupViewModel();
    }

    private void initUI() {
        binding.btnSearch.setOnClickListener(view -> {
            viewModel.searchTimesheetEntry(binding.etTask.getText().toString().trim());
        });

        binding.btnCreate.setOnClickListener(view -> {
            showTimesheetEntryForm();
        });
    }

    private void setupViewModel() {
        viewModel.getTimesheetList();

        viewModel.getState().observe(this, homeEvent -> {
            if (homeEvent instanceof HomeEvent.OnDisplayTimesheetList) {
                HomeEvent.OnDisplayTimesheetList state = (HomeEvent.OnDisplayTimesheetList) homeEvent;
                displayTimesheet(state.getTimesheetList());

            } else if (homeEvent instanceof HomeEvent.OnFormError) {
                HomeEvent.OnFormError state = (HomeEvent.OnFormError) homeEvent;
                Toast.makeText(HomeActivity.this, state.getErrorMessage(), Toast.LENGTH_SHORT).show();

            } else if (homeEvent instanceof HomeEvent.OnCreateTimesheetEntry) {
                HomeEvent.OnCreateTimesheetEntry state = (HomeEvent.OnCreateTimesheetEntry) homeEvent;

                if (state.getSuccess()) {
                    Toast.makeText(HomeActivity.this, getString(R.string.dialog_create_success), Toast.LENGTH_SHORT).show();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(entryBinding.btnSubmit.getApplicationWindowToken(), 0);
                    entryDialog.dismiss();
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.dialog_create_fail), Toast.LENGTH_SHORT).show();
                }

            } else if (homeEvent instanceof HomeEvent.OnUpdateTimesheetEntry) {
                HomeEvent.OnUpdateTimesheetEntry state = (HomeEvent.OnUpdateTimesheetEntry) homeEvent;

                if (state.getSuccess()) {
                    Toast.makeText(HomeActivity.this, getString(R.string.dialog_update_success), Toast.LENGTH_SHORT).show();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(entryBinding.btnSubmit.getApplicationWindowToken(), 0);
                    entryDialog.dismiss();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.dialog_update_fail), Toast.LENGTH_SHORT).show();
                }

            } else if (homeEvent instanceof HomeEvent.OnDeleteTimesheetEntry) {
                HomeEvent.OnDeleteTimesheetEntry state = (HomeEvent.OnDeleteTimesheetEntry) homeEvent;

                if (state.getSuccess()) {
                    Toast.makeText(HomeActivity.this, getString(R.string.dialog_delete_success), Toast.LENGTH_SHORT).show();
                    confirmDialog.dismiss();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.dialog_delete_fail), Toast.LENGTH_SHORT).show();
                }

            } else if (homeEvent instanceof HomeEvent.OnDisplaySearchTimesheetList) {
                HomeEvent.OnDisplaySearchTimesheetList state = (HomeEvent.OnDisplaySearchTimesheetList) homeEvent;
                displayTimesheet(state.getTimesheetList());
            } else if (homeEvent instanceof HomeEvent.OnGetUserList) {
                HomeEvent.OnGetUserList state = (HomeEvent.OnGetUserList) homeEvent;
                diplayUser(state.getUserList());
            }
        });
    }

    private void displayTimesheet(List<TimesheetEntryModel> timesheetList) {
        adapter = new TimesheetAdapter(HomeActivity.this, timesheetList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false);
        binding.rvTimesheet.setLayoutManager(linearLayoutManager);
        binding.rvTimesheet.setAdapter(adapter);

        adapter.setOnClickListener(new TimesheetAdapter.OnClickListener() {
            @Override
            public void onEdit(TimesheetEntryModel entry) {
                onEditClick(entry);
            }

            @Override
            public void onDelete(TimesheetEntryModel entry) {
                onDeleteClick(entry);
            }
        });
    }

    private void diplayUser(List<String> userList) {
        detailsAdapter = new DetailsAdapter(HomeActivity.this, userList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false);
        detailsBinding.rvDetails.setLayoutManager(linearLayoutManager);
        detailsBinding.rvDetails.setAdapter(detailsAdapter);

        detailsAdapter.setOnClickListener(detail -> detailsBinding.etInput.setText(detail));
    }

    private void showTimesheetEntryForm() {
        entryDialog = new Dialog(HomeActivity.this);
        entryBinding = DialogTimesheetEntryBinding.inflate(getLayoutInflater());
        entryDialog.setContentView(entryBinding.getRoot());
        entryDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        entryDialog.setCancelable(true);
        entryDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        entryBinding.tvAssignedTo.setOnClickListener(details -> onAssignToClick());
        entryBinding.tvDateFrom.setOnClickListener(date -> onDateClick("dateFrom"));
        entryBinding.tvDateTo.setOnClickListener(date -> onDateClick("dateTo"));
        entryBinding.tvStatus.setOnClickListener(status -> onStatusClick());
        entryBinding.btnSubmit.setOnClickListener(submit -> validateForm());

        entryDialog.show();
    }

    private void onEditClick(TimesheetEntryModel entry) {
        entryDialog = new Dialog(HomeActivity.this);
        entryBinding = DialogTimesheetEntryBinding.inflate(getLayoutInflater());
        entryDialog.setContentView(entryBinding.getRoot());
        entryDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        entryDialog.setCancelable(true);
        entryDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        entryBinding.etProject.setText(entry.projectTitle);
        entryBinding.etTask.setText(entry.taskDescription);
        entryBinding.tvAssignedTo.setText(entry.assignedTo);
        entryBinding.tvDateFrom.setText(entry.dateFrom);
        entryBinding.tvDateTo.setText(entry.dateTo);
        entryBinding.tvStatus.setText(entry.status);

        entryBinding.tvAssignedTo.setOnClickListener(details -> onAssignToClick());
        entryBinding.tvDateFrom.setOnClickListener(date -> onDateClick("dateFrom"));
        entryBinding.tvDateTo.setOnClickListener(date -> onDateClick("dateTo"));
        entryBinding.tvStatus.setOnClickListener(status -> onStatusClick());
        entryBinding.btnSubmit.setOnClickListener(submit -> validateFormUpdate(entry));

        entryDialog.show();
    }

    private void onDeleteClick(TimesheetEntryModel entry) {
        confirmDialog = new Dialog(HomeActivity.this);
        confirmBinding = DialogConfirmDeleteBinding.inflate(getLayoutInflater());
        confirmDialog.setContentView(confirmBinding.getRoot());
        confirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        confirmDialog.setCancelable(true);
        confirmDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        confirmBinding.btnConfirm.setOnClickListener(confirm -> viewModel.deleteTimesheetEntry(entry));

        confirmBinding.btnCancel.setOnClickListener(cancel -> confirmDialog.dismiss());

        confirmDialog.show();
    }

    private void onStatusClick() {
        statusDialog = new Dialog(HomeActivity.this);
        statusBinding = BottomSelectionStatusBinding.inflate(getLayoutInflater());
        statusDialog.setContentView(statusBinding.getRoot());
        statusDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        statusDialog.setCancelable(true);
        statusDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        AtomicReference<String> selectedStatus = new AtomicReference<>(TimesheetStatusEnum.OPEN.status);

        statusBinding.tvStatusOpen.setOnClickListener(view -> {
            statusBinding.divider1.setVisibility(View.VISIBLE);
            statusBinding.divider2.setVisibility(View.VISIBLE);
            statusBinding.divider3.setVisibility(View.INVISIBLE);
            statusBinding.divider4.setVisibility(View.INVISIBLE);
            selectedStatus.set(TimesheetStatusEnum.OPEN.status);
        });

        statusBinding.tvStatusInProgress.setOnClickListener(view -> {
            statusBinding.divider1.setVisibility(View.INVISIBLE);
            statusBinding.divider2.setVisibility(View.VISIBLE);
            statusBinding.divider3.setVisibility(View.VISIBLE);
            statusBinding.divider4.setVisibility(View.INVISIBLE);
            selectedStatus.set(TimesheetStatusEnum.INPROGRESS.status);
        });

        statusBinding.tvStatusClosed.setOnClickListener(view -> {
            statusBinding.divider1.setVisibility(View.INVISIBLE);
            statusBinding.divider2.setVisibility(View.INVISIBLE);
            statusBinding.divider3.setVisibility(View.VISIBLE);
            statusBinding.divider4.setVisibility(View.VISIBLE);
            selectedStatus.set(TimesheetStatusEnum.CLOSED.status);
        });

        statusBinding.ivConfirm.setOnClickListener(view -> {
            statusDialog.dismiss();
            entryBinding.tvStatus.setText(selectedStatus.toString());
        });

        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    private void onAssignToClick() {
        detailsDialog = new Dialog(HomeActivity.this);
        detailsBinding = BottomSelectionDetailsBinding.inflate(getLayoutInflater());
        detailsDialog.setContentView(detailsBinding.getRoot());
        detailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        detailsDialog.setCancelable(true);
        detailsDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        viewModel.getUserList();

        detailsBinding.ivConfirm.setOnClickListener(view -> {
            String details = detailsBinding.etInput.getText().toString().trim();
            if (TextUtils.isEmpty(details)) {
                Toast.makeText(HomeActivity.this, getString(R.string.prompt_message_details), Toast.LENGTH_SHORT).show();
            } else {
                detailsDialog.dismiss();
                entryBinding.tvAssignedTo.setText(details);
            }
        });

        detailsDialog.setCancelable(false);
        detailsDialog.show();
    }

    private void onDateClick(String source) {
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);

            String myFormat = "dd-MM-yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);

            if (source.equals("dateFrom")) {
                entryBinding.tvDateFrom.setText(dateFormat.format(myCalendar.getTime()));
            } else {
                entryBinding.tvDateTo.setText(dateFormat.format(myCalendar.getTime()));
            }

        };

        new DatePickerDialog(HomeActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void validateForm() {

        TimesheetEntryModel entry = new TimesheetEntryModel(
                "",
                entryBinding.etProject.getText().toString().trim(),
                entryBinding.etTask.getText().toString().trim(),
                entryBinding.tvAssignedTo.getText().toString().trim(),
                entryBinding.tvDateFrom.getText().toString().trim(),
                entryBinding.tvDateTo.getText().toString().trim(),
                entryBinding.tvStatus.getText().toString().trim()
        );

        viewModel.createTimesheetEntry(entry);
    }

    private void validateFormUpdate(TimesheetEntryModel entry) {

        TimesheetEntryModel updatedEntry = new TimesheetEntryModel(
                entry.entryId,
                entryBinding.etProject.getText().toString().trim(),
                entryBinding.etTask.getText().toString().trim(),
                entryBinding.tvAssignedTo.getText().toString().trim(),
                entryBinding.tvDateFrom.getText().toString().trim(),
                entryBinding.tvDateTo.getText().toString().trim(),
                entryBinding.tvStatus.getText().toString().trim()
        );

        viewModel.updateTimesheetEntry(updatedEntry);
    }
}