package com.example.fitnesstracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fitnesstracker.db.Route;
import com.example.fitnesstracker.repository.RouteRepository;

import java.util.List;

public class RouteViewModel extends AndroidViewModel {

    private RouteRepository routesRepository;
    private LiveData<List<Route>> allRoutes;

    public RouteViewModel(@NonNull Application application) {
        super(application);
        routesRepository = new RouteRepository(application);
        allRoutes = routesRepository.getAllRoutes();
    }

    public void insert(Route routes) {
        routesRepository.insert(routes);
    }

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }
}
