package com.brasens.layout.view;

import com.brasens.NetworkManager;
import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.components.CustomButton;
import com.brasens.layout.controller.DashboardController;
import com.brasens.objects.SerialCommand;
import com.brasens.objects.SerialPorts;
import com.brasens.objects.TelemetryDataManager;
import com.brasens.serialport.SerialManager;
import com.brasens.layout.utils.Page;
import com.brasens.serialport.SerialRunnable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardView extends Page {

    private CustomButton connectButton;
    private CustomButton injectorTestButton;
    private CustomButton ignitionTestButton;
    private CustomButton batteryCalibrationButton;

    private SerialRunnable serialRunnable = new SerialRunnable();

    public DashboardView(ApplicationWindow applicationWindow, NetworkManager networkManager) {

        super(applicationWindow, networkManager, "/mspm/pages/DashboardCSS.css");
        this.controller = new DashboardController(applicationWindow);
        createView();
    }

    public void createView() {
        getStyleClass().add("body");
        setMinHeight(1100);

        AnchorPane contentAnchorPane = new AnchorPane();
        contentAnchorPane.setMaxHeight(contentAnchorPane.getPrefHeight());
        contentAnchorPane.getStyleClass().add("body");

        AnchorPane.setBottomAnchor(contentAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(contentAnchorPane, 0.0);
        AnchorPane.setRightAnchor(contentAnchorPane, 0.0);
        AnchorPane.setTopAnchor(contentAnchorPane, 0.0);

        String buttonStyle = "-fx-background-color: #ffffff; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);";
        double buttonWidth = 250;
        double buttonHeight = 60;
        double spacing = 20; // Espaço entre os botões
        double startX = 40.0; // Posição inicial X

        Image connectIcon = new Image(getClass().getResourceAsStream("/mspm/icons/no-connection.png"));
        connectButton = new CustomButton("Conectar Dispositivo", connectIcon, buttonStyle, 32);
        connectButton.setPrefWidth(buttonWidth);
        connectButton.setPrefHeight(buttonHeight);
        connectButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);

        AnchorPane.setTopAnchor(connectButton, 40.0);
        AnchorPane.setLeftAnchor(connectButton, startX);

        connectButton.setOnMouseClicked(event -> openDeviceSelectionPopup());

        Image injectorIcon = new Image(getClass().getResourceAsStream("/mspm/icons/no-connection.png"));
        injectorTestButton = new CustomButton("Teste de Injetores", injectorIcon, buttonStyle, 32);
        injectorTestButton.setPrefWidth(buttonWidth);
        injectorTestButton.setPrefHeight(buttonHeight);
        injectorTestButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);

        AnchorPane.setTopAnchor(injectorTestButton, 40.0);
        AnchorPane.setLeftAnchor(injectorTestButton, startX + buttonWidth + spacing);

        injectorTestButton.setOnMouseClicked(event -> {
            if(getSerialRunnable().isConnected())
                getSerialRunnable().sendCommand(new SerialCommand("AT+INJTEST", "OK", ()->{
                    getApplicationWindow().getTelemetryDataManager().setInInjectorTest(!getApplicationWindow().getTelemetryDataManager().isInInjectorTest());
                    if(getApplicationWindow().getTelemetryDataManager().isInInjectorTest())
                        System.out.println("Iniciando Teste de Injetores...");
                    else System.out.println("Finalizando Teste de Injetores...");
                }, 100));
        });

        Image ignitionIcon = new Image(getClass().getResourceAsStream("/mspm/icons/no-connection.png"));
        ignitionTestButton = new CustomButton("Teste de Ignição", ignitionIcon, buttonStyle, 32);
        ignitionTestButton.setPrefWidth(buttonWidth);
        ignitionTestButton.setPrefHeight(buttonHeight);
        ignitionTestButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);

        // Posicionamento (Ao lado do injectorTestButton)
        AnchorPane.setTopAnchor(ignitionTestButton, 40.0);
        AnchorPane.setLeftAnchor(ignitionTestButton, startX + (buttonWidth + spacing) * 2);

        ignitionTestButton.setOnMouseClicked(event -> {
            if(getSerialRunnable().isConnected())
                getSerialRunnable().sendCommand(new SerialCommand("AT+IGNTEST", "OK", ()->{
                    getApplicationWindow().getTelemetryDataManager().setInIgnitionTest(!getApplicationWindow().getTelemetryDataManager().isInIgnitionTest());
                    if(getApplicationWindow().getTelemetryDataManager().isInIgnitionTest())
                        System.out.println("Iniciando Teste de Ignição...");
                    else System.out.println("Finalizando Teste de Ignição...");
                }, 100));
        });

        Image batteryIcon = new Image(getClass().getResourceAsStream("/mspm/icons/coins.png")); // Use o ícone que preferir
        batteryCalibrationButton = new CustomButton("Calibrar Bateria", batteryIcon, buttonStyle, 32);
        batteryCalibrationButton.setPrefWidth(buttonWidth);
        batteryCalibrationButton.setPrefHeight(buttonHeight);
        batteryCalibrationButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);

        AnchorPane.setTopAnchor(batteryCalibrationButton, 40.0);
        AnchorPane.setLeftAnchor(batteryCalibrationButton, startX + (buttonWidth + spacing) * 3);

        batteryCalibrationButton.setOnMouseClicked(event -> openBatteryCalibrationPopup());

        contentAnchorPane.getChildren().addAll(connectButton, injectorTestButton, ignitionTestButton, batteryCalibrationButton);
        getChildren().add(contentAnchorPane);
    }

    private void openDeviceSelectionPopup() {
        Stage popupStage = new Stage();
        popupStage.initOwner(getApplicationWindow().getWorkbench().getStage());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Conexão Serial");

        VBox popupLayout = new VBox(20);
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.setPadding(new Insets(30));
        popupLayout.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 1px;");

        Label titleLabel = new Label("Selecione a Porta COM");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        ComboBox<SerialPorts> portComboBox = new ComboBox<>();
        portComboBox.setPrefWidth(200);
        portComboBox.setPromptText("Procurando portas...");

        Button refreshBtn = new Button("Atualizar Lista");
        refreshBtn.setOnAction(e -> updatePortList(portComboBox));

        updatePortList(portComboBox);

        Button connectBtn = new Button("CONECTAR");
        connectBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        connectBtn.setPrefWidth(200);
        connectBtn.setPrefHeight(40);

        connectBtn.setOnAction(e -> {
            SerialPorts selected = portComboBox.getValue(); // Agora pegamos o objeto
            if (selected != null) {
                System.out.println("Conectando em: " + selected.getSystemName());

                serialRunnable = new SerialRunnable(selected.getSystemName(),this);
                serialRunnable.connect();

                popupStage.close();
            } else {
                System.out.println("Nenhuma porta selecionada!");
            }
        });

        popupLayout.getChildren().addAll(titleLabel, portComboBox, refreshBtn, connectBtn);

        Scene scene = new Scene(popupLayout, 400, 300);
        popupStage.setScene(scene);
        popupStage.show();
    }

    private void openBatteryCalibrationPopup() {
        Stage popupStage = new Stage();
        popupStage.initOwner(getApplicationWindow().getWorkbench().getStage());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Calibração da Bateria");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f4f4f4;");

        HBox hbStep1 = new HBox(10);
        hbStep1.setAlignment(Pos.CENTER_LEFT);

        Button btnCalib0V = new Button("1. Calibrar 0V (Desconectado)");
        btnCalib0V.setPrefWidth(250);

        Label check1 = new Label("✅");
        check1.setStyle("-fx-font-size: 20px;");
        check1.setVisible(getApplicationWindow().getTelemetryDataManager().isBatteryStep1Done());

        btnCalib0V.setOnAction(e -> {
            getSerialRunnable().sendCommand(new SerialCommand("AT+BATCAL0", "OK", () -> {
                getApplicationWindow().getTelemetryDataManager().setBatteryStep1Done(true);
                check1.setVisible(true);
                System.out.println("Passo 1 concluído!");
            }, 100));
        });
        hbStep1.getChildren().addAll(btnCalib0V, check1);

        HBox hbStep2 = new HBox(10);
        hbStep2.setAlignment(Pos.CENTER_LEFT);

        TextField tfVoltage = new TextField("12.6");
        tfVoltage.setPrefWidth(80);

        Button btnCalibValue = new Button("2. Definir Tensão Atual");
        btnCalibValue.setPrefWidth(160);

        Label check2 = new Label("✅");
        check2.setStyle("-fx-font-size: 20px;");
        check2.setVisible(getApplicationWindow().getTelemetryDataManager().isBatteryStep2Done());

        btnCalibValue.setOnAction(e -> {
            String val = tfVoltage.getText().replace(",", ".");
            getSerialRunnable().sendCommand(new SerialCommand("AT+BATCALV=" + val, "OK", () -> {
                getApplicationWindow().getTelemetryDataManager().setBatteryStep2Done(true);
                check2.setVisible(true);

                updateCalibrationChart();
                System.out.println("Passo 2 concluído!");
            }, 500));
        });
        hbStep2.getChildren().addAll(tfVoltage, btnCalibValue, check2);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Valor RAW (ADC)");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Tensão (V)");

        this.lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Reta de Calibração");
        lineChart.setCreateSymbols(true);
        lineChart.setPrefHeight(300);
        lineChart.setAnimated(false);

        this.calibrationSeries = new XYChart.Series<>();
        this.calibrationSeries.setName("V = a * RAW + b");
        lineChart.getData().add(calibrationSeries);

        layout.getChildren().addAll(new Label("Progresso da Calibração"), hbStep1, hbStep2, lineChart);

        Scene scene = new Scene(layout, 550, 650);
        popupStage.setScene(scene);
        popupStage.show();

        updateCalibrationChart();
    }

    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> calibrationSeries;

    public void updateCalibrationChart() {
        if (calibrationSeries == null) return;

        javafx.application.Platform.runLater(() -> {
            calibrationSeries.getData().clear();

            TelemetryDataManager dataManager = getApplicationWindow().getTelemetryDataManager();

            int rawL = dataManager.getBatteryLvLRawAdc();
            double voltL = dataManager.getBatteryLvLVoltage();

            int rawH = dataManager.getBatteryLvHRawAdc();
            double voltH = dataManager.getBatteryLvHVoltage();

            if (dataManager.isBatteryStep1Done()) {
                XYChart.Data<Number, Number> point0 = new XYChart.Data<>(rawL, voltL);
                calibrationSeries.getData().add(point0);
            }

            if (dataManager.isBatteryStep2Done()) {
                XYChart.Data<Number, Number> point1 = new XYChart.Data<>(rawH, voltH);
                calibrationSeries.getData().add(point1);
            }

            if (dataManager.isBatteryStep1Done() && dataManager.isBatteryStep2Done()) {
                double slope = (voltH - voltL) / (rawH - rawL);
                double intercept = voltL - (slope * rawL);

                String equation = String.format("V = %.6f * RAW + %.4f", slope, intercept);
                calibrationSeries.setName(equation);
            }
        });
    }

    private void updatePortList(ComboBox<SerialPorts> comboBox) {
        comboBox.getItems().clear();

        List<SerialPorts> ports = SerialManager.getSerialPortNames();

        if (ports.isEmpty()) {
            comboBox.setPromptText("Nenhuma porta encontrada");
        } else {
            comboBox.getItems().addAll(ports);
            comboBox.getSelectionModel().selectFirst();
        }
    }
}