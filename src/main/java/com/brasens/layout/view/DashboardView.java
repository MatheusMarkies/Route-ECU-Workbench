package com.brasens.layout.view;

import com.brasens.NetworkManager;
import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.components.CustomButton;
import com.brasens.layout.controller.DashboardController;
import com.brasens.objects.SerialPorts;
import com.brasens.serialport.SerialManager;
import com.brasens.layout.utils.Page;
import com.brasens.serialport.SerialRunnable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
            System.out.println("Iniciando Teste de Injetores...");
            if(getSerialRunnable().isConnected())
                getSerialRunnable().sendCommand("AT+INJTEST");
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
            System.out.println("Iniciando Teste de Ignição...");
            if(getSerialRunnable().isConnected())
                getSerialRunnable().sendCommand("AT+IGNTEST");
        });

        contentAnchorPane.getChildren().addAll(connectButton, injectorTestButton, ignitionTestButton);
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