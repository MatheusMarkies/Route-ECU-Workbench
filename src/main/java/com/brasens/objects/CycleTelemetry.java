package com.brasens.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CycleTelemetry extends Telemetry {
    private CycleInfo cycle;

    @Data
    public static class CycleInfo {

        @JsonProperty("cyl_0")
        private CylinderInfo cylinderZero;

        @JsonProperty("cyl_1")
        private CylinderInfo cylinderOne;

        @JsonProperty("cyl_2")
        private CylinderInfo cylinderTwo;

        @JsonProperty("cyl_3")
        private CylinderInfo cylinderThree;
    }

    @Data
    public static class CylinderInfo {

        @JsonProperty("dwell_angle")
        private double dwellAngle;

        @JsonProperty("spark_angle")
        private double sparkAngle;
    }
}