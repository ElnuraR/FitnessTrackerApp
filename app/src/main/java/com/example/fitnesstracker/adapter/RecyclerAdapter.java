package com.example.fitnesstracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.db.Route;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    List<Route> routes = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_route, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route currentRoute = routes.get(position);

        try {
            holder.textView_length.setText(String.valueOf(currentRoute.getLength()));
            holder.textView_speed.setText(String.valueOf(currentRoute.getSpeed()));
            holder.textView_time.setText(String.valueOf(currentRoute.getTime()));
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public void setRoutesArrayList(List<Route> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView_length;
        private TextView textView_time;
        private TextView textView_speed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_length = itemView.findViewById(R.id.list_runLength);
            textView_speed = itemView.findViewById(R.id.list_runSpeed);
            textView_time = itemView.findViewById(R.id.list_runTime);
        }
    }
}
