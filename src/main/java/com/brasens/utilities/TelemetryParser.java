package com.brasens.utilities;

import com.brasens.objects.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TelemetryParser {
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static Telemetry processJson(String json) {
        if (json == null || json.trim().isEmpty() || !json.trim().startsWith("{")) {
            return null;
        }

        Telemetry data = null;

        try {
            if (json.contains("\"ckp\"")) {
                data = mapper.readValue(json, VRTelemetry.class);

            } else if (json.contains("\"adc_u16\"")) {
                data = mapper.readValue(json, ADCTelemetry.class);

            } else if (json.contains("\"battery\"")) {
                data = mapper.readValue(json, BatteryTelemetry.class);

            } else if (json.contains("\"engine\"")) {
                data = mapper.readValue(json, EngineTelemetry.class);
            }

        } catch (Exception e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }
}
