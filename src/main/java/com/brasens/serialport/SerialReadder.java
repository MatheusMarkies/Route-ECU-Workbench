package com.brasens.serialport;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;

public class SerialReadder{
    private SerialPort serialPort;
    private String serialPortName;

    static int PORT_RATE = 115200;
    public static int PACKET_SIZE_IN_BYTES = 8;

    public SerialReadder(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    public SerialReadder(){
        super();
    }

    public boolean connect(){
        SerialPort[] serialPorts = SerialManager.getSerialPortList();

        for (SerialPort port: serialPorts)
            if(port.getDescriptivePortName().equals(serialPortName)) {
                serialPort = port;
                break;
            }

        if (serialPort.isOpen())
            return false;
        else {
            serialPort.openPort();

            serialPort.setBaudRate(SerialReadder.PORT_RATE);

            Platform.runLater(
                    new SerialRunnable(serialPort)
                );

            return serialPort.isOpen();
        }
    }

    public synchronized void close(){
        if(serialPort.isOpen())
            serialPort.closePort();
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

}

