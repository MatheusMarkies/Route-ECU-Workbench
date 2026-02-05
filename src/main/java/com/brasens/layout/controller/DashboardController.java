package com.brasens.layout.controller;

import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.view.DashboardView;
import com.brasens.layout.utils.Controller;

public class DashboardController extends Controller {
    DashboardView dashboardView;
    ApplicationWindow applicationWindow;

    public DashboardController(ApplicationWindow applicationWindow) {
        this.applicationWindow = applicationWindow;
    }

    @Override
    public void init() {
        dashboardView = applicationWindow.getViewManager().getDashboardView();
        delay = 1000;
    }

    @Override
    public void close() {

    }

    @Override
    public void update() {

    }

}
