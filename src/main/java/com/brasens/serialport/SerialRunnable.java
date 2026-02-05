package com.brasens.serialport;

import com.brasens.layout.view.DashboardView;
import com.brasens.objects.Telemetry;
import com.brasens.utilities.TelemetryParser;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Getter @Setter @AllArgsConstructor
public class SerialRunnable implements SerialPortDataListener, Runnable {

    private SerialPort serialPort;
    private final StringBuilder rxBuffer = new StringBuilder();

    private String serialPortName;

    private DashboardView dashboardView;

    public static final int BAUD_RATE = 115200;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = SerialPort.ONE_STOP_BIT;
    public static final int PARITY = SerialPort.NO_PARITY;
    public static final int FLOW_CONTROL = SerialPort.FLOW_CONTROL_DISABLED;

    public static final int READ_TIMEOUT = 100;
    public static final int WRITE_TIMEOUT = 100;

    public static final int PACKET_SIZE_IN_BYTES = 8;

    public SerialRunnable() {

    }

    public SerialRunnable(String serialPortName, DashboardView dashboardView) {
        this.serialPortName = serialPortName;
        this.dashboardView = dashboardView;
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

        this.run();
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

    public void close() {
        serialPort.closePort();
    }

    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        byte[] newData = new byte[serialPort.bytesAvailable()];
        int numRead = serialPort.readBytes(newData, newData.length);

        if (numRead > 0) {
            String textChunk = new String(newData, StandardCharsets.US_ASCII);

            synchronized (rxBuffer) {
                rxBuffer.append(textChunk);
                processBuffer();
            }
        }
    }

    private void processBuffer() {
        int terminatorIndex;
        while ((terminatorIndex = rxBuffer.indexOf("\r")) != -1) {
            String completeCommand = rxBuffer.substring(0, terminatorIndex);

            rxBuffer.delete(0, terminatorIndex + 1);
            handleCommand(completeCommand);
        }
    }

    private void handleCommand(String command) {
        System.out.println("Comando Recebido: " + command);
        sendCommand("OK");

        if (command.trim().startsWith("{")) {
            Telemetry data = TelemetryParser.processJson(command);

            javafx.application.Platform.runLater(() -> {

            });
        }

        javafx.application.Platform.runLater(() -> {

        });
    }

    public boolean sendCommand(String command) {
        return sendStringWithFlush(command + "\r");
    }

    public boolean sendStringWithFlush(String data) {
        if (serialPort == null || !serialPort.isOpen()) {
            return false;
        }

        try {
            System.out.println(">> Enviando: "+data);
            byte[] bytes = data.getBytes();
            int bytesSent = serialPort.writeBytes(bytes, bytes.length);

            serialPort.flushIOBuffers();

            return (bytesSent == bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        serialPort.addDataListener(this);
    }
}
