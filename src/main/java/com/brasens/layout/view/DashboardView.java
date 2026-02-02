package com.brasens.layout.view;

import com.brasens.NetworkManager;
import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.controller.DashboardController;
import com.brasens.utils.Page;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardView extends Page {

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

        getChildren().add(contentAnchorPane);
    }

}
