package com.brasens.serialport;

import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SerialManager{

    public static SerialPort[] getSerialPortList(){
        return SerialPort.getCommPorts();
    }

    public static List<String> getSerialPortNames(){
        List<String> names = new ArrayList<String>();
        SerialPort[] serialPortList = getSerialPortList();

        for (SerialPort port: serialPortList) {
            names.add(port.getSystemPortName());
        }

        return names;
    }

}