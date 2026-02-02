package com.brasens.serialport;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SerialRunnable implements SerialPortDataListener, Runnable {

    private final SerialPort port;
    private final StringBuilder rxBuffer = new StringBuilder();

    public SerialRunnable(SerialPort port) {
        this.port = port;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        byte[] newData = new byte[port.bytesAvailable()];
        int numRead = port.readBytes(newData, newData.length);

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

        /*
        javafx.application.Platform.runLater(() -> {

        });
        */
    }

    @Override
    public void run() {
        // Registar este listener na porta
        port.addDataListener(this);
    }
}
