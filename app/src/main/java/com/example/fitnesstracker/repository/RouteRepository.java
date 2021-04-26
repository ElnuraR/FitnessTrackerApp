package com.example.fitnesstracker.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.fitnesstracker.db.Route;
import com.example.fitnesstracker.db.RouteDao;
import com.example.fitnesstracker.db.RouteDatabase;

import java.util.List;

public class RouteRepository {
    private RouteDao routeDao;
    private LiveData<List<Route>> allRoutes;

    public RouteRepository(Application application) {
        RouteDatabase routesDatabase = RouteDatabase.getInstance(application);
        routeDao = routesDatabase.routeDao();
        allRoutes = routeDao.getAllRoutes();
    }

    public void insert(Route route) {
        new InsertRoutesAsyncTsk(routeDao).execute(route);
    }

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }

    private static class InsertRoutesAsyncTsk extends AsyncTask<Route, Void, Void> {

        private RouteDao routesDao;

        public InsertRoutesAsyncTsk(RouteDao routesDao) {
            this.routesDao = routesDao;
        }

        @Override
        protected Void doInBackground(Route... routes) {
            routesDao.insert(routes[0]);
            return null;
        }
    }
}
