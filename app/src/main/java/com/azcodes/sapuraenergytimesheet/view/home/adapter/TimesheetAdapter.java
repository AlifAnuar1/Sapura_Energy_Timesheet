package com.azcodes.sapuraenergytimesheet.view.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azcodes.sapuraenergytimesheet.R;
import com.azcodes.sapuraenergytimesheet.model.TimesheetEntryModel;
import com.azcodes.sapuraenergytimesheet.utils.TimesheetStatusEnum;

import java.util.List;

public class TimesheetAdapter extends RecyclerView.Adapter<TimesheetViewHolder> {

    private final Context context;
    private final List<TimesheetEntryModel> timesheetList;

    private OnClickListener onClickListener;


    public TimesheetAdapter(Context context, List<TimesheetEntryModel> timesheetList) {
        this.context = context;
        this.timesheetList = timesheetList;
    }

    @NonNull
    @Override
    public TimesheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_timesheet, parent, false);
        return new TimesheetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimesheetViewHolder holder, int position) {
        holder.tvProject.setText(timesheetList.get(position).projectTitle);
        holder.tvTask.setText(timesheetList.get(position).taskDescription);
        holder.tvAssignedTo.setText(timesheetList.get(position).assignedTo);
        holder.tvDateFrom.setText(timesheetList.get(position).dateFrom);
        holder.tvDateTo.setText(timesheetList.get(position).dateTo);

        holder.tvStatus.setText(timesheetList.get(position).status);
        if (timesheetList.get(position).status.equals(TimesheetStatusEnum.OPEN.status)) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.grey));
        } else if (timesheetList.get(position).status.equals(TimesheetStatusEnum.INPROGRESS.status)) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (timesheetList.get(position).status.equals(TimesheetStatusEnum.CLOSED.status)) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        }


        holder.ivEdit.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onEdit(timesheetList.get(position));
            }
        });

        holder.ivDelete.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onDelete(timesheetList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return timesheetList.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {

        void onEdit(TimesheetEntryModel entry);

        void onDelete(TimesheetEntryModel entry);


    }
}

class TimesheetViewHolder extends RecyclerView.ViewHolder {

    TextView tvProject, tvTask, tvAssignedTo, tvDateFrom, tvDateTo, tvStatus;
    ImageView ivEdit, ivDelete;

    public TimesheetViewHolder(@NonNull View itemView) {
        super(itemView);

        tvProject = itemView.findViewById(R.id.tv_project);
        tvTask = itemView.findViewById(R.id.tv_task);
        tvAssignedTo = itemView.findViewById(R.id.tv_assignedTo);
        tvDateFrom = itemView.findViewById(R.id.tv_from);
        tvDateTo = itemView.findViewById(R.id.tv_to);
        tvStatus = itemView.findViewById(R.id.tv_status);
        ivEdit = itemView.findViewById(R.id.iv_edit);
        ivDelete = itemView.findViewById(R.id.iv_delete);
    }
}
