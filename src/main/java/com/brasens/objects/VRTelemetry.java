package com.brasens.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VRTelemetry extends Telemetry{
    private SensorData ckp;
    private SensorData cmp;

    @Data
    public static class SensorData {
        private double rpm;
        private double frequence;
        private int pulses;
        private int revolutions;
        private double period;
    }
}
