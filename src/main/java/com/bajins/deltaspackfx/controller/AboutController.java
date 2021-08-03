package com.bajins.deltaspackfx.controller;

import com.bajins.deltaspackfx.Main;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutController {

    public AboutController() {

        Label pageHomeLabel = new Label("主页：");
        Hyperlink pageHomeLink = new Hyperlink("https://www.bajins.com");
        pageHomeLink.setOnAction(event -> browse(pageHomeLink.getText()));

        Label projectLabel = new Label("项目首页：");
        Hyperlink projectLink = new Hyperlink("https://github.com/bajins/deltas-pack-fx");
        projectLink.setOnAction(event -> browse(projectLink.getText()));
        Label issueLabel = new Label("问题反馈：");
        Hyperlink issueLink = new Hyperlink("https://github.com/bajins/deltas-pack-fx/issues");
        issueLink.setOnAction(event -> browse(issueLink.getText()));

        //GridPane.setVgrow(textArea, Priority.ALWAYS);
        //GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane gridPane = new GridPane();
        //gridPane.setVgap(400);
        gridPane.setStyle("-fx-padding: 50px");
        gridPane.add(pageHomeLabel, 0, 0); // 添加并设置位置
        gridPane.add(pageHomeLink, 2, 0);
        gridPane.add(projectLabel, 0, 1);
        gridPane.add(projectLink, 2, 1);
        gridPane.add(issueLabel, 0, 2);
        gridPane.add(issueLink, 2, 2);

        Insets insets = new Insets(10, 0, 10, 0);
        GridPane.setMargin(pageHomeLink, insets); // 间距
        GridPane.setMargin(projectLink, insets);
        GridPane.setMargin(issueLink, insets);

        Scene scene = new Scene(gridPane);
        //scene.setFill(Color.BLUE);

        Stage stage = new Stage();
        stage.setScene(scene);
        // 窗口父子关系
        stage.initOwner(Main.topStage);
        stage.setTitle("About");
        //stage.initStyle(StageStyle.UNDECORATED);
        //stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setWidth(Main.topStage.getWidth()); // 同步父窗口宽度
        //stage.setHeight(Main.topStage.getHeight()); // 同步父窗口高度
        stage.setOpacity(1); // 透明度
        //stage.setX(Main.topStage.getX()); // 更新坐标位置
        //stage.setY(Main.topStage.getY());
        stage.show();
    }

    /**
     * 打开默认浏览器并访问链接
     *
     * @param link
     */
    public void browse(String link) {
        try {
            java.awt.Desktop.getDesktop().browse(new URI(link));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
