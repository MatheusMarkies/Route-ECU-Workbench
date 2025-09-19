package com.brasens.serialport;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SerialRunnable implements SerialPortPacketListener, Runnable {

    private final SerialPort port;

    //int oldSize = 0;
    //ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
/*
    void onClickInConnectButton(ActionEvent event) {
        try {
            serialReadder = new SerialReadder(choisebox_serialport.getValue());
            if(serialReadder.connect()) {
                System.out.println("Connect!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    public SerialRunnable(SerialPort port) {
        this.port = port;
    }

    @Override
    public int getPacketSize() {
        return SerialReadder.PACKET_SIZE_IN_BYTES;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void run() {
        port.addDataListener(this);
        //mouseTrapCarManager.getMainFrameController().getRotationSeries().getData().add(new XYChart.Data<String,Double>("0",0.));
    }

    enum ReadType{

    }

    ReadType readType = null;
    boolean getReadType = true;
    private final byte[] buffer = new byte[2048];

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;
        byte[] buffer = new byte[port.bytesAvailable()];

        String inputString = new String(buffer, StandardCharsets.UTF_16LE);

        Scanner scanner_stream=  new Scanner( port.getInputStream());
        while(scanner_stream.hasNextLine()) {
            String received_string = scanner_stream.nextLine();

            int received_str_len = received_string.length();
            inputString = received_string;

            System.out.println("Input data: " + inputString);

            try {


            }catch (Exception exception){//System.err.println(exception);

            }
        }

    }
    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }
}
