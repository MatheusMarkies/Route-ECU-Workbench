package com.brasens.layout.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TelemetryCard extends AnchorPane {

    private VBox cardStruct;
    private Label cardTitleLabel;
    private AnchorPane contentAnchorPane;
    private String backgroundColor;

    public TelemetryCard(String title, String backgroundColor) {
        this.backgroundColor = backgroundColor;
        
        getStyleClass().add("card");
        setPrefHeight(200);
        setPrefWidth(400);
        setMinHeight(150);
        setMinWidth(300);
        
        setStyle(
            "-fx-background-color: " + backgroundColor + "; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-color: #3a3a3a; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 8px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 2);"
        );

        HBox.setHgrow(this, Priority.ALWAYS);
        VBox.setVgrow(this, Priority.ALWAYS);

        cardStruct = new VBox(10);
        cardStruct.setPadding(new Insets(15));
        
        AnchorPane.setBottomAnchor(cardStruct, 0.0);
        AnchorPane.setLeftAnchor(cardStruct, 0.0);
        AnchorPane.setRightAnchor(cardStruct, 0.0);
        AnchorPane.setTopAnchor(cardStruct, 0.0);

        // Header com título
        HBox cardHeaderHBox = new HBox();
        cardHeaderHBox.setAlignment(Pos.CENTER_LEFT);
        
        cardTitleLabel = new Label(title);
        cardTitleLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-padding: 0 0 5 0;"
        );
        
        cardHeaderHBox.getChildren().add(cardTitleLabel);

        // Separador
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #3a3a3a;");

        // Área de conteúdo
        contentAnchorPane = new AnchorPane();
        VBox.setVgrow(contentAnchorPane, Priority.ALWAYS);

        cardStruct.getChildren().addAll(
            cardHeaderHBox,
            separator,
            contentAnchorPane
        );

        getChildren().add(cardStruct);
    }

    public TelemetryCard(String title) {
        this(title, "#2a2a2a");
    }
}

