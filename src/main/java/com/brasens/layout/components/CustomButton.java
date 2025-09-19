package com.brasens.layout.components;

import com.brasens.layout.LayoutSizeManager;
import com.brasens.utilities.math.Interpolation;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomButton extends AnchorPane {

    private ImageView buttonLabelImageView = new ImageView();
    private Label buttonLabel = new Label();

    public CustomButton(String text, Image image, String style, int imageSize){
        HBox buttonLegendHBox = new HBox();
        buttonLegendHBox.setAlignment(Pos.CENTER);

        buttonLabelImageView = new ImageView(image);
        buttonLabelImageView.setFitHeight(imageSize);
        buttonLabelImageView.setFitWidth(imageSize);
        buttonLabelImageView.setPreserveRatio(true);

        buttonLabel = new Label(text);

        HBox.setMargin(buttonLabelImageView, LayoutSizeManager.getResizedInsert(2.0,0.0,2.0,0.0));
        HBox.setMargin(buttonLabel, LayoutSizeManager.getResizedInsert(2.0,0.0,2.0,5.0));

        buttonLegendHBox.getChildren().addAll(buttonLabelImageView, buttonLabel);

        AnchorPane.setBottomAnchor(buttonLegendHBox, 0.0);
        AnchorPane.setLeftAnchor(buttonLegendHBox, 0.0);
        AnchorPane.setRightAnchor(buttonLegendHBox, 0.0);
        AnchorPane.setTopAnchor(buttonLegendHBox, 0.0);

        this.setStyle(style);
        this.setCursor(Cursor.HAND);

        this.getChildren().addAll(buttonLegendHBox);
    }

    public void setAnimation(Color defaultColor, Color actionColor, float timer){
        this.setOnMouseEntered(event -> {
            final Animation animation = new Transition() {
                {
                    setCycleDuration(Duration.millis(timer));
                    setInterpolator(Interpolator.EASE_OUT);
                }

                @Override
                protected void interpolate(double frac) {
                    Color backgroundColor = Interpolation.lerpColorFX(defaultColor, actionColor, (float) frac);
                    setStyle("-fx-background-color: "+ "rgba("
                            + (int) (backgroundColor.getRed() * 255) + ","
                            + (int) (backgroundColor.getGreen() * 255) + ","
                            + (int) (backgroundColor.getBlue() * 255) + ","
                            + backgroundColor.getOpacity() + "); -fx-background-radius: 6px;-fx-border-radius: 6px;");
                }
            };
            animation.play();
        });

        this.setOnMouseExited(event -> {
            final Animation animation = new Transition() {
                {
                    setCycleDuration(Duration.millis(timer));
                    setInterpolator(Interpolator.EASE_OUT);
                }

                @Override
                protected void interpolate(double frac) {
                    Color backgroundColor = Interpolation.lerpColorFX(actionColor, defaultColor, (float) frac);
                    setStyle("-fx-background-color: "+ "rgba("
                            + (int) (backgroundColor.getRed() * 255) + ","
                            + (int) (backgroundColor.getGreen() * 255) + ","
                            + (int) (backgroundColor.getBlue() * 255) + ","
                            + backgroundColor.getOpacity() + "); -fx-background-radius: 6px;-fx-border-radius: 6px;");
                }
            };
            animation.play();
        });
    }

}
