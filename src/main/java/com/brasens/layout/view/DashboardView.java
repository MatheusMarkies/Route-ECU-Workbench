package com.brasens.layout.view;

import com.brasens.NetworkManager;
import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.components.CustomButton;
import com.brasens.layout.controller.DashboardController;
import com.brasens.objects.*;
import com.brasens.serialport.SerialManager;
import com.brasens.layout.utils.Page;
import com.brasens.serialport.SerialRunnable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
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

    private Label crankshaftAngleLabel;
    private Label crankshaftVelocityLabel;
    private Label crankshaftAccLabel;
    private Label camshaftAngleLabel;
    private Label camshaftVelocityLabel;
    private Label camshaftAccLabel;
    private Label ckpRpmLabel;
    private Label ckpFrequencyLabel;
    private Label ckpPulsesLabel;
    private Label cmpRpmLabel;
    private Label cmpFrequencyLabel;
    private Label cmpPulsesLabel;
    private Label piston1Label, piston2Label, piston3Label, piston4Label;
    private Label batteryVoltageLabel;
    private Label batteryRawAdcLabel;
    private VBox adcU16Panel;
    private VBox adcU17Panel;

    private Label cyl0DwellLabel, cyl0SparkLabel;
    private Label cyl1DwellLabel, cyl1SparkLabel;
    private Label cyl2DwellLabel, cyl2SparkLabel;
    private Label cyl3DwellLabel, cyl3SparkLabel;

    private Timeline telemetryUpdateTimeline;

    public DashboardView(ApplicationWindow applicationWindow, NetworkManager networkManager) {
        super(applicationWindow, networkManager, "/mspm/pages/DashboardCSS.css");
        this.controller = new DashboardController(applicationWindow);
        createView();
    }

    public void createView() {
        getStyleClass().add("body");
        setMinHeight(1100);

        // Container principal com scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.getStyleClass().add("body");

        HBox buttonBar = createControlButtons();

        GridPane telemetryGrid = createTelemetryGrid();

        mainContent.getChildren().addAll(buttonBar, telemetryGrid);
        scrollPane.setContent(mainContent);

        getChildren().add(scrollPane);

        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setTopAnchor(scrollPane, 0.0);
    }

    private HBox createControlButtons() {
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER_LEFT);

        String buttonStyle = "-fx-background-color: #ffffff; -fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);";
        double buttonWidth = 220;
        double buttonHeight = 55;

        // Connect Button
        Image connectIcon = new Image(getClass().getResourceAsStream("/mspm/icons/db89.png"));
        connectButton = new CustomButton("Conectar", connectIcon, buttonStyle, 28);
        connectButton.setPrefWidth(buttonWidth);
        connectButton.setPrefHeight(buttonHeight);
        //connectButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);
        connectButton.setOnMouseClicked(event -> openDeviceSelectionPopup());

        // Injector Test Button
        Image injectorIcon = new Image(getClass().getResourceAsStream("/mspm/icons/fuel.png"));
        injectorTestButton = new CustomButton("Teste Injetores", injectorIcon, buttonStyle, 28);
        injectorTestButton.setPrefWidth(buttonWidth);
        injectorTestButton.setPrefHeight(buttonHeight);
        //injectorTestButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);
        injectorTestButton.setOnMouseClicked(event -> {
            if(getSerialRunnable().isConnected())
                getSerialRunnable().sendCommand(new SerialCommand("AT+INJTEST", "OK", ()->{
                    getApplicationWindow().getTelemetryDataManager().setInInjectorTest(
                            !getApplicationWindow().getTelemetryDataManager().isInInjectorTest());
                }, 100));
        });

        // Ignition Test Button
        Image ignitionIcon = new Image(getClass().getResourceAsStream("/mspm/icons/plug.png"));
        ignitionTestButton = new CustomButton("Teste Ignição", ignitionIcon, buttonStyle, 28);
        ignitionTestButton.setPrefWidth(buttonWidth);
        ignitionTestButton.setPrefHeight(buttonHeight);
        //ignitionTestButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);
        ignitionTestButton.setOnMouseClicked(event -> {
            if(getSerialRunnable().isConnected())
                getSerialRunnable().sendCommand(new SerialCommand("AT+IGNTEST", "OK", ()->{
                    getApplicationWindow().getTelemetryDataManager().setInIgnitionTest(
                            !getApplicationWindow().getTelemetryDataManager().isInIgnitionTest());
                }, 100));
        });

        // Battery Calibration Button
        Image batteryIcon = new Image(getClass().getResourceAsStream("/mspm/icons/car-battery.png"));
        batteryCalibrationButton = new CustomButton("Calibrar Bateria", batteryIcon, buttonStyle, 28);
        batteryCalibrationButton.setPrefWidth(buttonWidth);
        batteryCalibrationButton.setPrefHeight(buttonHeight);
       // batteryCalibrationButton.setAnimation(Color.web("#ffffff"), Color.web("#f0f0f0"), 200);
        batteryCalibrationButton.setOnMouseClicked(event -> openBatteryCalibrationPopup());

        buttonBar.getChildren().addAll(connectButton, injectorTestButton, ignitionTestButton, batteryCalibrationButton);
        return buttonBar;
    }

    private GridPane createTelemetryGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(0));

        // Configurar colunas para serem responsivas
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33.33);
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33.33);
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(33.33);
        col3.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2, col3);

        // LINHA 0: ENGINE, VR SENSORS, BATTERY
        VBox enginePanel = createEnginePanel();
        enginePanel.setMaxWidth(Double.MAX_VALUE);
        grid.add(enginePanel, 0, 0);

        VBox vrPanel = createVRSensorsPanel();
        vrPanel.setMaxWidth(Double.MAX_VALUE);
        grid.add(vrPanel, 1, 0);

        VBox batteryPanel = createBatteryPanel();
        batteryPanel.setMaxWidth(Double.MAX_VALUE);
        grid.add(batteryPanel, 2, 0);

        // LINHA 1: CYCLE, ADC U16, ADC U17
        VBox cyclePanel = createCyclePanel();
        cyclePanel.setMaxWidth(Double.MAX_VALUE);
        grid.add(cyclePanel, 0, 1);

        adcU16Panel = createADCPanel("ADC U16");
        adcU16Panel.setMaxWidth(Double.MAX_VALUE);
        grid.add(adcU16Panel, 1, 1);

        adcU17Panel = createADCPanel("ADC U17");
        adcU17Panel.setMaxWidth(Double.MAX_VALUE);
        grid.add(adcU17Panel, 2, 1);

        return grid;
    }

    private VBox createEnginePanel() {
        VBox panel = createTelemetryPanel("MOTOR", "#ffffff");

        Label titleCrank = createSubtitle("Virabrequim");
        crankshaftAngleLabel = createValueLabel("Ângulo: --°");
        crankshaftVelocityLabel = createValueLabel("Velocidade: -- °/s");
        crankshaftAccLabel = createValueLabel("Aceleração: -- °/s^2");

        Label titleCam = createSubtitle("Comando");
        camshaftAngleLabel = createValueLabel("Ângulo: --°");
        camshaftVelocityLabel = createValueLabel("Velocidade: -- °/s");
        camshaftAccLabel = createValueLabel("Aceleração: -- °/s^2");

        Label titlePistons = createSubtitle("Pistões");
        HBox pistonsBox = new HBox(10);
        piston1Label = createPistonLabel("P1");
        piston2Label = createPistonLabel("P2");
        piston3Label = createPistonLabel("P3");
        piston4Label = createPistonLabel("P4");
        pistonsBox.getChildren().addAll(piston1Label, piston2Label, piston3Label, piston4Label);

        panel.getChildren().addAll(
                titleCrank, crankshaftAngleLabel, crankshaftVelocityLabel, crankshaftAccLabel,
                createSpacer(),
                titleCam, camshaftAngleLabel, camshaftVelocityLabel, camshaftAccLabel,
                createSpacer(),
                titlePistons, pistonsBox
        );

        return panel;
    }

    private VBox createVRSensorsPanel() {
        VBox panel = createTelemetryPanel("SENSORES VR", "#ffffff");

        Label titleCKP = createSubtitle("CKP (Crankshaft)");
        ckpRpmLabel = createValueLabel("RPM: --");
        ckpFrequencyLabel = createValueLabel("Frequência: -- Hz");
        ckpPulsesLabel = createValueLabel("Pulsos: --");

        Label titleCMP = createSubtitle("CMP (Camshaft)");
        cmpRpmLabel = createValueLabel("RPM: --");
        cmpFrequencyLabel = createValueLabel("Frequência: -- Hz");
        cmpPulsesLabel = createValueLabel("Pulsos: --");

        panel.getChildren().addAll(
                titleCKP, ckpRpmLabel, ckpFrequencyLabel, ckpPulsesLabel,
                createSpacer(),
                titleCMP, cmpRpmLabel, cmpFrequencyLabel, cmpPulsesLabel
        );

        return panel;
    }

    private VBox createBatteryPanel() {
        VBox panel = createTelemetryPanel("BATERIA", "#ffffff");

        batteryVoltageLabel = createValueLabel("Tensão: -- V", 24);
        batteryRawAdcLabel = createValueLabel("ADC Raw: --");

        panel.getChildren().addAll(batteryVoltageLabel, batteryRawAdcLabel);

        return panel;
    }

    private VBox createADCPanel(String title) {
        VBox panel = createTelemetryPanel(title, "#ffffff");
        panel.setPrefWidth(400);

        Label noDataLabel = createValueLabel("Aguardando dados...");
        noDataLabel.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 13px; -fx-font-style: italic;");
        panel.getChildren().add(noDataLabel);

        return panel;
    }

    private VBox createTelemetryPanel(String title, String accentColor) {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(20));
        panel.setStyle(
                "-fx-background-color: #202020;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: " + accentColor + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 12px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );
        panel.setMinWidth(280);
        panel.setMinHeight(220);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: " + accentColor + ";" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Consolas', 'Monaco', monospace;" +
                        "-fx-letter-spacing: 2px;"
        );

        panel.getChildren().add(titleLabel);
        return panel;
    }

    private Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: #00d9ff;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Segoe UI', sans-serif;"
        );
        return label;
    }

    private Label createValueLabel(String text) {
        return createValueLabel(text, 14);
    }

    private Label createValueLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: #e0e0e0;" +
                        "-fx-font-size: " + fontSize + "px;" +
                        "-fx-font-family: 'Consolas', 'Monaco', monospace;"
        );
        return label;
    }

    private Label createPistonLabel(String text) {
        Label label = new Label(text);
        label.setPrefSize(55, 55);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-background-color: #3a3a52;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: #00ff88;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Consolas', monospace;" +
                        "-fx-border-color: #00ff88;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 8px;"
        );
        return label;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        spacer.setPrefHeight(8);
        return spacer;
    }

    private void startTelemetryUpdates() {

    }

    public void updateEngineDisplay(EngineTelemetry telemetry) {
        if (telemetry.getEngine() == null) return;

        EngineTelemetry.EngineInfo engine = telemetry.getEngine();

        if (engine.getCrankshaft() != null) {
            crankshaftAngleLabel.setText(String.format("Ângulo: %.2f°", engine.getCrankshaft().getAngle()));
            crankshaftVelocityLabel.setText(String.format("Velocidade: %.2f °/s",
                    engine.getCrankshaft().getAngularvelocity()));
            crankshaftAccLabel.setText(String.format("Aceleração: %.4f °/s^2",
                    engine.getCrankshaft().getAngularacc()));
        }

        if (engine.getCamshaft() != null) {
            camshaftAngleLabel.setText(String.format("Ângulo: %.2f°", engine.getCamshaft().getAngle()));
            camshaftVelocityLabel.setText(String.format("Velocidade: %.2f °/s",
                    engine.getCamshaft().getAngularvelocity()));
            camshaftAccLabel.setText(String.format("Aceleração: %.4f °/s^2",
                    engine.getCamshaft().getAngularacc()));
        }

        updatePistonLabel(piston1Label, engine.getPistonOne());
        updatePistonLabel(piston2Label, engine.getPistonTwo());
        updatePistonLabel(piston3Label, engine.getPistonThree());
        updatePistonLabel(piston4Label, engine.getPistonFour());
    }

    private void updatePistonLabel(Label label, EngineTelemetry.EngineInfo.PistonPhase phase) {
        if (phase == null) {
            label.setText("--");
            return;
        }

        String text;
        String color;

        switch (phase) {
            case POWER:
                text = "PWR";
                color = "#ff4444";
                break;
            case EXHAUST:
                text = "EXH";
                color = "#ffaa00";
                break;
            case COMPRESSION:
                text = "CMP";
                color = "#00aaff";
                break;
            case INTAKE:
                text = "INT";
                color = "#00ff88";
                break;
            default:
                text = "--";
                color = "#666666";
        }

        label.setText(text);
        label.setStyle(
                "-fx-background-color: #3a3a52;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-family: 'Consolas', monospace;" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 8px;"
        );
    }

    public void updateVRDisplay(VRTelemetry telemetry) {
        if (telemetry.getCkp() != null) {
            VRTelemetry.SensorData ckp = telemetry.getCkp();
            ckpRpmLabel.setText(String.format("RPM: %.0f", ckp.getRpm()));
            ckpFrequencyLabel.setText(String.format("Frequência: %.2f Hz", ckp.getFrequence()));
            ckpPulsesLabel.setText(String.format("Pulsos: %d", ckp.getPulses()));
        }

        if (telemetry.getCmp() != null) {
            VRTelemetry.SensorData cmp = telemetry.getCmp();
            cmpRpmLabel.setText(String.format("RPM: %.0f", cmp.getRpm()));
            cmpFrequencyLabel.setText(String.format("Frequência: %.2f Hz", cmp.getFrequence()));
            cmpPulsesLabel.setText(String.format("Pulsos: %d", cmp.getPulses()));
        }
    }

    public void updateBatteryDisplay(BatteryTelemetry telemetry) {
        if (telemetry.getBattery() != null) {
            batteryVoltageLabel.setText(String.format("Tensão: %.2f V",
                    telemetry.getBattery().getVoltage()));
            batteryRawAdcLabel.setText(String.format("ADC Raw: %d",
                    telemetry.getBattery().getRawAdc()));
        }
    }

    public void updateADCDisplay(ADCTelemetry telemetry) {
        if (telemetry.getAdcU16() != null) {
            updateADCPanelContent(adcU16Panel, telemetry.getAdcU16(), "ADC U16");
        }

        if (telemetry.getAdcU17() != null) {
            updateADCPanelContent(adcU17Panel, telemetry.getAdcU17(), "ADC U17");
        }
    }

    private VBox createCyclePanel() {
        VBox panel = createTelemetryPanel("CICLO DE IGNIÇÃO", "#ffffff");

        // Grid para organizar os cilindros em 2 colunas
        GridPane cylindersGrid = new GridPane();
        cylindersGrid.setHgap(15);
        cylindersGrid.setVgap(10);
        cylindersGrid.setPadding(new Insets(5, 0, 0, 0));

        // Configurar colunas do grid interno
        ColumnConstraints colCyl1 = new ColumnConstraints();
        colCyl1.setPercentWidth(50);
        colCyl1.setHgrow(Priority.ALWAYS);

        ColumnConstraints colCyl2 = new ColumnConstraints();
        colCyl2.setPercentWidth(50);
        colCyl2.setHgrow(Priority.ALWAYS);

        cylindersGrid.getColumnConstraints().addAll(colCyl1, colCyl2);

        // Coluna 1: Cilindros 0 e 1
        VBox cyl0Box = createCylinderBox("Cilindro 0");
        cyl0DwellLabel = createValueLabel("Dwell: --°");
        cyl0SparkLabel = createValueLabel("Spark: --°");
        cyl0Box.getChildren().addAll(cyl0DwellLabel, cyl0SparkLabel);

        VBox cyl1Box = createCylinderBox("Cilindro 1");
        cyl1DwellLabel = createValueLabel("Dwell: --°");
        cyl1SparkLabel = createValueLabel("Spark: --°");
        cyl1Box.getChildren().addAll(cyl1DwellLabel, cyl1SparkLabel);

        // Coluna 2: Cilindros 2 e 3
        VBox cyl2Box = createCylinderBox("Cilindro 2");
        cyl2DwellLabel = createValueLabel("Dwell: --°");
        cyl2SparkLabel = createValueLabel("Spark: --°");
        cyl2Box.getChildren().addAll(cyl2DwellLabel, cyl2SparkLabel);

        VBox cyl3Box = createCylinderBox("Cilindro 3");
        cyl3DwellLabel = createValueLabel("Dwell: --°");
        cyl3SparkLabel = createValueLabel("Spark: --°");
        cyl3Box.getChildren().addAll(cyl3DwellLabel, cyl3SparkLabel);

        // Adicionar ao grid (2x2)
        cylindersGrid.add(cyl0Box, 0, 0);
        cylindersGrid.add(cyl2Box, 1, 0);
        cylindersGrid.add(cyl1Box, 0, 1);
        cylindersGrid.add(cyl3Box, 1, 1);

        panel.getChildren().add(cylindersGrid);

        return panel;
    }

    private VBox createCylinderBox(String title) {
        VBox box = new VBox(3);
        box.setMaxWidth(Double.MAX_VALUE);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-padding: 0 0 5 0;"
        );
        box.getChildren().add(titleLabel);
        return box;
    }

    public void updateCycleDisplay(CycleTelemetry data) {
        javafx.application.Platform.runLater(() -> {
            if (data != null && data.getCycle() != null) {
                CycleTelemetry.CycleInfo cycle = data.getCycle();

                // Cilindro 0
                if (cycle.getCylinderZero() != null) {
                    cyl0DwellLabel.setText(String.format("Dwell: %.4f°",
                            cycle.getCylinderZero().getDwellAngle()));
                    cyl0SparkLabel.setText(String.format("Spark: %.4f°",
                            cycle.getCylinderZero().getSparkAngle()));
                }

                // Cilindro 1
                if (cycle.getCylinderOne() != null) {
                    cyl1DwellLabel.setText(String.format("Dwell: %.4f°",
                            cycle.getCylinderOne().getDwellAngle()));
                    cyl1SparkLabel.setText(String.format("Spark: %.4f°",
                            cycle.getCylinderOne().getSparkAngle()));
                }

                // Cilindro 2
                if (cycle.getCylinderTwo() != null) {
                    cyl2DwellLabel.setText(String.format("Dwell: %.4f°",
                            cycle.getCylinderTwo().getDwellAngle()));
                    cyl2SparkLabel.setText(String.format("Spark: %.4f°",
                            cycle.getCylinderTwo().getSparkAngle()));
                }

                // Cilindro 3
                if (cycle.getCylinderThree() != null) {
                    cyl3DwellLabel.setText(String.format("Dwell: %.4f°",
                            cycle.getCylinderThree().getDwellAngle()));
                    cyl3SparkLabel.setText(String.format("Spark: %.4f°",
                            cycle.getCylinderThree().getSparkAngle()));
                }
            }
        });
    }

    private void updateADCPanelContent(VBox panel, List<ADCTelemetry.AdcSensor> sensors, String title) {
        javafx.application.Platform.runLater(() -> {
            // Keep only the title label
            if (panel.getChildren().size() > 1) {
                panel.getChildren().subList(1, panel.getChildren().size()).clear();
            }

            if (sensors == null || sensors.isEmpty()) {
                Label noDataLabel = createValueLabel("Aguardando dados...");
                noDataLabel.setStyle("-fx-text-fill: #b0b0b0; -fx-font-size: 13px; -fx-font-style: italic;");
                panel.getChildren().add(noDataLabel);
                return;
            }

            for (ADCTelemetry.AdcSensor sensor : sensors) {
                HBox sensorRow = new HBox(15);
                sensorRow.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(sensor.getSensor());
                nameLabel.setStyle(
                        "-fx-text-fill: #00d9ff;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-min-width: 120px;"
                );

                Label voltageLabel = createValueLabel(String.format("%.3f V", sensor.getVoltage()));
                voltageLabel.setStyle(voltageLabel.getStyle() + "-fx-min-width: 80px;");

                Label rawLabel = createValueLabel(String.format("Raw: %d", sensor.getRaw()));
                rawLabel.setStyle(rawLabel.getStyle() + "-fx-text-fill: #999999; -fx-font-size: 11px;");

                sensorRow.getChildren().addAll(nameLabel, voltageLabel, rawLabel);
                panel.getChildren().add(sensorRow);
            }
        });
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
        popupLayout.setStyle("-fx-background-color: #202020;");

        Label titleLabel = new Label("Selecione a Porta COM");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

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
            SerialPorts selected = portComboBox.getValue();
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

    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> calibrationSeries;

    private void openBatteryCalibrationPopup() {
        Stage popupStage = new Stage();
        popupStage.initOwner(getApplicationWindow().getWorkbench().getStage());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Calibração da Bateria");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #202020;");

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