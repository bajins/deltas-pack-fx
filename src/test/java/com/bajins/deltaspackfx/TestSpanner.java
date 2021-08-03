package com.bajins.deltaspackfx;

import com.bajins.deltaspackfx.common.IntegerFilter;
import com.bajins.deltaspackfx.utils.FxUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TestSpanner extends Application implements Initializable {

    @FXML
    private Spinner days;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        days.getEditor().setTextFormatter(new TextFormatter<>(new IntegerFilter()));
        days.setOnKeyReleased(event -> { // 监听键盘按钮松开
            // 数字按键、删除
            if (event.getCode().isDigitKey() || event.getCode().equals(KeyCode.BACK_SPACE) || event.getCode().equals(KeyCode.DELETE)) {
                days.getEditor().commitValue();
                FxUtils.commitEditorText(days);
            }
        });
        days.focusedProperty().addListener((s, ov, nv) -> { // 监听光标（焦点）离开
            if (nv) {
                return;
            }
            days.getEditor().commitValue();
            FxUtils.commitEditorText(days);
        });
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 创建布局控件: 这里的root从FXML文件中加载进行初始化，这里FXMLLoader类用于加载FXML文件
        AnchorPane load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/spanner.fxml")));

        // 创建场景，并将场景添加到窗口
        Scene scene = new Scene(load);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX自定义弹窗测试");
        primaryStage.setMaximized(false); // 最大化
        primaryStage.setResizable(true);// 窗体缩放（默认为true）
        primaryStage.setMinWidth(200);
        primaryStage.setMinHeight(200);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
