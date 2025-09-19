package com.brasens.layout.controller;

import com.brasens.Config;
import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.view.DashboardView;
import com.brasens.utilities.common.enums.HttpStatusCode;
import com.brasens.utilities.common.enums.PriorityState;
import com.brasens.utilities.common.enums.WorkOrderState;
import com.brasens.utils.Controller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.brasens.Workbench.printNicerStackTrace;

public class DashboardController extends Controller {
    DashboardView dashboardView;
    ApplicationWindow applicationWindow;

    public DashboardController(ApplicationWindow applicationWindow) {
        this.applicationWindow = applicationWindow;
    }

    @Override
    public void init() {
        dashboardView = applicationWindow.getViewManager().getDashboardView();
    }

    @Override
    public void close() {

    }

    @Override
    public void update() {

    }

}
