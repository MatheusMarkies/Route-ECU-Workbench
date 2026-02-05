package com.brasens.serialport;

import com.brasens.layout.controller.DashboardController;
import com.brasens.layout.view.DashboardView;
import com.brasens.objects.SerialCommand;
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
import java.util.ArrayList;
import java.util.List;
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

    List<SerialCommand> commandsBuffer = new ArrayList<>();

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

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName("SerialWorker-" + serialPortName);
        thread.start();

        sendCommand(new SerialCommand("AT", 1000));
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
            handleRX(completeCommand);
        }
    }

    private void handleRX(String command) {
        boolean isAnswer = false;
        if(!commandsBuffer.isEmpty()){
            if(commandsBuffer.get(0).getAnswer().equals(command)){
                isAnswer = true;
                System.out.println();
                System.out.println("Resposta Recebida: " + command);

                if (commandsBuffer.get(0).getCallback() != null) {
                    javafx.application.Platform.runLater(commandsBuffer.get(0).getCallback());
                }

                commandsBuffer.remove(0);
            }
        }

        if(!isAnswer) {
            if (command.length() >= 2)
                directSend("OK");
            else return;

            System.out.println("Comando Recebido: " + command);

            if (command.trim().startsWith("{")) {
                Telemetry data = TelemetryParser.processJson(command);
                if (data != null) {
                    getDashboardView().getApplicationWindow().getTelemetryDataManager().addData(data);
                    getDashboardView().updateCalibrationChart();

                    javafx.application.Platform.runLater(() -> {
                        DashboardController controller = (DashboardController) getDashboardView().getController();
                        controller.onTelemetryReceived(data);
                    });
                }
            }
        }

        javafx.application.Platform.runLater(() -> {

        });
    }

    public boolean directSend(String command) {
        return sendStringWithFlush(command + "\r");
    }

    public boolean sendCommand(SerialCommand command) {
        commandsBuffer.add(command);
        return sendStringWithFlush(command.getCommand() + "\r");
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
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.addDataListener(this);
        }

        System.out.println("Thread Serial Iniciada para: " + serialPortName);

        while (isConnected() && !Thread.currentThread().isInterrupted()) {
            try {
                if (!commandsBuffer.isEmpty()) {
                    SerialCommand first = commandsBuffer.get(0);
                    long rtt = System.currentTimeMillis() - first.getTimestamp();

                    if (rtt > first.getTimeout()) {
                        System.out.println("Timeout detectado para: " + first.getCommand());
                        sendCommand(first);
                        commandsBuffer.remove(0);
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Thread Serial interrompida.");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Erro no loop da serial: " + e.getMessage());
            }
        }

        System.out.println("Thread Serial finalizada.");
    }
}
