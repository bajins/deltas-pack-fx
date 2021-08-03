package com.bajins.deltaspackfx.common;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public class WindowAlert {

    public static boolean answer;

    /**
     * @param title 标题
     * @param msg   消息
     * @param owner 所有者
     */
    public static boolean display(String title, String msg, Window owner) {
        // 创建舞台
        Stage stage = new Stage();

        // 阻止事件传递到任何其他应用程序窗口
        stage.initModality(Modality.APPLICATION_MODAL); // 当指定了所有者时,没有任务栏独立窗口
        stage.initOwner(owner); // 指定所有者
        stage.setTitle(title);

        Label label = new Label(msg);
        label.setFont(Font.font("Arial", FontPosture.ITALIC, 20)); // 设置字体

        // 创建控件
        Button buttonYes = new Button("是");
        buttonYes.setOnMouseClicked(event -> {
            answer = true;
            stage.close();
        });
        /*buttonYes.setLayoutY(16.0);
        buttonYes.setLayoutX(17.0);*/
        buttonYes.setFont(Font.font("Arial", FontPosture.ITALIC, 16)); // 设置字体

        Button buttonNo = new Button("否");
        buttonNo.setOnMouseClicked(event -> {
            answer = false;
            stage.close();
        });
        /*buttonNo.setLayoutY(16.0);
        buttonNo.setLayoutX(17.0);*/
        buttonNo.setFont(Font.font("Arial", FontPosture.ITALIC, 16)); // 设置字体

        // 子元素（节点）位置
        AnchorPane.setTopAnchor(label, 25.0);
        AnchorPane.setLeftAnchor(label, 50.0);

        AnchorPane.setTopAnchor(buttonNo, 80.0);
        AnchorPane.setRightAnchor(buttonNo, 30.0);

        AnchorPane.setTopAnchor(buttonYes, 80.0);
        AnchorPane.setLeftAnchor(buttonYes, 30.0);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(label, buttonYes, buttonNo);
        /*anchorPane.setPrefWidth(500.0);
        anchorPane.setPrefHeight(500.0);*/

        // 创建布局
        /*VBox vBox = new VBox();
        vBox.getChildren().addAll(label, buttonYes, buttonNo);
        vBox.setAlignment(Pos.CENTER); // 布局居中显示*/

        // 创建场景
        Scene scene = new Scene(anchorPane, 250, 150);

        // 显示舞台
        stage.setScene(scene);
        // stage.show();
        stage.showAndWait();  // 等待窗体关闭才继续

        // 窗体返回值
        return answer;
    }

    public static boolean display(String title, String msg) {
        return display(title, msg, null);
    }
}
