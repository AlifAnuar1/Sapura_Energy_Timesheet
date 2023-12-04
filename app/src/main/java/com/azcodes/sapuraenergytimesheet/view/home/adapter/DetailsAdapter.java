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

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsViewHolder> {

    private final Context context;
    private final List<String> detailsList;

    private OnClickListener onClickListener;

    public DetailsAdapter(Context context, List<String> detailsList) {
        this.context = context;
        this.detailsList = detailsList;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_details, parent, false);
        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        holder.tvDetail.setText(detailsList.get(position));

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onDetailClick(detailsList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {

        void onDetailClick(String detail);
    }
}

class DetailsViewHolder extends RecyclerView.ViewHolder {

    TextView tvDetail;

    public DetailsViewHolder(@NonNull View itemView) {
        super(itemView);

        tvDetail = itemView.findViewById(R.id.tv_detail);
    }
}