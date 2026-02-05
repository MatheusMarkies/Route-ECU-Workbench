package com.brasens.layout.controller;

import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.view.DashboardView;
import com.brasens.layout.utils.Controller;
import com.brasens.objects.ADCTelemetry;
import com.brasens.objects.Telemetry;
import com.brasens.objects.VRTelemetry;

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

    public void onTelemetryReceived(Telemetry data) {
        if (data instanceof VRTelemetry vr) {

        }
        else if (data instanceof ADCTelemetry adc) {
            // Processa sensores ADC
            adc.getAdcU16().forEach(sensor -> {

            });
        }
    }

}
