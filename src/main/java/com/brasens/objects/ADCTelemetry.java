package com.brasens.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ADCTelemetry extends Telemetry{
    @JsonProperty("adc_u16")
    private List<AdcSensor> adcU16;

    @JsonProperty("adc_u17")
    private List<AdcSensor> adcU17;

    @Data
    public static class AdcSensor {
        private String sensor;
        private int raw;
        private double voltage;
    }
}