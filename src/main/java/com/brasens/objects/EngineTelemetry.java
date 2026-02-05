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

        public enum PistonPhase {
            POWER, EXHAUST, COMPRESSION, INTAKE
        }

        private ShaftInfo crankshaft;
        private ShaftInfo camshaft;

        @JsonProperty("piston_one")
        private PistonPhase pistonOne;

        @JsonProperty("piston_two")
        private PistonPhase pistonTwo;

        @JsonProperty("piston_three")
        private PistonPhase pistonThree;

        @JsonProperty("piston_four")
        private PistonPhase pistonFour;
    }

    @Data
    public static class ShaftInfo {
        private double angularvelocity;
        private double angle;
    }
}
