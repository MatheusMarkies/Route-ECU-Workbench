package com.brasens.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Telemetry {
    private final long timestamp = System.currentTimeMillis();
}
