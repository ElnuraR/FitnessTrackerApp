package com.example.fitnesstracker.fragments;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnesstracker.R;
import com.example.fitnesstracker.adapter.RecyclerAdapter;
import com.example.fitnesstracker.db.Route;
import com.example.fitnesstracker.viewModel.RouteViewModel;

import java.util.List;

public class HistoryFragment extends Fragment {

    RecyclerAdapter adapter;
    private RouteViewModel routeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);

        routeViewModel = new ViewModelProvider(this).get(RouteViewModel.class);
        routeViewModel.getAllRoutes().observe(getActivity(), new Observer<List<Route>>() {
            @Override
            public void onChanged(List<Route> routes) {
                adapter.setRoutesArrayList(routes);
            }
        });

        return view;
    }
}