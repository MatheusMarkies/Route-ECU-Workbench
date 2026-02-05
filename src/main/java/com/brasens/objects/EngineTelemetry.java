package com.brasens.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EngineTelemetry extends Telemetry{
    private EngineInfo engine;

    @Data
    public static class EngineInfo {
        private ShaftInfo crankshaft;
        private ShaftInfo camshaft;

        @JsonProperty("piston_one")
        private String pistonOne;

        @JsonProperty("piston_two")
        private String pistonTwo;

        @JsonProperty("piston_three")
        private String pistonThree;

        @JsonProperty("piston_four")
        private String pistonFour;
    }

    @Data
    public static class ShaftInfo {
        private double angularvelocity;
        private double angle;
    }
}
