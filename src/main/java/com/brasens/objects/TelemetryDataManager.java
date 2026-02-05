package com.brasens.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class TelemetryDataManager {
    private final List<VRTelemetry> vrHistory = Collections.synchronizedList(new ArrayList<>());
    private final List<ADCTelemetry> adcHistory = Collections.synchronizedList(new ArrayList<>());
    private final List<BatteryTelemetry> batteryHistory = Collections.synchronizedList(new ArrayList<>());
    private final List<EngineTelemetry> engineHistory = Collections.synchronizedList(new ArrayList<>());

    private boolean inIgnitionTest = false;
    private boolean inInjectorTest = false;

    private boolean batteryStep1Done = false;
    private boolean batteryStep2Done = false;

    private BatteryTelemetry latestBatteryTelemetry;

    public TelemetryDataManager() {

    }

    public void addData(Telemetry data) {
        if (data instanceof VRTelemetry) vrHistory.add((VRTelemetry) data);
        else if (data instanceof ADCTelemetry) adcHistory.add((ADCTelemetry) data);
        else if (data instanceof BatteryTelemetry) {
            batteryHistory.add((BatteryTelemetry) data);
            latestBatteryTelemetry = (BatteryTelemetry) data;
        }
        else if (data instanceof EngineTelemetry) engineHistory.add((EngineTelemetry) data);
    }

    public List<VRTelemetry> getVrHistory() {
        return new ArrayList<>(vrHistory);
    }

    // Métodos auxiliares para obter valores de calibração
    public double getBatteryLvLVoltage() {
        if (latestBatteryTelemetry != null &&
                latestBatteryTelemetry.getBattery() != null &&
                latestBatteryTelemetry.getBattery().getLvL() != null) {
            return latestBatteryTelemetry.getBattery().getLvL().getVoltage();
        }
        return 0.0;
    }

    public int getBatteryLvLRawAdc() {
        if (latestBatteryTelemetry != null &&
                latestBatteryTelemetry.getBattery() != null &&
                latestBatteryTelemetry.getBattery().getLvL() != null) {
            return latestBatteryTelemetry.getBattery().getLvL().getRawAdc();
        }
        return 0;
    }

    public double getBatteryLvHVoltage() {
        if (latestBatteryTelemetry != null &&
                latestBatteryTelemetry.getBattery() != null &&
                latestBatteryTelemetry.getBattery().getLvH() != null) {
            return latestBatteryTelemetry.getBattery().getLvH().getVoltage();
        }
        return 0.0;
    }

    public int getBatteryLvHRawAdc() {
        if (latestBatteryTelemetry != null &&
                latestBatteryTelemetry.getBattery() != null &&
                latestBatteryTelemetry.getBattery().getLvH() != null) {
            return latestBatteryTelemetry.getBattery().getLvH().getRawAdc();
        }
        return 0;
    }

    public double getBatteryCurrentVoltage() {
        if (latestBatteryTelemetry != null &&
                latestBatteryTelemetry.getBattery() != null) {
            return latestBatteryTelemetry.getBattery().getVoltage();
        }
        return 0.0;
    }

    public int getBatteryCurrentRawAdc() {
        if (latestBatteryTelemetry != null &&
                latestBatteryTelemetry.getBattery() != null) {
            return latestBatteryTelemetry.getBattery().getRawAdc();
        }
        return 0;
    }

    public int getBatteryLinearCte() {
        if (latestBatteryTelemetry != null &&
                latestBatteryTelemetry.getBattery() != null) {
            return latestBatteryTelemetry.getBattery().getLinearCte();
        }
        return 0;
    }
}