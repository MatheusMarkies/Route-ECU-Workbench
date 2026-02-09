// ============================================================================
// OPÇÃO ALTERNATIVA: Criar TelemetryCard extendendo Card
// Isso permite customizar especificamente para telemetria sem modificar Card
// ============================================================================

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
        
        // Estilo específico para tema escuro
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

    // Construtor com cor padrão
    public TelemetryCard(String title) {
        this(title, "#2a2a2a");
    }
}


// ============================================================================
// COMO USAR O TelemetryCard
// ============================================================================

/*
// Exemplo de uso no DashboardView:

private TelemetryCard createEnginePanel() {
    TelemetryCard card = new TelemetryCard("MOTOR", "#1a1a2e");
    
    VBox content = new VBox(8);
    content.setPadding(new Insets(10));
    
    Label titleCrank = createSubtitle("Virabrequim");
    crankshaftAngleLabel = createValueLabel("Ângulo: --°");
    crankshaftVelocityLabel = createValueLabel("Velocidade: -- °/s");
    
    content.getChildren().addAll(titleCrank, crankshaftAngleLabel, crankshaftVelocityLabel);
    
    AnchorPane contentPane = card.getContentAnchorPane();
    AnchorPane.setTopAnchor(content, 0.0);
    AnchorPane.setBottomAnchor(content, 0.0);
    AnchorPane.setLeftAnchor(content, 0.0);
    AnchorPane.setRightAnchor(content, 0.0);
    contentPane.getChildren().add(content);
    
    return card;
}

// Cores sugeridas para diferentes tipos:
// ENGINE:     #1a1a2e (azul escuro)
// VR SENSORS: #1a2e1a (verde escuro)
// BATTERY:    #2e1a1a (vermelho escuro)
// CYCLE:      #2e1a2e (roxo escuro)
// ADC:        #1a2e2e (ciano escuro)
// PADRÃO:     #2a2a2a (cinza escuro)
*/


// ============================================================================
// ADICIONAR CSS ESPECÍFICO PARA TelemetryCard
// ============================================================================

/*
Adicionar ao DashboardCSS.css:

.telemetry-card {
    -fx-background-radius: 8px;
    -fx-border-radius: 8px;
    -fx-border-width: 1px;
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 2);
}

.telemetry-card:hover {
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 12, 0, 0, 3);
    -fx-border-color: #4a4a4a;
}

.telemetry-card-engine {
    -fx-background-color: #1a1a2e;
    -fx-border-color: #2a2a4e;
}

.telemetry-card-sensors {
    -fx-background-color: #1a2e1a;
    -fx-border-color: #2a4e2a;
}

.telemetry-card-battery {
    -fx-background-color: #2e1a1a;
    -fx-border-color: #4e2a2a;
}

.telemetry-card-cycle {
    -fx-background-color: #2e1a2e;
    -fx-border-color: #4e2a4e;
}

.telemetry-card-adc {
    -fx-background-color: #1a2e2e;
    -fx-border-color: #2a4e4e;
}

.telemetry-card-default {
    -fx-background-color: #2a2a2a;
    -fx-border-color: #3a3a3a;
}
*/
