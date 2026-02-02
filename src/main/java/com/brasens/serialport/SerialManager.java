package com.brasens.serialport;

import com.brasens.objects.SerialPorts;
import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SerialManager{

    public static SerialPort[] getSerialPortList(){
        return SerialPort.getCommPorts();
    }

    public static List<SerialPorts> getSerialPortNames(){
        List<SerialPorts> names = new ArrayList<SerialPorts>();
        SerialPort[] serialPortList = getSerialPortList();

        for (SerialPort port: serialPortList) {
            SerialPorts newPort = new SerialPorts();

            newPort.setSystemName(port.getSystemPortName());
            newPort.setDeviceName(port.getDescriptivePortName());

            names.add(newPort);
        }

        return names;
    }

}