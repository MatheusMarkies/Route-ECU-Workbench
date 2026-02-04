package com.brasens.serialport;

import com.fazecast.jSerialComm.SerialPort;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SerialReader {
    private SerialPort serialPort;
    private String serialPortName;

    public static final int BAUD_RATE = 115200;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
    public static final int PARITY = SerialPort.NO_PARITY;
    public static final int FLOW_CONTROL = SerialPort.FLOW_CONTROL_DISABLED;

    public static final int READ_TIMEOUT = 100;
    public static final int WRITE_TIMEOUT = 100;

    public static final int PACKET_SIZE_IN_BYTES = 8;

    private SerialRunnable runnable;

    public SerialReader(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    public SerialReader() {
        super();
    }

    public void connect() {
        SerialPort[] serialPorts = SerialManager.getSerialPortList();

        for (SerialPort port : serialPorts) {
            if (port.getSystemPortName().equals(serialPortName)) {
                serialPort = port;
                break;
            }
        }

        if (serialPort == null) {
            System.out.println("Erro: Porta serial '" + serialPortName + "' não encontrada!");
            return;
        }

        if (serialPort.isOpen()) {
            System.out.println("Aviso: Porta serial '" + serialPortName + "' já está aberta");
            return;
        }

        if (!serialPort.openPort()) {
            System.out.println("Erro: Falha ao abrir a porta serial '" + serialPortName + "'");
            return;
        }


        serialPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 100);

        serialPort.setDTR();
        serialPort.setRTS();

        System.out.println("Sucesso: Porta serial '" + serialPortName + "' aberta e configurada");

        runnable = new SerialRunnable(serialPort);
        runnable.run();
    }

    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    public void reconnect() {
        serialPort.closePort();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        connect();
    }
}