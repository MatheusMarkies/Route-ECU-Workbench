package com.brasens.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class TelemetryDataManager {
    private final List<VRTelemetry> vrHistory = Collections.synchronizedList(new ArrayList<>());
    private final List<ADCTelemetry> adcHistory = Collections.synchronizedList(new ArrayList<>());
    private final List<BatteryTelemetry> batteryHistory = Collections.synchronizedList(new ArrayList<>());
    private final List<EngineTelemetry> engineHistory = Collections.synchronizedList(new ArrayList<>());

    private boolean inIgnitionTest = false;
    private boolean inInjectorTest = false;

    public TelemetryDataManager() {

    }

    public void addData(Telemetry data) {
        if (data instanceof VRTelemetry) vrHistory.add((VRTelemetry) data);
        else if (data instanceof ADCTelemetry) adcHistory.add((ADCTelemetry) data);
        else if (data instanceof BatteryTelemetry) batteryHistory.add((BatteryTelemetry) data);
        else if (data instanceof EngineTelemetry) engineHistory.add((EngineTelemetry) data);
    }

    public List<VRTelemetry> getVrHistory() { return new ArrayList<>(vrHistory); }
}
