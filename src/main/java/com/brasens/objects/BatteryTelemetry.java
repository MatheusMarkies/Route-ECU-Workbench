package com.brasens.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BatteryTelemetry extends Telemetry {
    private BatteryInfo battery;

    @Data
    public static class BatteryInfo {
        private double voltage;

        @JsonProperty("raw_adc")
        private int rawAdc;

        @JsonProperty("linear_cte")
        private int linearCte;

        private Calibration lvL;
        private Calibration lvH;
    }

    @Data
    public static class Calibration {
        private double voltage;

        @JsonProperty("raw_adc")
        private int rawAdc;
    }
}