package com.brasens.layout.controller;

import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.view.DashboardView;
import com.brasens.layout.utils.Controller;
import com.brasens.objects.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DashboardController extends Controller {
    DashboardView dashboardView;
    ApplicationWindow applicationWindow;

    public DashboardController(ApplicationWindow applicationWindow) {
        this.applicationWindow = applicationWindow;
    }

    @Override
    public void init() {
        dashboardView = applicationWindow.getViewManager().getDashboardView();
        delay = 1;
    }

    @Override
    public void close() {

    }

    @Override
    public void update() {

    }

    public void onTelemetryReceived(Telemetry data) {
        updateTelemetryDisplay();
    }

    private void updateTelemetryDisplay() {
        TelemetryDataManager dataManager = getApplicationWindow().getTelemetryDataManager();

        List<EngineTelemetry> engineHistory = dataManager.getEngineHistory();
        if (!engineHistory.isEmpty()) {
            EngineTelemetry latest = engineHistory.get(engineHistory.size() - 1);
            dashboardView.updateEngineDisplay(latest);
        }

        List<VRTelemetry> vrHistory = dataManager.getVrHistory();
        if (!vrHistory.isEmpty()) {
            VRTelemetry latest = vrHistory.get(vrHistory.size() - 1);
            dashboardView.updateVRDisplay(latest);
        }

        List<BatteryTelemetry> batteryHistory = dataManager.getBatteryHistory();
        if (!batteryHistory.isEmpty()) {
            BatteryTelemetry latest = batteryHistory.get(batteryHistory.size() - 1);
            dashboardView.updateBatteryDisplay(latest);
        }

        List<ADCTelemetry> adcHistory = dataManager.getAdcHistory();
        if (!adcHistory.isEmpty()) {
            ADCTelemetry latest = adcHistory.get(adcHistory.size() - 1);
            dashboardView.updateADCDisplay(latest);
        }

        List<CycleTelemetry> cycleHistory = dataManager.getCycleHistory();
        if (!cycleHistory.isEmpty()) {
            CycleTelemetry latest = cycleHistory.get(cycleHistory.size() - 1);
            dashboardView.updateCycleDisplay(latest);
        }
    }
}
