package com.brasens.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SerialPorts {
    private String systemName;
    private String deviceName;

    @Override
    public String toString() {
        return systemName + " " + deviceName;
    }
}
