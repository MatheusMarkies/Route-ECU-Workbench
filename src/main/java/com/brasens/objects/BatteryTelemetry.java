package com.brasens.objects;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BatteryTelemetry extends Telemetry{
    private BatteryInfo battery;

    @Data
    public static class BatteryInfo {
        private double voltage;
    }
}
