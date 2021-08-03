package com.bajins.deltaspackfx.common;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 加载loading遮罩
 */
public class ProgressFrom {

    private Stage ownerStage;
    private Stage dialogStage;
    private ProgressIndicator progressIndicator;

    public ProgressFrom(Stage primaryStage) {
        ownerStage = primaryStage;

        dialogStage = new Stage();
        progressIndicator = new ProgressIndicator();
        // 窗口父子关系
        dialogStage.initOwner(ownerStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setWidth(ownerStage.getWidth()); // 同步父窗口宽度
        dialogStage.setHeight(ownerStage.getHeight()); // 同步父窗口高度
        dialogStage.setOpacity(0.9); // 透明度
        // progress bar
        Label label = new Label("请稍后...");
        label.setFont(Font.font("Arial", FontWeight.BLACK, 20)); // 设置字体
        label.setTextFill(Color.BLUE);
        progressIndicator.setProgress(-1F);

        VBox vBox = new VBox();
        //vBox.setPrefSize(ownerStage.getWidth(), ownerStage.getHeight());
        vBox.setSpacing(20); // 各个子节点间距
        //vBox.setBackground(Background.EMPTY);
        vBox.setStyle("-fx-border-radius:8px;-fx-fill:#409eff;");
        //vBox.setStyle("-fx-background-color:rgba(0,0,0,0.8);");
        vBox.getChildren().addAll(progressIndicator, label);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);
        //scene.setFill(Color.BLUE);
        dialogStage.setScene(scene);
    }

    /**
     * 激活加载层
     */
    public void activateProgressBar() {
        dialogStage.setX(ownerStage.getX()); // 更新坐标位置
        dialogStage.setY(ownerStage.getY());
        dialogStage.show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    /**
     * 取消加载层
     */
    public void cancelProgressBar() {
        dialogStage.close();
    }
}