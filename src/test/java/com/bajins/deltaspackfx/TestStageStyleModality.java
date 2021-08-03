package com.bajins.deltaspackfx;

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 模式(StageStyle)，模态(Modality)
 */
public class TestStageStyleModality extends Application implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void start(Stage primaryStage) {
        Stage s1 = new Stage();
        s1.setTitle("s1");
        // 纯白背景和平台装饰
        s1.initStyle(StageStyle.DECORATED);
        s1.show();

        Stage s2 = new Stage();
        s2.setTitle("s2");
        // 透明背景且没有装饰
        s2.initStyle(StageStyle.TRANSPARENT);
        s2.show();

        Stage s3 = new Stage();
        s3.setTitle("s3");

        // 纯白背景，无装饰。
        s3.initStyle(StageStyle.UNDECORATED);
        s3.show();

        Stage s4 = new Stage();
        s4.setTitle("s4");

        // 纯白背景和最少平台装饰
        s4.initStyle(StageStyle.UNIFIED);
        s4.show();

        Stage s5 = new Stage();
        s5.setTitle("s5");
        // 一个统一标准的舞台
        s5.initStyle(StageStyle.UTILITY); // 没有任务栏独立窗口
        s5.show();

        Stage s6 = new Stage();
        // 阻止事件传递到任何其他应用程序窗口
        s6.initModality(Modality.APPLICATION_MODAL); // 当指定了所有者时,没有任务栏独立窗口
        s6.initOwner(s1); // 指定所有者
        s6.setTitle("s6");
        s6.show();

        Stage s7 = new Stage();
        // 阻止事件传递到所有者的窗口
        s7.initModality(Modality.WINDOW_MODAL); // 当指定了所有者时,没有任务栏独立窗口
        s7.initOwner(s1); // 指定所有者
        s7.setTitle("s7");
        s7.show();

        // 终止JavaFX应用程序
        //Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
