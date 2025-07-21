package com.example.internshipapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InternshipAdapter extends RecyclerView.Adapter<InternshipAdapter.ViewHolder> {
    private List<Internship> internships;

    public interface OnItemClickListener {
        void onItemClick(Internship internship);
    }

    private OnItemClickListener listener;

    public InternshipAdapter(List<Internship> internships, OnItemClickListener listener) {
        this.internships = internships;
        this.listener = listener;
    }

    public void setData(List<Internship> list) {
        this.internships = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_internship, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Internship internship = internships.get(position);
        holder.tvTitle.setText(internship.getTitle());
        holder.tvCompany.setText(internship.getCompany());
        holder.tvLocation.setText(internship.getLocation());
        holder.tvDuration.setText(internship.getDuration());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(internship));
    }

    @Override
    public int getItemCount() {
        return internships.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCompany, tvLocation, tvDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }
    }
}
