package com.brasens.layout.view;


import com.brasens.Config;
import com.brasens.NetworkManager;
import com.brasens.Workbench;
import com.brasens.layout.ApplicationWindow;
import com.brasens.layout.LayoutSizeManager;
import com.brasens.layout.components.Card;
import com.brasens.layout.components.MinimizedCard;
import com.brasens.layout.components.charts.CustomBarChart;
import com.brasens.layout.controller.DashboardController;
import com.brasens.utilities.common.enums.PriorityState;
import com.brasens.utilities.common.enums.WorkOrderState;
import com.brasens.utils.Page;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
